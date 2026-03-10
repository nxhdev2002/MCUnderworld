package com.kiemhiep.network;

import com.kiemhiep.effect.SkySplitEffect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers skill effect payload and spawns particles on the client.
 */
public final class SkillEffectReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    private SkillEffectReceiver() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SkillEffectPayload.TYPE, SkillEffectPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SkillEffectPayload.TYPE, (payload, context) -> {
            double x = payload.x();
            double y = payload.y();
            double z = payload.z();
            String effectType = payload.effectType();
            LOGGER.debug("Skill effect received: skillId={} effectType={} at ({}, {}, {})", payload.skillId(), effectType, x, y, z);
            LOGGER.debug("Skill effect: scheduling spawn on client thread");
            context.client().execute(() -> {
                Level world = context.client().level;
                LOGGER.debug("Skill effect execute: level={} thread={}", world != null ? "non-null" : "NULL", Thread.currentThread().getName());
                if (world == null) {
                    LOGGER.warn("Skill effect: level null when spawning particles, aborting");
                    return;
                }
                LOGGER.debug("Skill effect: calling spawnParticles effectType={} at ({}, {}, {})", effectType, x, y, z);
                spawnParticles(world, x, y, z, effectType);
                LOGGER.debug("Skill effect: spawnParticles returned");
            });
        });
    }

    private static void spawnParticles(Level world, double x, double y, double z, String effectType) {
        LOGGER.debug("Skill effect spawnParticles: effectType={} worldClass={}", effectType, world.getClass().getName());
        switch (effectType) {
            case "thunder" -> spawnThunderParticles(world, x, y, z);
            case "tornado" -> spawnTornadoParticles(world, x, y, z);
            case "tsunami" -> spawnTsunamiParticles(world, x, y, z);
            case "meteor" -> spawnMeteorParticles(world, x, y, z);
            default -> spawnGenericParticles(world, x, y, z);
        }
    }

    /** Spawn particle; use addAlwaysVisibleParticle on ClientLevel so effect is visible at any distance. */
    private static void addParticle(Level world, net.minecraft.core.particles.ParticleOptions type, double x, double y, double z, double vx, double vy, double vz) {
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
        LOGGER.debug("Skill effect spawnThunderParticles: start at ({}, {}, {})", x, y, z);
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
        LOGGER.debug("Skill effect spawnThunderParticles: done");
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

    private static void spawnGenericParticles(Level world, double x, double y, double z) {
        for (int i = 0; i < 8; i++) {
            addParticle(world, ParticleTypes.CLOUD, x, y + 0.5, z,
                (world.random.nextDouble() - 0.5) * 0.2, 0.1, (world.random.nextDouble() - 0.5) * 0.2);
        }
    }
}
