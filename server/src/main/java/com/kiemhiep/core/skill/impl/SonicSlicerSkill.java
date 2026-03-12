package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
 * Siêu_Am_Cắt - Sonic Slicer skill that cuts at supersonic speeds.
 * Single target with speed-based attack.
 * Unique behavior: Creates a supersonic speed wave with visual distortion
 * followed by a cutting shockwave that deals massive damage.
 */
public class SonicSlicerSkill extends BaseSkill implements ISingleTargetDamage {

    public static final SonicSlicerSkill INSTANCE = new SonicSlicerSkill();

    private static final int DISTORTION_DURATION = 80; // 4 seconds
    private static final double DAMAGE_BASE = 12.0;
    private static final double SHOCKWAVE_RADIUS = 4.0;
    private static final int KNOCKBACK_STRENGTH = 8;
    private static final int MAX_SLICES = 5;
    private static final RandomSource RANDOM = RandomSource.create();

    private SonicSlicerSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getSkillLocation();
        PlayerAdapter player = ctx.getSkillCaster();

        if (location != null && player != null) {
            // Spawn initial speed particles
            LivingEntity caster = (LivingEntity) ctx.getCasterEntity();
            spawnSupersonicTrail(location, caster, ctx.getSkillLevel());

            // Sound effect: Supersonic crack - get ServerLevel from player adapter
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(),
                        SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.5f + ThreadLocalRandom.current().nextFloat() * 0.3f);
            }
        }
    }

    /**
     * Spawn supersonic particles for the speed trail
     */
    private void spawnSupersonicTrail(Location start, LivingEntity caster, int skillLevel) {
        if (!(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        Vec3 direction = caster.getLookAngle().normalize();

        // Primary high-speed trail
        for (int i = 0; i < 25 + skillLevel; i++) {
            double t = i / (25.0 + skillLevel);
            double x = start.x() + direction.x() * t * 8.0;
            double y = start.y() + direction.y() * t * 8.0;
            double z = start.z() + direction.z() * t * 8.0;

            // SMOKE for visual distortion (replaces TINTED_GLASS)
            level.sendParticles(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.4,
                    y + (random.nextDouble() - 0.5) * 0.4,
                    z + (random.nextDouble() - 0.5) * 0.4,
                    2,
                    direction.x() * 0.5, 0, direction.z() * 0.5,
                    0.2);

            // White smoke for speed blur (SMOKE version)
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    1,
                    direction.x() * 0.2, 0.02, direction.z() * 0.2,
                    0.1);

            // End rod sparks for high speed
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD,
                        x + (random.nextDouble() - 0.5) * 0.3,
                        y + (random.nextDouble() - 0.5) * 0.3,
                        z + (random.nextDouble() - 0.5) * 0.3,
                        1,
                        direction.x() * 0.8, 0.2, direction.z() * 0.8,
                        0.25);
            }
        }

        // Secondary slicing paths (multiple slices)
        int numSlices = Math.min(MAX_SLICES, 2 + skillLevel / 2);
        for (int slice = 1; slice < numSlices; slice++) {
            for (int i = 0; i < 15; i++) {
                double t = i / 15.0;
                double baseX = start.x() + direction.x() * t * 6.0;
                double baseY = start.y() + direction.y() * t * 6.0;
                double baseZ = start.z() + direction.z() * t * 6.0;

                // apply rotation for extra slice
                double offsetX = direction.z() * t * 1.5 * (slice - (numSlices - 1) / 2.0) * 0.3;
                double offsetZ = -direction.x() * t * 1.5 * (slice - (numSlices - 1) / 2.0) * 0.3;

                double x = baseX + offsetX;
                double y = baseY;
                double z = baseZ + offsetZ;

                // Faint trails for secondary slices
                level.sendParticles(ParticleTypes.CLOUD,
                        x, y, z,
                        1,
                        direction.x() * 0.3, 0.05, direction.z() * 0.3,
                        0.15);

                // SMOKE for speed impression (replaces TINTED_GLASS)
                level.sendParticles(ParticleTypes.SMOKE,
                        x + (random.nextDouble() - 0.5) * 0.2,
                        y + (random.nextDouble() - 0.5) * 0.2,
                        z + (random.nextDouble() - 0.5) * 0.2,
                        1,
                        0, 0, 0,
                        0.1);
            }
        }

        // Shockwave burst at the end of trail
        double trailEndX = start.x() + direction.x() * 8.0;
        double trailEndY = start.y() + direction.y() * 8.0;
        double trailEndZ = start.z() + direction.z() * 8.0;

        for (int i = 0; i < 20; i++) {
            double angle = (System.currentTimeMillis() / 15.0) + (i * (Math.PI * 2) / 20);
            double radius = 1.0 + random.nextDouble() * 1.5;

            double x = trailEndX + Math.cos(angle) * radius;
            double z = trailEndZ + Math.sin(angle) * radius;

            // Fast expanding ring - SMOKE (replaces TINTED_GLASS)
            level.sendParticles(ParticleTypes.SMOKE,
                    x, trailEndY, z,
                    3,
                    Math.cos(angle) * 0.4, 0.15, Math.sin(angle) * 0.4,
                    0.3);

            // White smoke expanding
            level.sendParticles(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.3,
                    trailEndY + (random.nextDouble() - 0.5) * 0.3,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    2,
                    Math.cos(angle) * 0.2, 0.1, Math.sin(angle) * 0.2,
                    0.2);
        }

        // Center shockwave burst
        for (int i = 0; i < 15; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    trailEndX, trailEndY, trailEndZ,
                    1,
                    0, 0.4, 0,
                    0.4);
        }
    }

    /**
     * Spawn the cutting shockwave particles
     */
    private void spawnShockwave(Location center, LivingEntity caster) {
        if (!(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        Vec3 direction = caster.getLookAngle().normalize();

        // Circular shockwave
        for (int i = 0; i < 30; i++) {
            double angle = (System.currentTimeMillis() / 10.0) + (i * (Math.PI * 2) / 30);
            double radius = 2.0 + random.nextDouble() * 2.0;

            double x = center.x() + Math.cos(angle) * radius;
            double z = center.z() + Math.sin(angle) * radius;

            // SMOKE for shockwave edge (replaces TINTED_GLASS)
            level.sendParticles(ParticleTypes.SMOKE,
                    x, center.y(), z,
                    2,
                    direction.x() * 0.3 + Math.cos(angle) * 0.1, 0.1, direction.z() * 0.3 + Math.sin(angle) * 0.1,
                    0.25);

            // SMOKE for compressed air
            level.sendParticles(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.3,
                    center.y() + (random.nextDouble() - 0.5) * 0.3,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    1,
                    direction.x() * 0.2, 0.05, direction.z() * 0.2,
                    0.2);
        }

        // Air blade trail
        for (int i = 0; i < 10; i++) {
            double t = i / 10.0;
            double x = center.x() + direction.x() * t * 6.0;
            double y = center.y() + direction.y() * t * 6.0;
            double z = center.z() + direction.z() * t * 6.0;

            // SMOKE for blade edge (replaces TINTED_GLASS)
            level.sendParticles(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    y + (random.nextDouble() - 0.5) * 0.5,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    1,
                    direction.x() * 0.4, 0.1, direction.z() * 0.4,
                    0.2);

            // SMOKE trail
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.15);
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Location start = ctx.getSkillLocation();
        Object casterObj = ctx.getCasterEntity();
        if (casterObj == null) return;
        LivingEntity caster = (LivingEntity) casterObj;

        if (start == null || caster == null) return;

        // Spawn shockwave
        spawnShockwave(start, caster);

        // Calculate damage
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = DAMAGE_BASE + (casterLevel * 1.5);

        // Get forward direction
        Vec3 direction = caster.getLookAngle().normalize();

        // Check for entities in path
        AABB pathArea = new AABB(
                start.x() - 1.0, start.y() - 1.0, start.z() - 1.0,
                start.x() + 1.0, start.y() + 1.0, start.z() + 1.0
        );

        List<LivingEntity> targets = ctx.getEntitiesWithinArea(pathArea).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());
        RandomSource random = RANDOM;

        int hitCount = 0;
        for (LivingEntity target : targets) {
            if (target == caster || !target.isAlive()) continue;

            // Check if target is in front
            Vec3 toTarget = new Vec3(target.getX() - start.x(), 0, target.getZ() - start.z()).normalize();
            double dot = toTarget.dot(direction);
            if (dot < 0.6) continue; // Not in front

            hitCount++;
            // Damage increases with each hit (up to a point)
            double damage = baseDamage * (0.7 + hitCount * 0.1);

            // Apply damage - useDamageSource factory pattern
            target.hurt(target.damageSources().indirectMagic((net.minecraft.world.entity.Entity) ctx.getCasterEntity(), caster), (float) damage);

            // Strong knockback
            Vec3 knockback = new Vec3(
                    direction.x() * KNOCKBACK_STRENGTH,
                    0.5 + random.nextDouble() * 0.3,
                    direction.z() * KNOCKBACK_STRENGTH
            ).normalize().scale(1.5);

            target.push(knockback.x(), knockback.y(), knockback.z());

            // Apply sound crack
            if (target.level() instanceof ServerLevel targetLevel) {
                targetLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.PLAYERS, 0.7f, 1.5f + random.nextFloat() * 0.3f);
            }

            if (target instanceof Player player) {
                // Sound speed effect (confusion)
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, DISTORTION_DURATION, 0, false, false));
                // Weakness from impact
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DISTORTION_DURATION, 1, false, false));

                // Create visual distortion for player - use correct signature
                if (player.level() instanceof ServerLevel playerLevel) {
                    playerLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.5f);
                }
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, DISTORTION_DURATION, 0, false, false));
            }
        }

        // Shockwave area damage (secondary targets)
        AABB shockwaveArea = new AABB(
                start.x() - SHOCKWAVE_RADIUS, start.y() - 1.0, start.z() - SHOCKWAVE_RADIUS,
                start.x() + SHOCKWAVE_RADIUS, start.y() + 2.0, start.z() + SHOCKWAVE_RADIUS
        );

        List<LivingEntity> shockwaveTargets = ctx.getEntitiesWithinArea(shockwaveArea).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());
        for (LivingEntity target : shockwaveTargets) {
            if (target == caster || !target.isAlive()) continue;

            // Check if in path
            Vec3 toTarget = new Vec3(target.getX() - start.x(), 0, target.getZ() - start.z()).normalize();
            double dot = toTarget.dot(direction);
            if (dot < 0.3) continue;

            double distance = Math.sqrt(Math.pow(target.getX() - start.x(), 2) + Math.pow(target.getZ() - start.z(), 2));
            double shockwaveDamage = baseDamage * 0.3 * (1.0 - Math.min(1.0, distance / SHOCKWAVE_RADIUS));

            target.hurt(target.damageSources().indirectMagic((net.minecraft.world.entity.Entity) ctx.getCasterEntity(), caster), (float) shockwaveDamage);

            // Knockback from shockwave
            Vec3 waveKnockback = new Vec3(
                    direction.x() * KNOCKBACK_STRENGTH * 0.4,
                    0.2 + random.nextDouble() * 0.2,
                    direction.z() * KNOCKBACK_STRENGTH * 0.4
            );

            target.push(waveKnockback.x(), waveKnockback.y(), waveKnockback.z());

            // Apply wind blade effect (weaker)
            if (target instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, DISTORTION_DURATION, 0, false, false));
            }
        }
    }
}
