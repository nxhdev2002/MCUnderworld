package com.kiemhiep.network;

import com.kiemhiep.effect.SkySplitEffect;
import com.kiemhiep.effect.TimeBombEffect;
import com.kiemhiep.effect.SentryLightEffect;
import com.kiemhiep.effect.StarlightHealEffect;
import com.kiemhiep.effect.WardBlueEffect;
import com.kiemhiep.effect.WardRedEffect;
import com.kiemhiep.effect.SpiritOwlEffect;
import com.kiemhiep.effect.BeastWolfEffect;
import com.kiemhiep.effect.PhoenixFlameEffect;
import com.kiemhiep.effect.CrabSummonEffect;
import com.kiemhiep.effect.BearSummonEffect;
import com.kiemhiep.effect.VoidSpawnEffect;
import com.kiemhiep.effect.TimeBreakerEffect;
import com.kiemhiep.effect.DarkRiftEffect;
import com.kiemhiep.effect.QuantumRayEffect;
import com.kiemhiep.effect.ShieldSummonEffect;
import com.kiemhiep.effect.FlameChompersEffect;
import com.kiemhiep.effect.RainArrowsEffect;
import com.kiemhiep.effect.LightSpikeEffect;
import com.kiemhiep.effect.FrozenCageEffect;
import com.kiemhiep.effect.ElectricSnakeEffect;
import com.kiemhiep.shader.ElementalShaderState;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers skill effect payload and spawns particles on the client.
 */
