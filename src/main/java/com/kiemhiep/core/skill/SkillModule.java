package com.kiemhiep.core.skill;

import com.kiemhiep.Kiemhiep;
import com.kiemhiep.KiemhiepBootstrap;
import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.api.service.SkillService;
import com.kiemhiep.core.command.SkillCommands;
import com.kiemhiep.core.database.JdbcPlayerRepository;
import com.kiemhiep.core.database.JdbcSkillDefinitionRepository;
import com.kiemhiep.core.skill.impl.FireballSkill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Skill module: skill = item, effect interfaces, SkillEngine, commands, UseItemCallback.
 */
public class SkillModule implements KiemHiepModule {

    private static final String MOD_ID = "kiemhiep";
    private static final int TICK_INTERVAL = 5;

    private static volatile SkillService skillServiceHolder;
    private static volatile SkillManager skillManagerHolder;

    private boolean enabled;
    private SkillDefinitionRepository definitionRepository;
    private SkillServiceImpl skillServiceImpl;
    private CooldownManager cooldownManager;
    private CastStateManager castStateManager;
    private JdbcPlayerRepository playerRepository;
    private long serverTickCounter;

    @Override
    public String getId() {
        return "skill";
    }

    @Override
    public String getName() {
        return "Skill";
    }

    @Override
    public List<String> getDependencies() {
        return List.of("cultivation");
    }

    @Override
    public void onLoad(ModuleContext ctx) {
        Optional<DataSource> dsOpt = KiemhiepBootstrap.getDataSource();
        if (dsOpt.isEmpty()) {
            Kiemhiep.LOGGER.warn("Skill module: no DataSource, skill definitions and DB features disabled.");
            return;
        }
        DataSource ds = dsOpt.get();
        definitionRepository = new JdbcSkillDefinitionRepository(ds);
        com.kiemhiep.api.repository.SkillRepository playerSkillRepo = new com.kiemhiep.core.database.JdbcSkillRepository(ds);
        playerRepository = new JdbcPlayerRepository(ds);
        cooldownManager = new CooldownManager();
        castStateManager = new CastStateManager();
        EffectManager effectManager = new EffectManager();
        skillManagerHolder = new SkillManager(definitionRepository, cooldownManager, castStateManager, effectManager, ctx.getPlatformProvider());
        skillServiceImpl = new SkillServiceImpl(definitionRepository, playerSkillRepo, skillManagerHolder);
        skillServiceHolder = skillServiceImpl;

        SkillRegistry.register("FIREBALL", FireballSkill.INSTANCE);

        registerSkillItems();
    }

    private void registerSkillItems() {
        // Item registration: requires ResourceLocation at runtime. Seed DB with item_id kiemhiep:skill_fireball;
        // register the item in a dedicated content class or use data-driven registration.
        Kiemhiep.LOGGER.info("Skill items: register via content class or data (item_id kiemhiep:skill_fireball)");
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        enabled = true;
        if (skillServiceImpl == null) return;

        // UseItemCallback: when player uses item, resolve skill by item_id and call SkillService.useSkill.
        // Return type is TypedActionResult<ItemStack> (Minecraft 1.21); uncomment when mapping confirmed.
        // UseItemCallback.EVENT.register((player, world, hand) -> { ... });

        if (playerRepository != null) {
            SkillCommands.register(() -> skillServiceHolder, () -> playerRepository);
        }
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerTick(MinecraftServer server) {
        serverTickCounter++;
        if (serverTickCounter % TICK_INTERVAL != 0) return;
        long now = System.currentTimeMillis();
        if (skillManagerHolder != null) {
            skillManagerHolder.getCooldownManager().tick(now);
            skillManagerHolder.getCastStateManager().tick(server.getTickCount(), (playerId, entry) ->
                skillManagerHolder.onCastComplete(playerId, entry));
        }
    }

    @Override
    public void onDisable() {
        enabled = false;
        skillServiceHolder = null;
        skillManagerHolder = null;
        if (cooldownManager != null) cooldownManager.clear();
        if (castStateManager != null) castStateManager.clear();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static Optional<SkillService> getSkillService() {
        return Optional.ofNullable(skillServiceHolder);
    }
}
