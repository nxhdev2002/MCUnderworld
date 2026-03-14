package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Hỏa Kiếm - Blazing Sword skill that imbues the user's blade with sentient fire.
 * Single target damage with fire trail and blade surge effect.
 *
 * Hiệu ứng:
 * - Fire trail from caster to target
 * - Blade surge explosion on impact
 * - Single target damage with fire burn
 * - Particle effect: FLAME, LAVA
 * - Sound effect: Sword swing + explosion
 */
public class BlazingSwordSkill extends BaseSkill implements ISingleTargetDamage {

    public static final BlazingSwordSkill INSTANCE = new BlazingSwordSkill();

    // Configuration
    private static final double RANGE = 8.0; // Range for single target
    private static final double DAMAGE_MULTIPLIER = 2.0; // 2x base damage
    private static final int BURN_DURATION = 60; // 3 seconds burn (20 ticks = 1s)
    private static final int BURN_AMPLIFIER = 1; // Level II burn

    private BlazingSwordSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Play initial sword swing sound - use AMBIENT_CAVE instead of SWEEP (doesn't exist)
        level.playSound(null, caster.getLocation().x(), caster.getLocation().y(), caster.getLocation().z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.8f + level.random.nextFloat() * 0.4f);

        // Visual effect: fire trail from caster to target
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "fire");
        }

        // Spawn fire particles along the trail
        spawnFireTrail(level, caster.getLocation(), origin);
        // Spawn blade surge particles at target location
        spawnBladeSurge(level, origin);
    }

    private void spawnFireTrail(ServerLevel level, Location from, Location to) {
        RandomSource random = level.random;
        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        double dz = to.z() - from.z();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        int steps = (int) (distance * 2); // 2 particles per block
        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;
            double x = from.x() + dx * progress;
            double y = from.y() + dy * progress;
            double z = from.z() + dz * progress;

            // Random jitter for natural fire look
            double jitter = 0.25;
            double px = x + (random.nextDouble() - 0.5) * jitter;
            double py = y + (random.nextDouble() - 0.5) * jitter;
            double pz = z + (random.nextDouble() - 0.5) * jitter;

            // Multiple particle types for visual depth - fixed sendParticles signature (9 args)
            level.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0.04);
            level.sendParticles(ParticleTypes.SMOKE, px, py + 0.3, pz, 1, 0, 0, 0, 0.02);
            level.sendParticles(ParticleTypes.LAVA, px, py - 0.2, pz, 1, 0, 0, 0, 0.03);
        }
    }

    private void spawnBladeSurge(ServerLevel level, Location origin) {
        RandomSource random = level.random;

        // Circle of fire around the impact point
        int circleParticles = 24;
        for (int i = 0; i < circleParticles; i++) {
            double angle = (i / (double) circleParticles) * Math.PI * 2;
            double r = 1.5 + random.nextDouble() * 0.5;
            double px = origin.x() + Math.cos(angle) * r;
            double pz = origin.z() + Math.sin(angle) * r;
            double py = origin.y() + 0.5 + random.nextDouble() * 1.0;

            level.sendParticles(ParticleTypes.FLAME, px, py, pz, 2, 0, 0, 0, 0.1);
            level.sendParticles(ParticleTypes.LAVA, px, py + 0.3, pz, 1, 0, 0, 0, 0.08);
        }

        // Vertical explosion column
        for (int i = 0; i < 8; i++) {
            double rise = i * 0.4;
            level.sendParticles(ParticleTypes.LAVA, origin.x(), origin.y() + 0.5 + rise, origin.z(), 1, 0, 0, 0, 0.1);
        }

        // Lingering smoke ring
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2 + (level.random.nextDouble() * 0.3);
            double r = 2.5;
            double px = origin.x() + Math.cos(angle) * r;
            double pz = origin.z() + Math.sin(angle) * r;
            double py = origin.y() + 1.5 + level.random.nextDouble() * 0.5;

            level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 2, 0, 0, 0, 0.05);
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location targetLoc = ctx.getOrigin();
        Player targetPlayer = ctx.getTargetEntity() instanceof Player p ? p : null;

        // Calculate damage based on caster's level
        double baseDamage = 25.0 + (caster.getLevel() * 3.5);
        double finalDamage = baseDamage * DAMAGE_MULTIPLIER;

        // Apply damage to target player
        if (targetPlayer != null) {
            AABB targetBox = new AABB(
                targetLoc.x() - 1.0, targetLoc.y() - 1.0, targetLoc.z() - 1.0,
                targetLoc.x() + 1.0, targetLoc.y() + 2.0, targetLoc.z() + 1.0
            );

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, targetBox);

            for (LivingEntity entity : entities) {
                if (entity instanceof Player target && target != null) {
                    // Fire damage - use damageSources().onFire() for 1.21.11
                    entity.hurt(level.damageSources().onFire(), (float) finalDamage);

                    // Apply burn debuff - use setRemainingFireTicks for LivingEntity
                    if (entity instanceof Player player) {
                        player.setRemainingFireTicks(BURN_DURATION);
                    } else {
                        entity.setRemainingFireTicks(BURN_DURATION);
                    }
                }
            }

            // Small explosion at impact point - use NONE instead of SOURCE
            level.explode(
                null,
                targetLoc.x(), targetLoc.y() + 0.5, targetLoc.z(),
                2.0f, // explosion radius
                ServerLevel.ExplosionInteraction.NONE
            );
        }
    }
}