public final class SkillEffectReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");
    /** Các effect nguyên tố: kích hoạt overlay shader. "thunder" = lightning (ThunderSkill gửi thunder). */
    private static final java.util.Set<String> ELEMENTAL_TYPES = java.util.Set.of("fire", "ice", "lightning", "thunder", "earth", "wind", "poison");

    private SkillEffectReceiver() {}

    public static void register() {
        System.out.println("[Kiemhiep] SkillEffectReceiver registering (client mod loaded)");
        LOGGER.info("[SkillEffect] register: client payload receiver init");
        PayloadTypeRegistry.playS2C().register(SkillEffectPayload.TYPE, SkillEffectPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SkillEffectPayload.TYPE, (payload, context) -> {
            try {
                double x = payload.x();
                double y = payload.y();
                double z = payload.z();
                String effectType = payload.effectType();
                System.out.println("[Kiemhiep] SkillEffect received: effectType=" + effectType + " at (" + x + "," + y + "," + z + ") worldId=" + payload.worldId());
                LOGGER.info("[SkillEffect] received effectType={} at ({}, {}, {}) worldId={}", effectType, x, y, z, payload.worldId());
                context.client().execute(() -> {
                    try {
                        Level world = context.client().level;
                        if (world == null) {
                            LOGGER.warn("Skill effect: level null when spawning particles, aborting");
                            return;
                        }
                        String worldId = payload.worldId();
                        String currentDim = world.dimension().toString();
                        if (worldId != null && !worldId.isEmpty() && !currentDim.equals(worldId)) {
                            LOGGER.warn("[SkillEffect] ignored (dimension mismatch): payload.worldId={} current={}", worldId, currentDim);
                            return;
                        }
                        if (ELEMENTAL_TYPES.contains(effectType)) {
                            ElementalShaderState.activate(effectType);
                        }
                        spawnParticles(world, x, y, z, effectType);
                        if (context.client().player != null) {
                            context.client().player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("§b[Kiemhiep] Effect: " + effectType), true);
                        }
                    } catch (Throwable t) {
                        System.err.println("[Kiemhiep] SkillEffect handler error: " + t.getMessage());
                        LOGGER.error("[SkillEffect] handler error", t);
                    }
                });
            } catch (Throwable t) {
                System.err.println("[Kiemhiep] SkillEffect decode/callback error: " + t.getMessage());
                LOGGER.error("[SkillEffect] decode/callback error", t);
            }
        });
    }

    private static void spawnParticles(Level world, double x, double y, double z, String effectType) {
        switch (effectType) {
            case "thunder" -> spawnThunderParticles(world, x, y, z);
            case "tornado" -> spawnTornadoParticles(world, x, y, z);
            case "tsunami" -> spawnTsunamiParticles(world, x, y, z);
            case "meteor" -> spawnMeteorParticles(world, x, y, z);
            case "fire" -> spawnFireParticles(world, x, y, z);
            case "ice" -> spawnIceParticles(world, x, y, z);
            case "lightning" -> spawnLightningParticles(world, x, y, z);
            case "earth" -> spawnEarthParticles(world, x, y, z);
            case "wind" -> spawnWindParticles(world, x, y, z);
            case "poison" -> spawnPoisonParticles(world, x, y, z);
            case "time_bomb" -> spawnTimeBombParticles(world, x, y, z);
            case "sentry_light" -> spawnSentryParticles(world, x, y, z);
            case "star_heal" -> spawnStarHealParticles(world, x, y, z);
            case "ward_blue" -> spawnWardBlueParticles(world, x, y, z);
            case "ward_red" -> spawnWardRedParticles(world, x, y, z);
            case "spirit_owl" -> spawnSpiritOwlParticles(world, x, y, z);
            case "beast_wolf" -> spawnBeastWolfParticles(world, x, y, z);
            case "phoenix" -> spawnPhoenixParticles(world, x, y, z);
            case "summon_crab" -> spawnCrabParticles(world, x, y, z);
            case "bear_summon" -> spawnBearParticles(world, x, y, z);
            case "void_spawn" -> spawnVoidParticles(world, x, y, z);
            case "time_breaker" -> spawnTimeBreakerParticles(world, x, y, z);
            case "dark_rift" -> spawnDarkRiftParticles(world, x, y, z);
            case "quantum_ray" -> spawnQuantumParticles(world, x, y, z);
            case "shield_summon" -> spawnShieldParticles(world, x, y, z);
            case "flame_chomp" -> spawnFlameChompersParticles(world, x, y, z);
            case "rain_arrows" -> spawnRainArrowsParticles(world, x, y, z);
            case "light_spike" -> spawnLightSpikeParticles(world, x, y, z);
            case "frozen_cage" -> spawnFrozenCageParticles(world, x, y, z);
            case "electric_snake" -> spawnElectricSnakeParticles(world, x, y, z);
            default -> {
                LOGGER.warn("Unknown skill effectType, using generic particles: effectType={}", effectType);
                spawnGenericParticles(world, x, y, z);
            }
        }
    }

    /** Spawn particle; use addAlwaysVisibleParticle on ClientLevel so effect is visible at any distance. */
    private static void addParticle(Level world, ParticleOptions type, double x, double y, double z, double vx, double vy, double vz) {
        if (world instanceof ClientLevel clientLevel) {
            clientLevel.addAlwaysVisibleParticle(type, x, y, z, vx, vy, vz);
        } else {
            LOGGER.warn("Skill effect addParticle: world is not ClientLevel ({}), using addParticle", world.getClass().getName());
            world.addParticle(type, x, y, z, vx, vy, vz);
        }
    }

    /**
     * Thunder: tia sấm sét đánh xuống (ELECTRIC_SPARK từ trên xuống) + nổ tại điểm chạm (EXPLOSION + khói/lửa).
     */
    private static void spawnThunderParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnThunderParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        int lightningSteps = 12;
        for (int i = 0; i <= lightningSteps; i++) {
            double progress = (double) i / lightningSteps;
            double py = y + height * (1.0 - progress);
            double jitter = 0.15 * (1.0 - progress);
            double px = x + (world.random.nextDouble() - 0.5) * jitter;
            double pz = z + (world.random.nextDouble() - 0.5) * jitter;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0, -0.5, 0);
            addParticle(world, ParticleTypes.SMOKE, px, py, pz, 0, -0.2, 0);
        }
        addParticle(world, ParticleTypes.EXPLOSION_EMITTER, x, y + 0.5, z, 0, 0, 0);
        addParticle(world, ParticleTypes.EXPLOSION, x, y + 0.5, z, 0, 0, 0);
        for (int i = 0; i < 30; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 3.0;
            double oz = (world.random.nextDouble() - 0.5) * 3.0;
            addParticle(world, ParticleTypes.LARGE_SMOKE, x + ox, y + 0.5, z + oz, 0, 0.2, 0);
            addParticle(world, ParticleTypes.FLAME, x + ox * 0.8, y + 0.3, z + oz * 0.8, 0, 0.25, 0);
            addParticle(world, ParticleTypes.SMOKE, x + ox, y + 0.8, z + oz, 0, 0.15, 0);
        }
        LOGGER.trace("Skill effect spawnThunderParticles: done");
    }

    private static void spawnTornadoParticles(Level world, double x, double y, double z) {
        for (int i = 0; i < 24; i++) {
            double angle = (i / 24.0) * Math.PI * 2;
            double r = 1.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.CLOUD, px, y + 0.5, pz, 0, 0.15, 0);
            addParticle(world, ParticleTypes.SMOKE, px, y + 1, pz, 0, 0.1, 0);
        }
    }

    private static void spawnTsunamiParticles(Level world, double x, double y, double z) {
        for (int i = 0; i < 20; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            addParticle(world, ParticleTypes.SPLASH, x + ox, y + 0.5, z + oz, 0, 0.3, 0);
            addParticle(world, ParticleTypes.BUBBLE, x + ox, y, z + oz, 0, 0.2, 0);
        }
    }

    /** Thiên thạch từ trên trời rơi xuống mục tiêu (x, y, z): trail từ cao rơi xuống + nổ tại điểm chạm. */
    private static void spawnMeteorParticles(Level world, double x, double y, double z) {
        // Kích hoạt hiệu ứng sky split
        SkySplitEffect.activate(x, y, z);

        double height = 12.0;
        int trails = 5;
        int particlesPerTrail = 8;
        for (int t = 0; t < trails; t++) {
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            for (int i = 0; i < particlesPerTrail; i++) {
                double progress = (i + world.random.nextDouble()) / particlesPerTrail;
                double py = y + height * (1.0 - progress);
                double px = x + ox * (1.0 - progress * 0.5);
                double pz = z + oz * (1.0 - progress * 0.5);
                addParticle(world, ParticleTypes.FLAME, px, py, pz, 0, -0.8, 0);
                addParticle(world, ParticleTypes.SMOKE, px, py, pz, 0, -0.5, 0);
                addParticle(world, ParticleTypes.LARGE_SMOKE, px, py + 0.3, pz, 0, -0.4, 0);
            }
        }
        for (int i = 0; i < 25; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 4;
            double oz = (world.random.nextDouble() - 0.5) * 4;
            addParticle(world, ParticleTypes.FLAME, x + ox, y + 0.5, z + oz, 0, 0.2, 0);
            addParticle(world, ParticleTypes.LARGE_SMOKE, x + ox, y, z + oz, 0, 0.15, 0);
        }
    }

    private static void spawnTimeBombParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        TimeBombEffect.activate(x, y, z);

        // Particle effect for Zilean's Time Bomb
        LOGGER.trace("Skill effect spawnTimeBombParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        // Countdown particles falling
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * (1.0 - progress);
            addParticle(world, ParticleTypes.FLAME, x, py, z, 0, -0.5, 0);
            addParticle(world, ParticleTypes.SMOKE, x, py + 0.2, z, 0, -0.3, 0);
        }
        // Expanding ring
        for (int i = 0; i < 20; i++) {
            double angle = (System.currentTimeMillis() * 0.01 + i / 20.0) * Math.PI * 2;
            double r = 2.0 + world.random.nextDouble();
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, y, pz, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnTimeBombParticles: done");
    }

    private static void spawnSentryParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        SentryLightEffect.activate(x, y, z);

        // Particle effect for Vikor's Light Sentry
        LOGGER.trace("Skill effect spawnSentryParticles: start at ({}, {}, {})", x, y, z);
        double height = 8.0;
        // Light particles rising from ground
        for (int i = 0; i < 12; i++) {
            double progress = (i + world.random.nextDouble()) / 12.0;
            double py = y + height * progress;
            addParticle(world, ParticleTypes.FLAME, x, py, z, 0, 0.3, 0);
        }
        // Rotating light ring
        for (int i = 0; i < 16; i++) {
            double angle = (System.currentTimeMillis() * 0.015 + i / 16.0) * Math.PI * 2;
            double r = 2.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, y + 1, pz, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnSentryParticles: done");
    }

    private static void spawnStarHealParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        StarlightHealEffect.activate(x, y, z);

        // Particle effect for Soraka's Starlight Heal
        LOGGER.trace("Skill effect spawnStarHealParticles: start at ({}, {}, {})", x, y, z);
        double height = 15.0;
        // Star particles falling
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            addParticle(world, ParticleTypes.END_ROD, x, py, z, 0, -0.6, 0);
            addParticle(world, ParticleTypes.ENCHANT, x + 0.2, py + 0.1, z, 0, -0.4, 0);
        }
        // Healing circle
        for (int i = 0; i < 24; i++) {
            double angle = (i / 24.0) * Math.PI * 2;
            double r = 3.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.HEART, px, y, pz, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnStarHealParticles: done");
    }

    private static void spawnWardBlueParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        WardBlueEffect.activate(x, y, z);

        // Particle effect for Blue Ward
        LOGGER.trace("Skill effect spawnWardBlueParticles: start at ({}, {}, {})", x, y, z);
        double height = 6.0;
        // Blue crystals
        for (int i = 0; i < 10; i++) {
            double progress = (i + world.random.nextDouble()) / 10.0;
            double py = y + height * progress;
            double ox = (world.random.nextDouble() - 0.5) * 2;
            double oz = (world.random.nextDouble() - 0.5) * 2;
            addParticle(world, ParticleTypes.DRIPPING_WATER, x + ox, py, z + oz, 0, 0.4, 0);
        }
        // Rotating ring
        for (int i = 0; i < 16; i++) {
            double angle = (System.currentTimeMillis() * 0.01 + i / 16.0) * Math.PI * 2;
            double r = 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.DRIPPING_WATER, px, y, pz, 0, 0, 0);
        }
        LOGGER.trace("Skill effect spawnWardBlueParticles: done");
    }

    private static void spawnWardRedParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        WardRedEffect.activate(x, y, z);

        // Particle effect for Red Ward
        LOGGER.trace("Skill effect spawnWardRedParticles: start at ({}, {}, {})", x, y, z);
        double height = 6.0;
        // Red embers
        for (int i = 0; i < 10; i++) {
            double progress = (i + world.random.nextDouble()) / 10.0;
            double py = y + height * progress;
            double ox = (world.random.nextDouble() - 0.5) * 2;
            double oz = (world.random.nextDouble() - 0.5) * 2;
            addParticle(world, ParticleTypes.FLAME, x + ox, py, z + oz, 0, 0.4, 0);
        }
        // Spinning ring
        for (int i = 0; i < 16; i++) {
            double angle = (System.currentTimeMillis() * 0.01 + i / 16.0) * Math.PI * 2;
            double r = 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SMOKE, px, y, pz, 0, 0, 0);
        }
        LOGGER.trace("Skill effect spawnWardRedParticles: done");
    }

    private static void spawnSpiritOwlParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        SpiritOwlEffect.activate(x, y, z);

        // Particle effect for Ahri's Fox Spirit
        LOGGER.trace("Skill effect spawnSpiritOwlParticles: start at ({}, {}, {})", x, y, z);
        double height = 12.0;
        // Fox spirit particles
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * (1.0 - progress);
            double angle = System.currentTimeMillis() * 0.005 + progress * Math.PI * 4;
            double r = 1.5 + progress;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, py, pz, 0, -0.4, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.5, py + 0.2, pz + 0.5, 0, -0.2, 0);
        }
        LOGGER.trace("Skill effect spawnSpiritOwlParticles: done");
    }

    private static void spawnBeastWolfParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        BeastWolfEffect.activate(x, y, z);

        // Particle effect for Brand's Wolf Beast
        LOGGER.trace("Skill effect spawnBeastWolfParticles: start at ({}, {}, {})", x, y, z);
        double height = 8.0;
        // Wolf spirit trail
        for (int i = 0; i < 18; i++) {
            double progress = (i + world.random.nextDouble()) / 18.0;
            double py = y + height * (1.0 - progress);
            addParticle(world, ParticleTypes.FLAME, x, py, z, 0, -0.5, 0);
            addParticle(world, ParticleTypes.SMOKE, x + 0.3, py + 0.2, z + 0.3, 0, -0.3, 0);
        }
        // Howling sound particles
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2;
            double r = 2.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.ENCHANT, px, y, pz, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnBeastWolfParticles: done");
    }

    private static void spawnPhoenixParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        PhoenixFlameEffect.activate(x, y, z);

        // Particle effect for Phoenix
        LOGGER.trace("Skill effect spawnPhoenixParticles: start at ({}, {}, {})", x, y, z);
        double height = 20.0;
        // Phoenix wing trail
        for (int i = 0; i < 25; i++) {
            double progress = (i + world.random.nextDouble()) / 25.0;
            double py = y + height * (1.0 - progress);
            double angle = progress * Math.PI * 6 + System.currentTimeMillis() * 0.005;
            double r = 2.0 + progress * 2;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, py, pz, 0, -0.7, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.5, py + 0.2, pz + 0.5, 0, -0.4, 0);
        }
        // Explosion core
        for (int i = 0; i < 20; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            addParticle(world, ParticleTypes.FLAME, x + ox, y, z + oz, 0, 0.2, 0);
        }
        LOGGER.trace("Skill effect spawnPhoenixParticles: done");
    }

    private static void spawnCrabParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        CrabSummonEffect.activate(x, y, z);

        // Particle effect for Crab Summon
        LOGGER.trace("Skill effect spawnCrabParticles: start at ({}, {}, {})", x, y, z);
        double height = 5.0;
        // Sand particles from ground
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * progress;
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            addParticle(world, ParticleTypes.FALLING_SPORE_BLOSSOM, x + ox, py, z + oz, 0, 0.3, 0);
        }
        // Shell particles
        for (int i = 0; i < 10; i++) {
            double angle = (i / 10.0) * Math.PI * 2;
            double r = 2.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.BUBBLE, px, y, pz, 0, 0.05, 0);
        }
        LOGGER.trace("Skill effect spawnCrabParticles: done");
    }

    private static void spawnBearParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        BearSummonEffect.activate(x, y, z);

        // Particle effect for Bear Summon
        LOGGER.trace("Skill effect spawnBearParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        // Bear spirit trail
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x, py, z, 0, -0.5, 0);
            addParticle(world, ParticleTypes.SMOKE, x + 0.2, py + 0.1, z + 0.2, 0, -0.3, 0);
        }
        // Huge aura
        for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2;
            double r = 3.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.HEART, px, y, pz, 0, 0.15, 0);
        }
        LOGGER.trace("Skill effect spawnBearParticles: done");
    }

    private static void spawnVoidParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        VoidSpawnEffect.activate(x, y, z);

        // Particle effect for Void Spawn
        LOGGER.trace("Skill effect spawnVoidParticles: start at ({}, {}, {})", x, y, z);
        double height = 12.0;
        // Void rift particles
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            double angle = System.currentTimeMillis() * 0.01 + progress * Math.PI * 8;
            double r = 2.0 + progress * 3;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SOUL, px, py, pz, 0, -0.4, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.3, py + 0.2, pz + 0.3, 0, -0.2, 0);
        }
        LOGGER.trace("Skill effect spawnVoidParticles: done");
    }

    private static void spawnTimeBreakerParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        TimeBreakerEffect.activate(x, y, z);

        // Particle effect for Ekko's Z-Drive
        LOGGER.trace("Skill effect spawnTimeBreakerParticles: start at ({}, {}, {})", x, y, z);
        double height = 15.0;
        // Time rift particles
        for (int i = 0; i < 25; i++) {
            double progress = (i + world.random.nextDouble()) / 25.0;
            double py = y + height * (1.0 - progress);
            double angle = System.currentTimeMillis() * 0.02 + progress * Math.PI * 10;
            double r = 1.5 + progress * 2.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.END_ROD, px, py, pz, 0, -0.6, 0);
            addParticle(world, ParticleTypes.END_ROD, px + 0.2, py + 0.1, pz + 0.2, 0, -0.3, 0);
        }
        // Resonance rings
        for (int i = 0; i < 3; i++) {
            double ringProgress = (System.currentTimeMillis() / 500.0 + i * 0.33) % 1.0;
            double r = 2.0 + ringProgress * 4;
            for (int j = 0; j < 16; j++) {
                double angle = (j / 16.0) * Math.PI * 2;
                double px = x + Math.cos(angle) * r;
                double pz = z + Math.sin(angle) * r;
                addParticle(world, ParticleTypes.END_ROD, px, y, pz, 0, 0, 0);
            }
        }
        LOGGER.trace("Skill effect spawnTimeBreakerParticles: done");
    }

    private static void spawnDarkRiftParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        DarkRiftEffect.activate(x, y, z);

        // Particle effect for Dark Rift
        LOGGER.trace("Skill effect spawnDarkRiftParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        // Dark rift particles
        for (int i = 0; i < 18; i++) {
            double progress = (i + world.random.nextDouble()) / 18.0;
            double py = y + height * (1.0 - progress);
            double angle = System.currentTimeMillis() * 0.015 + progress * Math.PI * 6;
            double r = 2.5 + progress;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SOUL, px, py, pz, 0, -0.3, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.2, py + 0.1, pz + 0.2, 0, -0.1, 0);
        }
        LOGGER.trace("Skill effect spawnDarkRiftParticles: done");
    }

    private static void spawnQuantumParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        QuantumRayEffect.activate(x, y, z);

        // Particle effect for Quantum Ray
        LOGGER.trace("Skill effect spawnQuantumParticles: start at ({}, {}, {})", x, y, z);
        double height = 18.0;
        // Quantum trail
        for (int i = 0; i < 22; i++) {
            double progress = (i + world.random.nextDouble()) / 22.0;
            double py = y + height * (1.0 - progress);
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, x + ox, py, z + oz, 0, -0.7, 0);
            addParticle(world, ParticleTypes.END_ROD, x + ox + 0.5, py + 0.2, z + oz + 0.5, 0, -0.4, 0);
        }
        LOGGER.trace("Skill effect spawnQuantumParticles: done");
    }

    private static void spawnShieldParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        ShieldSummonEffect.activate(x, y, z);

        // Particle effect for Void Shield
        LOGGER.trace("Skill effect spawnShieldParticles: start at ({}, {}, {})", x, y, z);
        double height = 8.0;
        // Shield particles
        for (int i = 0; i < 16; i++) {
            double progress = (i + world.random.nextDouble()) / 16.0;
            double py = y + height * (1.0 - progress);
            double angle = (System.currentTimeMillis() * 0.02 + progress * Math.PI * 4) % (Math.PI * 2);
            double r = 2.0 + progress;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SOUL, px, py, pz, 0, -0.4, 0);
        }
        // Protective ring
        for (int i = 0; i < 24; i++) {
            double angle = (i / 24.0) * Math.PI * 2;
            double r = 3.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.ENCHANT, px, y, pz, 0, 0.05, 0);
        }
        LOGGER.trace("Skill effect spawnShieldParticles: done");
    }

    private static void spawnFlameChompersParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        FlameChompersEffect.activate(x, y, z);

        // Particle effect for Jinx's Flame Chompers
        LOGGER.trace("Skill effect spawnFlameChompersParticles: start at ({}, {}, {})", x, y, z);
        double height = 6.0;
        // Flame chompers trail
        for (int i = 0; i < 12; i++) {
            double progress = (i + world.random.nextDouble()) / 12.0;
            double py = y + height * (1.0 - progress);
            double angle = (progress * Math.PI * 4 + System.currentTimeMillis() * 0.01) % (Math.PI * 2);
            double r = 1.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, py, pz, 0, -0.3, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.3, py + 0.1, pz + 0.3, 0, -0.1, 0);
        }
        LOGGER.trace("Skill effect spawnFlameChompersParticles: done");
    }

    private static void spawnRainArrowsParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        RainArrowsEffect.activate(x, y, z);

        // Particle effect for Miss Fortune's Make It Rain
        LOGGER.trace("Skill effect spawnRainArrowsParticles: start at ({}, {}, {})", x, y, z);
        double height = 25.0;
        // Arrows falling from sky
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            double ox = (world.random.nextDouble() - 0.5) * 8;
            double oz = (world.random.nextDouble() - 0.5) * 8;
            addParticle(world, ParticleTypes.END_ROD, x + ox, py, z + oz, 0, -1.2, 0);
            addParticle(world, ParticleTypes.SMOKE, x + ox + 0.2, py + 0.1, z + oz + 0.2, 0, -0.6, 0);
        }
        // Impact ring
        for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2;
            double r = 3.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SPLASH, px, y, pz, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnRainArrowsParticles: done");
    }

    private static void spawnLightSpikeParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        LightSpikeEffect.activate(x, y, z);

        // Particle effect for Lux's Final Spark
        LOGGER.trace("Skill effect spawnLightSpikeParticles: start at ({}, {}, {})", x, y, z);
        double height = 20.0;
        // Light spikes
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * (1.0 - progress);
            double ox = (world.random.nextDouble() - 0.5) * 5;
            double oz = (world.random.nextDouble() - 0.5) * 5;
            addParticle(world, ParticleTypes.END_ROD, x + ox, py, z + oz, 0, -0.8, 0);
            addParticle(world, ParticleTypes.FLAME, x + ox + 0.3, py + 0.2, z + oz + 0.3, 0, -0.5, 0);
        }
        // Spark burst
        for (int i = 0; i < 20; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 4;
            double oz = (world.random.nextDouble() - 0.5) * 4;
            addParticle(world, ParticleTypes.FLAME, x + ox, y, z + oz, 0, 0.15, 0);
        }
        LOGGER.trace("Skill effect spawnLightSpikeParticles: done");
    }

    private static void spawnFrozenCageParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        FrozenCageEffect.activate(x, y, z);

        // Particle effect for Frozen Cage
        LOGGER.trace("Skill effect spawnFrozenCageParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        // Ice particles
        for (int i = 0; i < 16; i++) {
            double progress = (i + world.random.nextDouble()) / 16.0;
            double py = y + height * (1.0 - progress);
            double angle = (progress * Math.PI * 6 + System.currentTimeMillis() * 0.015) % (Math.PI * 2);
            double r = 3.0 + progress;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SNOWFLAKE, px, py, pz, 0, -0.4, 0);
            addParticle(world, ParticleTypes.END_ROD, px + 0.2, py + 0.1, pz + 0.2, 0, -0.2, 0);
        }
        LOGGER.trace("Skill effect spawnFrozenCageParticles: done");
    }

    private static void spawnElectricSnakeParticles(Level world, double x, double y, double z) {
        // Activate custom effect
        ElectricSnakeEffect.activate(x, y, z);

        // Particle effect for Electric Snake
        LOGGER.trace("Skill effect spawnElectricSnakeParticles: start at ({}, {}, {})", x, y, z);
        double height = 8.0;
        // Snake particles
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            double angle = (progress * Math.PI * 8 + System.currentTimeMillis() * 0.02) % (Math.PI * 2);
            double r = 2.5 + progress * 1.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0, -0.5, 0);
            addParticle(world, ParticleTypes.SMOKE, px + 0.2, py + 0.1, pz + 0.2, 0, -0.2, 0);
        }
        // Electric field
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2;
            double r = 3.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, px, y, pz, 0, 0.05, 0);
        }
        LOGGER.trace("Skill effect spawnElectricSnakeParticles: done");
    }

    private static void spawnGenericParticles(Level world, double x, double y, double z) {
        for (int i = 0; i < 8; i++) {
            addParticle(world, ParticleTypes.CLOUD, x, y + 0.5, z,
                (world.random.nextDouble() - 0.5) * 0.2, 0.1, (world.random.nextDouble() - 0.5) * 0.2);
        }
    }

    /**
     * Fire: Hỏa trail dari trên xuống + cháy vùng.
     * Particle: FLAME, LARGE_SMOKE
     * Màu sắc: Gradient Cam→Đỏ
     */
    private static void spawnFireParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnFireParticles: start at ({}, {}, {})", x, y, z);
        double height = 15.0;
        // Vệt lửa từ trên xuống
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            double ox = (world.random.nextDouble() - 0.5) * 2;
            double oz = (world.random.nextDouble() - 0.5) * 2;
            addParticle(world, ParticleTypes.FLAME, x + ox, py, z + oz,
                0, -0.5, 0);
            addParticle(world, ParticleTypes.LARGE_SMOKE, x + ox * 0.5, py + 0.3, z + oz * 0.5,
                0, -0.3, 0);
        }
        // Cháy vùng quanh trung tâm
        for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2;
            double r = 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.FLAME, px, y + 0.5, pz, 0.2, 0.3, 0.2);
            addParticle(world, ParticleTypes.LARGE_SMOKE, px, y + 1, pz, 0.1, 0.2, 0.1);
        }
        LOGGER.trace("Skill effect spawnFireParticles: done");
    }

    /**
     * Ice: Băng tinh thể rơi và XOAY ốc.
     * Particle: SNOWFLAKE, END_ROD
     * Màu sắc: Gradient Cyan→Trắng
     */
    private static void spawnIceParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnIceParticles: start at ({}, {}, {})", x, y, z);
        double height = 20.0;
        // Tinh thể băng rơi từ trên xuống
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * (1.0 - progress);
            addParticle(world, ParticleTypes.SNOWFLAKE, x, py, z, 0, -0.6, 0);
            addParticle(world, ParticleTypes.END_ROD, x, py + 0.3, z, 0, -0.3, 0);
        }
        // Vòng tròn băng quanh trung tâm
        for (int i = 0; i < 24; i++) {
            double angle = (i / 24.0) * Math.PI * 2;
            double r = 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.SNOWFLAKE, x, y, z,
                (px - x) * 0.1, 0.4, (pz - z) * 0.1);
            addParticle(world, ParticleTypes.END_ROD, x, y, z,
                (px - x) * 0.05, 0.2, (pz - z) * 0.05);
        }
        LOGGER.trace("Skill effect spawnIceParticles: done");
    }

    /**
     * Lightning: Tia sét zig-zag từ trên xuống + điện tích lan ra.
     * Particle: ELECTRIC_SPARK, SMOKE
     * Màu sắc: Gradient Xanh→Trắng chớp
     */
    private static void spawnLightningParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnLightningParticles: start at ({}, {}, {})", x, y, z);
        double height = 25.0;
        int steps = 15;
        // Tia sét zig-zag từ trên xuống
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            double py = y + height * (1.0 - progress);
            double jitter = 0.3 * (1.0 - progress);
            double px = x + (world.random.nextDouble() - 0.5) * jitter * 4;
            double pz = z + (world.random.nextDouble() - 0.5) * jitter * 4;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0, -0.8, 0);
            addParticle(world, ParticleTypes.SMOKE, px, py, pz, 0, 0, 0);
        }
        // Điện tích lan ra quanh trung tâm
        for (int i = 0; i < 32; i++) {
            double angle = (i / 32.0) * Math.PI * 2;
            double r = 2.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.ELECTRIC_SPARK, x, y, z,
                (px - x) * 0.1, 0.2, (pz - z) * 0.1);
            addParticle(world, ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
        }
        LOGGER.trace("Skill effect spawnLightningParticles: done");
    }

    /**
     * Earth: Đất/lỏa phun lên từ mặt đất + gợn sóng.
     * Particle: DRIPSTONE_BLOCK, CRIMSON_SPORE
     * Màu sắc: Nâu→Xanh đất
     */
    private static void spawnEarthParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnEarthParticles: start at ({}, {}, {})", x, y, z);
        double height = 10.0;
        // Đất/lỏa phun lên từ mặt đất
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * progress;
            double ox = (world.random.nextDouble() - 0.5) * 2;
            double oz = (world.random.nextDouble() - 0.5) * 2;
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x + ox, py, z + oz, 0, 0.5, 0);
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x + ox * 0.5, py + 0.3, z + oz * 0.5, 0, 0.3, 0);
        }
        // Gợn sóng trên đất
        for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2;
            double r = 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x, y, z,
                (px - x) * 0.1, 0.2, (pz - z) * 0.1);
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x, y, z,
                (px - x) * 0.05, 0.1, (pz - z) * 0.05);
        }
        LOGGER.trace("Skill effect spawnEarthParticles: done");
    }

    /**
     * Wind: Gió XOAY ốc từ trên xuống + luồng khí.
     * Particle: CLOUD, END_ROD
     * Màu sắc: Xanh nhạt→Xám
     */
    private static void spawnWindParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnWindParticles: start at ({}, {}, {})", x, y, z);
        double height = 15.0;
        // Gió XOAY ốc từ trên xuống
        for (int i = 0; i < 15; i++) {
            double progress = (i + world.random.nextDouble()) / 15.0;
            double py = y + height * (1.0 - progress);
            double angle = (System.currentTimeMillis() * 0.005 + progress * Math.PI * 4) % (Math.PI * 2);
            double r = 1.5 + progress * 2.0;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            addParticle(world, ParticleTypes.CLOUD, px, py, pz, 0, -0.5, 0);
            addParticle(world, ParticleTypes.END_ROD, px, py + 0.3, pz, 0, -0.3, 0);
        }
        LOGGER.trace("Skill effect spawnWindParticles: done");
    }

    /**
     * Poison: Khí độc lan tỏa + độc tinh rơi.
     * Particle: SMOKE, CRIMSON_SPORE
     * Màu sắc: Xanh→Tím độc
     */
    private static void spawnPoisonParticles(Level world, double x, double y, double z) {
        LOGGER.trace("Skill effect spawnPoisonParticles: start at ({}, {}, {})", x, y, z);
        double height = 12.0;
        // Độc tinh rơi từ trên xuống
        for (int i = 0; i < 20; i++) {
            double progress = (i + world.random.nextDouble()) / 20.0;
            double py = y + height * (1.0 - progress);
            double ox = (world.random.nextDouble() - 0.5) * 2;
            double oz = (world.random.nextDouble() - 0.5) * 2;
            addParticle(world, ParticleTypes.SMOKE, x + ox, py, z + oz, 0, -0.5, 0);
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x + ox * 0.5, py + 0.3, z + oz * 0.5, 0, -0.3, 0);
        }
        // Mây khí độc lan tỏa
        for (int i = 0; i < 20; i++) {
            double ox = (world.random.nextDouble() - 0.5) * 3;
            double oz = (world.random.nextDouble() - 0.5) * 3;
            double py = y + 0.5 + world.random.nextDouble() * 2;
            addParticle(world, ParticleTypes.SMOKE, x + ox, py, z + oz, 0, 0.2, 0);
            addParticle(world, ParticleTypes.CRIMSON_SPORE, x + ox * 0.5, py + 0.3, z + oz * 0.5, 0, 0.1, 0);
        }
        LOGGER.trace("Skill effect spawnPoisonParticles: done");
    }
}
