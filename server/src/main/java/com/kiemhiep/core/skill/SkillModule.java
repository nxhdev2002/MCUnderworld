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
import com.kiemhiep.core.skill.impl.InfernoFistSkill;
import com.kiemhiep.core.skill.impl.SolarFlareSkill;
import com.kiemhiep.core.skill.impl.DragonBreathSkill;
import com.kiemhiep.core.skill.impl.BlazingSwordSkill;
import com.kiemhiep.core.skill.impl.PyroclasticFlowSkill;
import com.kiemhiep.core.skill.impl.GlacierSpikeSkill;
import com.kiemhiep.core.skill.impl.CryoBlastSkill;
import com.kiemhiep.core.skill.impl.ArcticWindSkill;
import com.kiemhiep.core.skill.impl.IcePrisonSkill;
import com.kiemhiep.core.skill.impl.ThunderFangSkill;
import com.kiemhiep.core.skill.impl.ElectroWaveSkill;
import com.kiemhiep.core.skill.impl.RagingThunderSkill;
import com.kiemhiep.core.skill.impl.ChainThrustSkill;
import com.kiemhiep.core.skill.impl.VajraLightningSkill;
import com.kiemhiep.core.skill.impl.StoneFistSkill;
import com.kiemhiep.core.skill.impl.MudWallSkill;
import com.kiemhiep.core.skill.impl.SeismicPulseSkill;
import com.kiemhiep.core.skill.impl.EarthGolemSkill;
import com.kiemhiep.core.skill.impl.QuakeStompSkill;
import com.kiemhiep.core.skill.impl.GaleSwordSkill;
import com.kiemhiep.core.skill.impl.SandStormSkill;
import com.kiemhiep.core.skill.impl.VacuumCutSkill;
import com.kiemhiep.core.skill.impl.TornadoSweepSkill;
import com.kiemhiep.core.skill.impl.SonicSlicerSkill;
import com.kiemhiep.core.skill.impl.BlackSparkSkill;
import com.kiemhiep.core.skill.impl.AcidRainSkill;
import com.kiemhiep.core.skill.impl.MiasmaBlastSkill;
import com.kiemhiep.core.skill.impl.TransformationSkill;
import com.kiemhiep.core.skill.impl.CursedGasSkill;
import com.kiemhiep.core.skill.impl.TimeBombSkill;
import com.kiemhiep.core.skill.impl.LightSentrySkill;
import com.kiemhiep.core.skill.impl.StarlightHealSkill;
import com.kiemhiep.core.skill.impl.BlueWardSkill;
import com.kiemhiep.core.skill.impl.RedWardSkill;
import com.kiemhiep.core.skill.impl.OwlSpiritSkill;
import com.kiemhiep.core.skill.impl.BeastWolfSkill;
import com.kiemhiep.core.skill.impl.PhoenixFlameSkill;
import com.kiemhiep.core.skill.impl.CrabSummonSkill;
import com.kiemhiep.core.skill.impl.BearSummonSkill;
import com.kiemhiep.core.skill.impl.VoidSpawnSkill;
import com.kiemhiep.core.skill.impl.ZDriveSkill;
import com.kiemhiep.core.skill.impl.DarkRiftSkill;
import com.kiemhiep.core.skill.impl.QuantumRaySkill;
import com.kiemhiep.core.skill.impl.ShieldSummonSkill;
import com.kiemhiep.core.skill.impl.FlameChompersSkill;
import com.kiemhiep.core.skill.impl.RainArrowsSkill;
import com.kiemhiep.core.skill.impl.LightSpikeSkill;
import com.kiemhiep.core.skill.impl.FrozenCageSkill;
import com.kiemhiep.core.skill.impl.ElectricSnakeSkill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import com.kiemhiep.cultivation.CultivationModule;
import com.kiemhiep.platform.FabricEffectManager;
import com.kiemhiep.platform.FabricPlatformProvider;
import com.kiemhiep.platform.SkillItemRegistrationHelper;
import com.kiemhiep.platform.network.PlayerStatsPayload;
import com.kiemhiep.platform.network.SkillDefinitionsPayload;
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
        SkillRegistry.register("INFERNO_FIST", InfernoFistSkill.INSTANCE);
        SkillRegistry.register("SOLAR_FLARE", SolarFlareSkill.INSTANCE);
        SkillRegistry.register("DRAGON_BREATH", DragonBreathSkill.INSTANCE);
        SkillRegistry.register("BLAZING_SWORD", BlazingSwordSkill.INSTANCE);
        SkillRegistry.register("PYROCLASTIC_FLOW", PyroclasticFlowSkill.INSTANCE);
        SkillRegistry.register("ICE_SHARD", GlacierSpikeSkill.INSTANCE);
        SkillRegistry.register("GLACIER_SPIKE", GlacierSpikeSkill.INSTANCE);
        SkillRegistry.register("CRYO_BLAST", CryoBlastSkill.INSTANCE);
        SkillRegistry.register("ARCTIC_WIND", ArcticWindSkill.INSTANCE);
        SkillRegistry.register("ICE_PRISON", IcePrisonSkill.INSTANCE);
        SkillRegistry.register("THUNDER_FANG", ThunderFangSkill.INSTANCE);
        SkillRegistry.register("ELECTRO_WAVE", ElectroWaveSkill.INSTANCE);
        SkillRegistry.register("RAGING_THUNDER", RagingThunderSkill.INSTANCE);
        SkillRegistry.register("CHAIN_THRUST", ChainThrustSkill.INSTANCE);
        SkillRegistry.register("VAJRA_LIGHTNING", VajraLightningSkill.INSTANCE);
        SkillRegistry.register("STONE_FIST", StoneFistSkill.INSTANCE);
        SkillRegistry.register("MUD_WALL", MudWallSkill.INSTANCE);
        SkillRegistry.register("SEISMIC_PULSE", SeismicPulseSkill.INSTANCE);
        SkillRegistry.register("EARTH_GOLEM", EarthGolemSkill.INSTANCE);
        SkillRegistry.register("QUAKE_STOMP", QuakeStompSkill.INSTANCE);
        SkillRegistry.register("GALE_SWORD", GaleSwordSkill.INSTANCE);
        SkillRegistry.register("SAND_STORM", SandStormSkill.INSTANCE);
        SkillRegistry.register("VACUUM_CUT", VacuumCutSkill.INSTANCE);
        SkillRegistry.register("TORNADO_SWEEP", TornadoSweepSkill.INSTANCE);
        SkillRegistry.register("SONIC_SLICER", SonicSlicerSkill.INSTANCE);
        SkillRegistry.register("BLACK_SPARK", BlackSparkSkill.INSTANCE);
        SkillRegistry.register("ACID_RAIN", AcidRainSkill.INSTANCE);
        SkillRegistry.register("MIASMA_BLAST", MiasmaBlastSkill.INSTANCE);
        SkillRegistry.register("TRANSFORMATION", TransformationSkill.INSTANCE);
        SkillRegistry.register("CURSED_GAS", CursedGasSkill.INSTANCE);
        SkillRegistry.register("TIME_BOMB", TimeBombSkill.INSTANCE);
        SkillRegistry.register("SENTRY_LIGHT", LightSentrySkill.INSTANCE);
        SkillRegistry.register("HEAL_STAR", StarlightHealSkill.INSTANCE);
        SkillRegistry.register("WARD_BLUE", BlueWardSkill.INSTANCE);
        SkillRegistry.register("WARD_RED", RedWardSkill.INSTANCE);
        SkillRegistry.register("SPIRIT_OWL", OwlSpiritSkill.INSTANCE);
        SkillRegistry.register("BEAST_WOLF", BeastWolfSkill.INSTANCE);
        SkillRegistry.register("PHOENIX_FLAME", PhoenixFlameSkill.INSTANCE);
        SkillRegistry.register("SUMMON_CRAB", CrabSummonSkill.INSTANCE);
        SkillRegistry.register("SUMMON_BEAR", BearSummonSkill.INSTANCE);
        SkillRegistry.register("VOID_SPAWN", VoidSpawnSkill.INSTANCE);
        SkillRegistry.register("TIME_BREAKER", ZDriveSkill.INSTANCE);
        SkillRegistry.register("DARK_RIFT", DarkRiftSkill.INSTANCE);
        SkillRegistry.register("QUANTUM_RAY", QuantumRaySkill.INSTANCE);
        SkillRegistry.register("SUMMON_SHIELD", ShieldSummonSkill.INSTANCE);
        SkillRegistry.register("FLAME_CHOMP", FlameChompersSkill.INSTANCE);
        SkillRegistry.register("RAIN_ARROWS", RainArrowsSkill.INSTANCE);
        SkillRegistry.register("LIGHT_SPIKE", LightSpikeSkill.INSTANCE);
        SkillRegistry.register("FROZEN_CAGE", FrozenCageSkill.INSTANCE);
        SkillRegistry.register("ELECTRIC_SNAKE", ElectricSnakeSkill.INSTANCE);

        // V5 seed: elemental skills (map to existing implementations)
        SkillRegistry.register("FIRE_BLAST", FireballSkill.INSTANCE);
        SkillRegistry.register("FIRE_STORM", FireballSkill.INSTANCE);
        SkillRegistry.register("FIRE_WALL", InfernoFistSkill.INSTANCE);
        SkillRegistry.register("FROST_WAVE", GlacierSpikeSkill.INSTANCE);
        SkillRegistry.register("ICE_CRYSTAL", CryoBlastSkill.INSTANCE);
        SkillRegistry.register("LIGHTNING_BOLT", ThunderSkill.INSTANCE);
        SkillRegistry.register("CHAIN_LIGHTNING", ElectroWaveSkill.INSTANCE);
        SkillRegistry.register("THUNDER_STORM", RagingThunderSkill.INSTANCE);
        SkillRegistry.register("EARTH_SPIKE", StoneFistSkill.INSTANCE);
        SkillRegistry.register("TREMOR", SeismicPulseSkill.INSTANCE);
        SkillRegistry.register("EARTH_BARRIER", MudWallSkill.INSTANCE);
        SkillRegistry.register("WIND_CUT", GaleSwordSkill.INSTANCE);
        SkillRegistry.register("CYCLONE", TornadoSweepSkill.INSTANCE);
        SkillRegistry.register("SONIC_BOOM", SonicSlicerSkill.INSTANCE);
        SkillRegistry.register("POISON_DART", BlackSparkSkill.INSTANCE);
        SkillRegistry.register("POISON_CLOUD", AcidRainSkill.INSTANCE);
        SkillRegistry.register("VENOM_WEB", MiasmaBlastSkill.INSTANCE);
        // V6: lightning stab (melee)
        SkillRegistry.register("LIGHTNING_STAB", ChainThrustSkill.INSTANCE);

        SkillNetworking.register();
        // Set repository reference for skill definitions payload
        com.kiemhiep.platform.network.SkillDefinitionsPayload.setRepository(definitionRepository);
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
            Kiemhiep.LOGGER.debug("[Skill] item used: player={} itemId={} hand={}", player.getName().getString(), itemId, hand);
            SkillManager.UseResult result = skillServiceOpt.get().useSkill(player.getUUID(), itemId, serverTick);
            Kiemhiep.LOGGER.debug("[Skill] useSkill result: itemId={} result={}", itemId, result);
            if (result == SkillManager.UseResult.SUCCESS) {
                skillServiceOpt.get().getByItemId(itemId).ifPresent(def -> {
                    if (def.consumable()) stack.shrink(1);
                });
                return InteractionResult.SUCCESS;
            }
            if (result == SkillManager.UseResult.CAST_STARTED) {
                return InteractionResult.SUCCESS;
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

            // Send skill definitions for tooltip
            var skillDefsPayload = SkillDefinitionsPayload.create();
            ServerPlayNetworking.send(serverPlayer, skillDefsPayload);
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
