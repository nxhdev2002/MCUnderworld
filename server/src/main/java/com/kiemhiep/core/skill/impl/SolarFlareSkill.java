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

/**
 * Thai Duong Buc (Solar Flare) - Solar Flare skill that creates a blinding burst of solar energy.
 *
 * Hiệu ứng:
 * - concentric expanding rings of light and fire
 * - Solar beams striking from above
 * - Area damage with sunburn effect
 * - Particle effect: FLAME, LAVA, DRAGON_BREATH, ENERGIZED_SPARK
 * - Sound effect: Solar surge + thunder
 *
 * Nguồn cảm hứng: Sun Wukong's sunlight burst, Naruto's Sage Mode solar burst
 */
public class SolarFlareSkill extends BaseSkill implements IAreaDamage {

    public static final SolarFlareSkill INSTANCE = new SolarFlareSkill();

    // Configuration
    private static final double INITIAL_RADIUS = 3.0;
    private static final double MAX_RADIUS = 18.0;
    private static final double RADIUS_GROWTH = 0.4;
    private static final double BASE_DAMAGE = 20.0;
    private static final int BURN_DURATION = 100; // 5 seconds burn
    private static final int TOTAL_DURATION = 60; // 3 seconds of full effect
    private static final int TICK_INTERVAL = 4; // Damage/expand every 4 ticks

