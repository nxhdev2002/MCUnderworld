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
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.core.skill.impl.FireballSkill;
import com.kiemhiep.core.skill.impl.ThunderSkill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import com.kiemhiep.cultivation.CultivationModule;
import com.kiemhiep.platform.FabricEffectManager;
import com.kiemhiep.platform.FabricPlatformProvider;
import com.kiemhiep.platform.SkillItemRegistrationHelper;
import com.kiemhiep.platform.network.PlayerStatsPayload;
import com.kiemhiep.platform.network.SkillNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private InMemoryManaProvider manaProvider;

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
        EffectManager effectManager = ctx.getPlatformProvider() instanceof FabricPlatformProvider fpp
            ? new FabricEffectManager(fpp) : new EffectManager();
        this.manaProvider = new InMemoryManaProvider();
        skillManagerHolder = new SkillManager(definitionRepository, cooldownManager, castStateManager, effectManager, ctx.getPlatformProvider(), Optional.of(this.manaProvider));
        skillServiceImpl = new SkillServiceImpl(definitionRepository, playerSkillRepo, skillManagerHolder);
        skillServiceHolder = skillServiceImpl;

        SkillRegistry.register("FIREBALL", FireballSkill.INSTANCE);
        SkillRegistry.register("THUNDER", ThunderSkill.INSTANCE);

        SkillNetworking.register();
        registerSkillItems();
    }

    private void registerSkillItems() {
        if (definitionRepository == null) return;
        try {
            List<SkillDefinition> all = definitionRepository.findAll();
            Set<String> uniqueItemIds = all.stream()
                .map(SkillDefinition::itemId)
                .collect(Collectors.toSet());
            for (String itemId : uniqueItemIds) {
                if (itemId == null || !itemId.contains(":")) {
                    Kiemhiep.LOGGER.warn("Skill item_id invalid (need namespace:path): {}", itemId);
                    continue;
                }
                if (SkillItemRegistrationHelper.registerItem(itemId)) {
                    Kiemhiep.LOGGER.info("Registered skill item: {}", itemId);
                }
            }
        } catch (Exception e) {
            Kiemhiep.LOGGER.warn("Skill items: could not load definitions for registration", e);
        }
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        enabled = true;
        if (skillServiceImpl == null) return;

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            Optional<SkillService> skillServiceOpt = getSkillService();
            if (skillServiceOpt.isEmpty()) return InteractionResult.PASS;
            ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty()) return InteractionResult.PASS;
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (!(world instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
            long serverTick = serverLevel.getServer().getTickCount();
            Kiemhiep.LOGGER.debug("Item used (skill check): player={} itemId={} hand={}", player.getName().getString(), itemId, hand);
            SkillManager.UseResult result = skillServiceOpt.get().useSkill(player.getUUID(), itemId, serverTick);
            if (result == SkillManager.UseResult.SUCCESS) {
                skillServiceOpt.get().getByItemId(itemId).ifPresent(def -> {
                    if (def.consumable()) stack.shrink(1);
                });
                Kiemhiep.LOGGER.debug("Skill use success: player={} itemId={} result={}", player.getName().getString(), itemId, result);
                return InteractionResult.SUCCESS;
            }
            if (result == SkillManager.UseResult.CAST_STARTED) {
                Kiemhiep.LOGGER.debug("Skill cast started (use item): player={} itemId={}", player.getName().getString(), itemId);
                return InteractionResult.SUCCESS;
            }
            if (result != SkillManager.UseResult.SUCCESS && result != SkillManager.UseResult.CAST_STARTED) {
                Kiemhiep.LOGGER.debug("Skill use not triggered: player={} itemId={} result={}", player.getName().getString(), itemId, result);
            }
            return InteractionResult.PASS;
        });

        if (playerRepository != null) {
            SkillCommands.register(() -> skillServiceHolder, () -> playerRepository);
        }
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        registerPlayerStatsSync(ctx);
    }

    private static final int DEFAULT_MAX_MANA = 100;

    /**
     * Registers a listener that sends {@link PlayerStatsPayload} (level, mana) to the client when a player joins.
     * Stats are sent only on join; the HUD does not update during the session (e.g. after level up or mana use)
     * until the player reconnects.
     */
    private void registerPlayerStatsSync(ModuleContext ctx) {
        if (manaProvider == null) return;
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var serverPlayer = handler.getPlayer();
            if (serverPlayer == null) return;
            var cultOpt = ctx.getModuleRegistry().get("cultivation");
            if (cultOpt.isEmpty() || !(cultOpt.get() instanceof CultivationModule cm)) return;
            var cultivationService = cm.getService();
            var playerService = cm.getPlayerService();
            var player = playerService.get(serverPlayer.getUUID().toString());
            if (player.isEmpty()) return;
            int level = cultivationService.get(player.get().id()).map(c -> c.level()).orElse(1);
            int currentMana = manaProvider.getCurrentMana(serverPlayer.getUUID());
            var payload = new PlayerStatsPayload(level, currentMana, DEFAULT_MAX_MANA);
            ServerPlayNetworking.send(serverPlayer, payload);
        });
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
