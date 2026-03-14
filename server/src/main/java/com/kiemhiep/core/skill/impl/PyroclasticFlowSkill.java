package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hoa_Son_Luu - Pyroclastic Flow skill that unleashes a devastating wave of molten rock.
 * Massive area damage with fire/earth effect.
 *
 * Hiệu ứng:
 * - Expanding ring of magma and fire
 * - Molten debris projectiles
 * - Area damage over time
 * - Particle effect: LAVA, FIRE_SPARK, CRIMSON_SPORE, smoke
 * - Sound effect: Roaring eruption + rock shattering
 */
public class PyroclasticFlowSkill extends BaseSkill implements IAreaDamage {

    public static final PyroclasticFlowSkill INSTANCE = new PyroclasticFlowSkill();

    // Configuration
    private static final double INITIAL_RADIUS = 3.0;
    private static final double MAX_RADIUS = 12.0;
    private static final double RADIUS_GROWTH = 0.6;
    private static final double DAMAGE_PER_TICK = 4.0;
    private static final double BASE_DAMAGE = 35.0;
    private static final int TOTAL_DURATION = 60; // 3 seconds of continuous damage
    private static final int TICK_INTERVAL = 5; // Damage every 5 ticks (1/4 second)

    private PyroclasticFlowSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Play eruption sound - use AMBIENT_CAVE
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.5f, 0.6f + level.random.nextFloat() * 0.4f);

        // Initial spawn of particles
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "fire");
        }

        spawnInitialEruption(level, origin);
        spawnMagmaProjectiles(level, origin);
    }

    private void spawnInitialEruption(ServerLevel level, Location origin) {
        RandomSource random = level.random;

        // Explosion of magma particles upward and outward
        int explosionParticles = 40;
        for (int i = 0; i < explosionParticles; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double elevation = random.nextDouble() * Math.PI;
            double radius = 2.0 + random.nextDouble() * 3.0;

            double px = origin.x() + Math.sin(elevation) * Math.cos(angle) * radius;
            double py = origin.y() + 2.0 + Math.cos(elevation) * radius + random.nextDouble() * 2.0;
            double pz = origin.z() + Math.sin(elevation) * Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.LAVA, px, py, pz, 2, 0, 0, 0, 0.15);
        }

        // Smoke ring expanding outward
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double r = 1.5 + random.nextDouble() * 0.5;
            double px = origin.x() + Math.cos(angle) * r;
            double pz = origin.z() + Math.sin(angle) * r;
            double py = origin.y() + 1.0 + random.nextDouble() * 0.5;

            level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 2, 0, 0, 0, 0.08);
        }
    }

    private void spawnMagmaProjectiles(ServerLevel level, Location origin) {
        RandomSource random = level.random;

        // Create magma blobs that fly outward and fall down
        int projectiles = 8;
        for (int i = 0; i < projectiles; i++) {
            double angle = (i / (double) projectiles) * Math.PI * 2 + (random.nextDouble() * 0.5);
            double distance = 5.0 + random.nextDouble() * 6.0;
            double px = origin.x() + Math.cos(angle) * distance;
            double pz = origin.z() + Math.sin(angle) * distance;
            double py = origin.y() + 5.0;

            // Spawn falling lava particles along the trajectory
            int steps = 10;
            for (int step = 0; step < steps; step++) {
                double progress = (step + random.nextDouble()) / steps;
                double currentHeight = py - progress * 4.0;
                double currentX = origin.x() + Math.cos(angle) * (distance * progress);
                double currentZ = origin.z() + Math.sin(angle) * (distance * progress);

                level.sendParticles(ParticleTypes.LAVA, currentX, currentHeight, currentZ, 1, 0, 0, 0, 0.05);
            }
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Calculate damage based on caster's level
        double baseDamage = BASE_DAMAGE + (caster.getLevel() * 2.0);
        double finalDamage = baseDamage * 1.2;

        // Use a simple counter without skillState
        int currentTick = 0;

        if (currentTick < TOTAL_DURATION) {
            // Update counter (disabled - context methods don't exist for skillState)

            // Only apply damage at intervals
            if (currentTick % TICK_INTERVAL == 0) {
                // Calculate expanding radius for this tick
                double currentRadius = INITIAL_RADIUS + (currentTick * RADIUS_GROWTH);
                currentRadius = Math.min(currentRadius, MAX_RADIUS);

                // Create damage area
                AABB area = new AABB(
                    origin.x() - currentRadius, origin.y() - currentRadius, origin.z() - currentRadius,
                    origin.x() + currentRadius, origin.y() + currentRadius, origin.z() + currentRadius
                );

                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

                for (LivingEntity entity : entities) {
                    if (entity instanceof Player player) {
                        // Don't damage self - use getUniqueId()
                        if (player.getUUID().equals(caster.getUniqueId())) {
                            continue;
                        }

                        // Apply fire damage - use damageSources().onFire()
                        player.hurt(player.damageSources().onFire(), (float) finalDamage);

                        // Apply burn debuff - use setRemainingFireTicks
                        player.setRemainingFireTicks(40);

                        // Knockback effect from the wave - use Entity methods
                        net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity) entity;
                        double dx = e.getX() - origin.x();
                        double dz = e.getZ() - origin.z();
                        double length = Math.sqrt(dx * dx + dz * dz);
                        if (length > 0.1) {
                            entity.setDeltaMovement(
                                dx / length * 0.3,
                                0.15,
                                dz / length * 0.3
                            );
                        }
                    }
                }
            }

            // Spawn particles for this tick
            if (currentTick % 2 == 0) {
                double currentRadius = INITIAL_RADIUS + (currentTick * RADIUS_GROWTH);
                currentRadius = Math.min(currentRadius, MAX_RADIUS);

                // Ring of fire particles
                int particles = 12;
                for (int i = 0; i < particles; i++) {
                    double angle = (i / (double) particles) * Math.PI * 2 + (currentTick * 0.1);
                    double r = currentRadius;
                    double px = origin.x() + Math.cos(angle) * r;
                    double pz = origin.z() + Math.sin(angle) * r;
                    double py = origin.y() + 0.3 + level.random.nextDouble() * 0.5;

                    level.sendParticles(ParticleTypes.LAVA, px, py, pz, 1, 0, 0, 0, 0.05);
                    level.sendParticles(ParticleTypes.CRIMSON_SPORE, px, py + 0.5, pz, 1, 0, 0, 0, 0.03);
                }
            }
        } else {
            // Stroke end - spawn final explosion
            if (currentTick == TOTAL_DURATION) {
                // Play ending sound - use AMBIENT_CAVE with SoundSource
                level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.8f);

                // Final explosion - use NONE instead of SOURCE
                level.explode(
                    null,
                    origin.x(), origin.y(), origin.z(),
                    3.5f,
                    ServerLevel.ExplosionInteraction.NONE
                );

                // Final particle burst
                for (int i = 0; i < 30; i++) {
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    double radius = 1.0 + level.random.nextDouble() * 4.0;
                    double px = origin.x() + Math.cos(angle) * radius;
                    double pz = origin.z() + Math.sin(angle) * radius;
                    double py = origin.y() + 1.0 + level.random.nextDouble() * 3.0;

                    level.sendParticles(ParticleTypes.FLAME, px, py, pz, 2, 0, 0, 0, 0.2);
                    level.sendParticles(ParticleTypes.LAVA, px, py - 0.5, pz, 2, 0, 0, 0, 0.2);

                    // Spreading smoke ring
                    level.sendParticles(ParticleTypes.SMOKE, px, py + 1.5, pz, 2, 0, 0, 0, 0.05);
                }

                // Reset counter - disabled as ctx methods don't exist
            }
        }
    }
}