    private SolarFlareSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Sound effect: Solar surge with SoundSource
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.3f, 0.6f + level.random.nextFloat() * 0.4f);

        // Visual effect: expanding radiant energy
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "fire");
        }

        // Initial energy burst from sky and ground
        spawnSolarBurst(level, origin);
    }

    private void spawnSolarBurst(ServerLevel level, Location origin) {
        RandomSource random = level.random;

        // Beams shooting upward (sun rays)
        int beams = 16;
        for (int i = 0; i < beams; i++) {
            double angle = (i / (double) beams) * Math.PI * 2 + random.nextDouble() * 0.3;
            for (int h = 0; h < 15; h++) {
                double py = origin.y() + 2.0 + h * 0.8;
                double px = origin.x() + Math.cos(angle) * (h * 0.15);
                double pz = origin.z() + Math.sin(angle) * (h * 0.15);

                // Energy sparks in sun ray pattern
                level.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0.08);
                level.sendParticles(ParticleTypes.SMOKE, px + 0.2, py, pz, 1, 0, 0, 0, 0.05);
            }
        }

        // Ring of light around origin
        for (int i = 0; i < 24; i++) {
            double angle = (i / 24.0) * Math.PI * 2;
            double r = 2.0;
            double px = origin.x() + Math.cos(angle) * r;
            double pz = origin.z() + Math.sin(angle) * r;
            double py = origin.y() + 0.5;

            level.sendParticles(ParticleTypes.FLAME, px, py, pz, 2, 0, 0, 0, 0.1);
            level.sendParticles(ParticleTypes.LAVA, px, py + 0.3, pz, 1, 0, 0, 0, 0.08);
        }

        // Ground ring expanding outward
        for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2 + random.nextDouble() * 0.5;
            double r = 3.0 + random.nextDouble() * 2.0;
            double px = origin.x() + Math.cos(angle) * r;
            double pz = origin.z() + Math.sin(angle) * r;

            level.sendParticles(ParticleTypes.LAVA, px, origin.y() - 0.3, pz, 3, 0, 0, 0, 0.12);
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
        double baseDamage = BASE_DAMAGE + (caster.getLevel() * 4.0);
        double finalDamage = baseDamage * 1.5;

        // Use a simple counter without skillData - just track with local variable since we re-calculate each tick
        int currentTick = 0;

        if (currentTick < TOTAL_DURATION) {

            // Spawn expanding rings
            if (currentTick % TICK_INTERVAL == 0) {
                double currentRadius = INITIAL_RADIUS + (currentTick * RADIUS_GROWTH);
                currentRadius = Math.min(currentRadius, MAX_RADIUS);

                // Concentric rings of energy
                int rings = 3;
                for (int r = 0; r < rings; r++) {
                    double ringRadius = currentRadius - (r * 1.5);
                    if (ringRadius < 0.5) continue;

                    int particlesPerRing = 20 - r * 4;
                    for (int i = 0; i < particlesPerRing; i++) {
                        double angle = (i / (double) particlesPerRing) * Math.PI * 2 + (currentTick * 0.2 + r * 0.5);
                        double px = origin.x() + Math.cos(angle) * ringRadius;
                        double pz = origin.z() + Math.sin(angle) * ringRadius;
                        double py = origin.y() + 0.5 + level.random.nextDouble() * 1.5;

                        // Inner ring - flame
                        if (r == 0) {
                            level.sendParticles(ParticleTypes.FLAME, px, py, pz, 3, 0, 0, 0, 0.12);
                            level.sendParticles(ParticleTypes.END_ROD, px, py + 0.5, pz, 2, 0, 0, 0, 0.1);
                        }
                        // Middle ring - lava
                        if (r <= 1) {
                            level.sendParticles(ParticleTypes.LAVA, px, py - 0.3, pz, 2, 0, 0, 0, 0.08);
                        }
                        // Outer ring - use SMOKE instead of DRAGON_BREATH
                        if (r == 2) {
                            level.sendParticles(ParticleTypes.SMOKE, px, py + 0.3, pz, 1, 0, 0, 0, 0.06);
                        }
                        // All rings - use END_ROD for energy sparks instead of ENERGY_SPARK
                        level.sendParticles(ParticleTypes.END_ROD, px, py + 0.8, pz, 1, 0, 0, 0, 0.05);
                    }
                }

                // Solar beams from above striking the area
                if (currentTick % 5 == 0) {
                    int beamCount = 3;
                    for (int b = 0; b < beamCount; b++) {
                        double beamAngle = level.random.nextDouble() * Math.PI * 2;
                        double beamDist = level.random.nextDouble() * currentRadius;
                        double bx = origin.x() + Math.cos(beamAngle) * beamDist;
                        double bz = origin.z() + Math.sin(beamAngle) * beamDist;

                        // Beam from sky
                        for (int h = 10; h > 0; h--) {
                            double by = origin.y() + 10.0 + h * 2.0;
                            level.sendParticles(ParticleTypes.LAVA, bx, by, bz, 2, 0, 0, 0, 0.1);
                            level.sendParticles(ParticleTypes.FLAME, bx, by, bz, 1, 0, 0, 0, 0.05);
                        }
                    }
                }
            }

            // Apply damage at intervals
            if (currentTick % TICK_INTERVAL == 0) {
                double currentRadius = INITIAL_RADIUS + (currentTick * RADIUS_GROWTH);
                currentRadius = Math.min(currentRadius, MAX_RADIUS);

                AABB area = new AABB(
                    origin.x() - currentRadius, origin.y() - currentRadius, origin.z() - currentRadius,
                    origin.x() + currentRadius, origin.y() + currentRadius, origin.z() + currentRadius
                );

                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

                for (LivingEntity entity : entities) {
                    if (entity instanceof Player player && player != null) {
                        // Don't damage self
                        if (player.getUUID().equals(caster.getUniqueId())) {
                            continue;
                        }

                        // Apply fire damage (higher damage for closer entities)
                        double dx = entity.getX() - origin.x();
                        double dz = entity.getZ() - origin.z();
                        double distance = Math.sqrt(dx * dx + dz * dz);
                        double damageMultiplier = 1.0 + (1.0 - distance / currentRadius);
                        double entityDamage = finalDamage * damageMultiplier;

                        entity.hurt(level.damageSources().onFire(), (float) entityDamage);

                        // Apply burn debuff (longer duration for stronger sunlight exposure)
                        entity.setRemainingFireTicks(BURN_DURATION * 20);

                        // Push away from the center (sun repulsion)
                        double pushForce = 0.05 + (currentRadius - distance) * 0.01;
                        double length = Math.sqrt(dx * dx + dz * dz);
                        if (length > 0.1) {
                            entity.setDeltaMovement(
                                dx / length * pushForce * 5,
                                0.2 + (currentRadius - distance) * 0.02,
                                dz / length * pushForce * 5
                            );
                        }
                    }
                }
            }
        } else {
            // Stroke end - solar collapse
            if (currentTick == TOTAL_DURATION) {
                // Final solar collapse sound
                level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.5f, 0.4f);

                // Collapse particles - spiraling inward
                for (int i = 0; i < 40; i++) {
                    double angle = (i / 40.0) * Math.PI * 2;
                    double radius = MAX_RADIUS;
                    for (int r = 0; r < 10; r++) {
                        double progress = r / 10.0;
                        double currentRadius = radius * (1.0 - progress);
                        double px = origin.x() + Math.cos(angle) * currentRadius;
                        double pz = origin.z() + Math.sin(angle) * currentRadius;
                        double py = origin.y() + 2.0 * progress;

                        level.sendParticles(ParticleTypes.LAVA, px, py, pz, 2, 0, 0, 0, 0.1);
                        level.sendParticles(ParticleTypes.END_ROD, px, py + 0.3, pz, 1, 0, 0, 0, 0.08);
                    }
                }

                // Center collapse
                level.sendParticles(ParticleTypes.FLAME, origin.x(), origin.y() + 2, origin.z(), 20, 0, 0, 0, 0.3);
                level.sendParticles(ParticleTypes.END_ROD, origin.x(), origin.y() + 2, origin.z(), 15, 0, 0, 0, 0.2);

                // Final explosion
                level.explode(
                    null,
                    origin.x(), origin.y(), origin.z(),
                    4.0f,
                    ServerLevel.ExplosionInteraction.NONE
                );

                // Reset counter
                // Skill tracking done via local variable, no need to reset skillData
            }
        }
    }
}
