package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Long_Tornado - Tornado Sweep skill that creates a massive cyclone to sweep away enemies.
 * Area damage with powerful wind vortex.
 * Unique behavior: Creates a rising tornado vortex with spiraling wind particles
 * that lifts enemies and deals damage over time.
 */
public class TornadoSweepSkill extends BaseSkill implements IAreaDamage {

    public static final TornadoSweepSkill INSTANCE = new TornadoSweepSkill();

    private static final int TORNADO_DURATION = 120; // 6 seconds
    private static final double AREA_DAMAGE_BASE = 5.0;
    private static final double TORNADO_RADIUS = 8.0;
    private static final double TORNADO_HEIGHT = 15.0;
    private static final int KNOCKBACK_STRENGTH = 6;

    private TornadoSweepSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getSkillLocation();
        PlayerAdapter player = ctx.getSkillCaster();

        if (location != null && player != null) {
            // Sound effect: Powerful wind cyclone sound - get ServerLevel from player adapter
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(),
                        SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.2f + ThreadLocalRandom.current().nextFloat() * 0.3f);
            }
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        Location center = ctx.getSkillLocation();
        PlayerAdapter playerAdapter = ctx.getCaster();

        if (center == null || playerAdapter == null) return;

        // Get ServerLevel from player adapter
        if (!(playerAdapter instanceof FabricPlayerAdapter fabricCaster)) return;
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();

        // Define tornado area
        AABB area = new AABB(
                center.x() - TORNADO_RADIUS, center.y() - 2.0, center.z() - TORNADO_RADIUS,
                center.x() + TORNADO_RADIUS, center.y() + TORNADO_HEIGHT, center.z() + TORNADO_RADIUS
        );

        List<LivingEntity> targets = ctx.getEntitiesWithinArea(area).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(java.util.stream.Collectors.toList());

        if (targets.isEmpty()) return;

        // Get caster entity for damage calculation
        Object casterObj = ctx.getCasterEntity();
        if (casterObj == null || !(casterObj instanceof LivingEntity caster)) return;

        // Calculate base damage
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = AREA_DAMAGE_BASE + (casterLevel * 0.7);

        // Get random from ServerLevel
        RandomSource random = level.getRandom();

        for (LivingEntity target : targets) {
            if (target == caster || !target.isAlive()) continue;

            // Damage with slight variation
            double damage = baseDamage * (0.8 + random.nextDouble() * 0.4);

            // Apply damage using direct damage source
            target.hurt(target.damageSources().indirectMagic(caster, caster), (float) damage);

            // Calculate knockback from tornado center
            double dx = target.getX() - center.x();
            double dz = target.getZ() - center.z();
            double distance = Math.sqrt(dx * dx + dz * dz);

            // Knockback patterns based on distance from center
            Vec3 knockback;
            if (distance < TORNADO_RADIUS * 0.5) {
                // Strong upward lift near center
                knockback = new Vec3(
                        -dx * 0.3,
                        1.2 + random.nextDouble() * 0.5,
                        -dz * 0.3
                );
            } else {
                // Outward swirl further from center
                double angle = Math.atan2(dz, dx);
                double swirlForce = 0.8;
                knockback = new Vec3(
                        -Math.sin(angle) * swirlForce,
                        0.5 + random.nextDouble() * 0.3,
                        Math.cos(angle) * swirlForce
                );
            }

            // Normalize and scale
            knockback = knockback.normalize().scale(KNOCKBACK_STRENGTH * 0.5);
            target.push(knockback.x(), knockback.y(), knockback.z());

            // Apply debuffs with MobEffectInstance wrapper
            if (target instanceof Player player) {
                // Levitation for float effect
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, TORNADO_DURATION, 1, false, false));
                // Slow falling to simulate being caught in winds
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, TORNADO_DURATION, 0, false, false));
                // Weakness from being tossed around
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TORNADO_DURATION, 0, false, false));

                // Sound on hit
                target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.4f, 0.8f + random.nextFloat() * 0.3f);
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, TORNADO_DURATION, 1, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, TORNADO_DURATION, 0, false, false));
            }
        }

        // Spawn tornado particles
        spawnTornadoParticles(level, center, casterLevel);

        // Spawn debris
        spawnDebris(level, center);

        // Sound after damage
        level.playSound(null, center.x(), center.y(), center.z(),
                SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.25f);
    }

    /**
     * Spawn tornado particles
     */
    private void spawnTornadoParticles(ServerLevel level, Location center, int skillLevel) {
        int tornadoSegments = 12 + skillLevel; // More segments for higher level
        RandomSource random = level.getRandom();

        // Create rising spiral tornado
        for (int segment = 0; segment < tornadoSegments; segment++) {
            double y = center.y() - 2.0 + (segment * TORNADO_HEIGHT / tornadoSegments);
            double radius = 1.0 + (segment * 0.6);

            // Rotate particles in spiral pattern
            for (int i = 0; i < 6; i++) {
                double angle = (System.currentTimeMillis() / 25.0) + (i * (Math.PI * 2) / 6) + (segment * 0.3);

                double x = center.x() + Math.cos(angle) * radius;
                double z = center.z() + Math.sin(angle) * radius;

                // SMOKE particles for the core (replaces WHITE_SMOKE)
                level.sendParticles(ParticleTypes.SMOKE,
                        x, y, z,
                        2,
                        0, 0.05, 0,
                        0.15);

                // SMOKE for high-speed edge
                level.sendParticles(ParticleTypes.SMOKE,
                        x + (random.nextDouble() - 0.5) * 0.3,
                        y + (random.nextDouble() - 0.5) * 0.2,
                        z + (random.nextDouble() - 0.5) * 0.3,
                        1,
                        0, 0.02, 0,
                        0.1);

                // CLOUD particles for the outer body
                if (i % 2 == 0) {
                    level.sendParticles(ParticleTypes.CLOUD,
                            x + Math.cos(angle) * 0.5,
                            y,
                            z + Math.sin(angle) * 0.5,
                            1,
                            -Math.sin(angle) * 0.05, 0, Math.cos(angle) * 0.05,
                            0.1);
                }

                // END_ROD for energy core
                if (segment % 3 == 0 && i % 3 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD,
                            center.x(), y + 0.5, center.z(),
                            1,
                            0, 0.2, 0,
                            0.2);
                }
            }

            // Spiral particles rising up
            double spiralAngle = (System.currentTimeMillis() / 30.0) + (segment * 0.5);
            double spiralX = center.x() + Math.cos(spiralAngle) * (radius + 1.5);
            double spiralZ = center.z() + Math.sin(spiralAngle) * (radius + 1.5);

            level.sendParticles(ParticleTypes.CLOUD,
                    spiralX, y + 0.5, spiralZ,
                    1,
                    -Math.sin(spiralAngle) * 0.1, 0.1, Math.cos(spiralAngle) * 0.1,
                    0.2);
        }

        // Top swirling vortex
        double topY = center.y() + TORNADO_HEIGHT;
        for (int i = 0; i < 15; i++) {
            double angle = (System.currentTimeMillis() / 20.0) + (i * (Math.PI * 2) / 15);
            double radius = 2.0 + random.nextDouble() * 1.5;

            double x = center.x() + Math.cos(angle) * radius;
            double z = center.z() + Math.sin(angle) * radius;

            // Fast spiraling particles at top - SMOKE
            level.sendParticles(ParticleTypes.SMOKE,
                    x, topY, z,
                    3,
                    -Math.sin(angle) * 0.3, 0.1, Math.cos(angle) * 0.3,
                    0.2);

            // Expand outward
            level.sendParticles(ParticleTypes.CLOUD,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    topY + 0.5,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    2,
                    0, 0.15, 0,
                    0.25);
        }

        // Bottom suction particles
        for (int i = 0; i < 10; i++) {
            double angle = (System.currentTimeMillis() / 40.0) + (i * (Math.PI * 2) / 10);
            double radius = 2.5 + random.nextDouble() * 2.0;

            // Inward spiraling
            double x = center.x() + Math.cos(angle) * radius;
            double z = center.z() + Math.sin(angle) * radius;
            double moveX = -Math.cos(angle) * 0.15;
            double moveZ = -Math.sin(angle) * 0.15;

            level.sendParticles(ParticleTypes.CLOUD,
                    x, center.y() - 1.0, z,
                    2,
                    moveX, -0.05, moveZ,
                    0.15);

            // CRIMSON_SPORE (replaces DUST with color) - earth particles drawn inward
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    x + (random.nextDouble() - 0.5) * 0.3,
                    center.y() - 1.0,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    1,
                    moveX * 0.5, -0.02, moveZ * 0.5,
                    0.1f);
        }
    }

    /**
     * Spawn debris particles from the tornado
     */
    private void spawnDebris(ServerLevel level, Location center) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < 25; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 3.0 + random.nextDouble() * 4.0;
            double startY = center.y();
            double endY = center.y() + TORNADO_HEIGHT;

            // Debris trail
            for (int j = 0; j < 15; j++) {
                double t = j / 15.0;
                double y = startY + (endY - startY) * t;

                // Twisting debris trail
                double twistRadius = radius * (1.0 - t);
                double twistAngle = angle + t * 3.0;
                double tx = center.x() + Math.cos(twistAngle) * twistRadius;
                double tz = center.z() + Math.sin(twistAngle) * twistRadius;

                level.sendParticles(ParticleTypes.CLOUD,
                        tx, y, tz,
                        1,
                        0, 0.02, 0,
                        0.1);

                if (j % 3 == 0) {
                    level.sendParticles(ParticleTypes.SMOKE,
                            tx, y, tz,
                            1,
                            0, 0.01, 0,
                            0.05);
                }
            }
        }
    }
}
