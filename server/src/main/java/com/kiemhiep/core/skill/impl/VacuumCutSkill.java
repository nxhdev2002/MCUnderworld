package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
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
 * Hư_Không_Cắt - Vacuum Cut skill that cuts with razor-sharp wind pressure.
 * Single target with precision cutting.
 * Unique behavior: Creates an air pressure wave that causes a sudden vacuum
 * followed by a powerful explosive force.
 */
public class VacuumCutSkill extends BaseSkill implements ISingleTargetDamage {

    public static final VacuumCutSkill INSTANCE = new VacuumCutSkill();

    private static final int VACUUM_DURATION = 40; // 2 seconds
    private static final double DAMAGE_BASE = 8.0;
    private static final double PRESSURE_WAVE_RADIUS = 5.0;
    private static final int KNOCKBACK_STRENGTH = 5;

    private VacuumCutSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (location != null && player != null) {
            // Sound effect: Sudden vacuum suction sound
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.7f + ThreadLocalRandom.current().nextFloat() * 0.2f);
            }

            // Spawn vacuum particles before the cut
            LivingEntity caster = ctx.getCasterEntity() instanceof LivingEntity le ? le : null;
            spawnVacuumEffect(location, caster);
        }
    }

    /**
     * Spawn particles for the vacuum suction effect
     */
    private void spawnVacuumEffect(Location start, LivingEntity caster) {
        if (caster == null || !(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        // Phase 1: Inward suction particles (toward the cutting plane)
        for (int i = 0; i < 30; i++) {
            // Create a circle of particles moving inward
            double angle = (System.currentTimeMillis() / 30.0) + (i * (Math.PI * 2) / 30);
            double radius = 2.0 + random.nextDouble() * 2.0;

            double x = start.x() + Math.cos(angle) * radius;
            double y = start.y() + (random.nextDouble() - 0.5) * 2.0;
            double z = start.z() + Math.sin(angle) * radius;

            // CLOUD particles moving inward
            double moveX = -Math.cos(angle) * 0.3;
            double moveZ = -Math.sin(angle) * 0.3;

            level.sendParticles(ParticleTypes.CLOUD,
                    x, y, z,
                    2,
                    moveX, 0, moveZ,
                    0.15);

            // SMOKE for vacuum effect (replaces WHITE_SMOKE)
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y + 0.3, z,
                    1,
                    moveX * 0.5, 0.05, moveZ * 0.5,
                    0.1);

            // SMOKE for high-pressure boundary
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.SMOKE,
                        start.x(), start.y(), start.z(),
                        1,
                        0, 0, 0,
                        0.2);
            }
        }
    }

    /**
     * Spawn the explosive cut wave particles
     */
    private void spawnCutWave(Location center, LivingEntity caster) {
        if (caster == null || !(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        Vec3 direction = caster.getLookAngle().normalize();

        // Phase 2: Explosive outward wave
        for (int i = 0; i < 40; i++) {
            double angle = (System.currentTimeMillis() / 20.0) + (i * (Math.PI * 2) / 40);
            double radius = 1.0 + random.nextDouble() * 3.0;

            double x = center.x() + Math.cos(angle) * radius;
            double y = center.y() + (random.nextDouble() - 0.5) * 2.0;
            double z = center.z() + Math.sin(angle) * radius;

            // Fast moving particles for the wave
            double moveX = direction.x() * 0.5 + Math.cos(angle) * 0.2;
            double moveZ = direction.z() * 0.5 + Math.sin(angle) * 0.2;

            // SMOKE for compressed air
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    3,
                    moveX, 0.1, moveZ,
                    0.2);

            // END_ROD for high energy
            level.sendParticles(ParticleTypes.END_ROD,
                    x, y + 0.5, z,
                    2,
                    moveX * 0.5, 0.15, moveZ * 0.5,
                    0.3);

            // CLOUD expanding rapidly
            level.sendParticles(ParticleTypes.CLOUD,
                    x + (random.nextDouble() - 0.5) * 0.5,
                    y + (random.nextDouble() - 0.5) * 0.3,
                    z + (random.nextDouble() - 0.5) * 0.5,
                    1,
                    moveX * 0.3, 0.05, moveZ * 0.3,
                    0.1);

            // SMOKE for sharp edge
            if (i % 4 == 0) {
                level.sendParticles(ParticleTypes.SMOKE,
                        center.x() + direction.x() * 2.0,
                        center.y() + direction.y() * 2.0,
                        center.z() + direction.z() * 2.0,
                        1,
                        direction.x() * 0.5, 0.2, direction.z() * 0.5,
                        0.2);
            }
        }
    }

    /**
     * Spawn particles along the cut path
     */
    private void spawnCutPath(Location start, LivingEntity caster) {
        if (caster == null || !(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        Vec3 direction = caster.getLookAngle().normalize();

        // Thin high-speed cut path
        for (int i = 0; i < 20; i++) {
            double t = i / 20.0;
            double x = start.x() + direction.x() * t * 6.0;
            double y = start.y() + direction.y() * t * 6.0;
            double z = start.z() + direction.z() * t * 6.0;

            // SMOKE for sharp edge
            level.sendParticles(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.3,
                    y + (random.nextDouble() - 0.5) * 0.3,
                    z + (random.nextDouble() - 0.5) * 0.3,
                    2,
                    direction.x() * 0.8, 0.1, direction.z() * 0.8,
                    0.15);

            // SMOKE trail for the blade
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.1);

            // End rod sparks for high-speed motion
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.END_ROD,
                        x + (random.nextDouble() - 0.5) * 0.5,
                        y + (random.nextDouble() - 0.5) * 0.5,
                        z + (random.nextDouble() - 0.5) * 0.5,
                        1,
                        0, 0.3, 0,
                        0.2);
            }
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Location start = ctx.getSkillLocation();
        Object casterObj = ctx.getCasterEntity();
        if (casterObj == null || !(casterObj instanceof LivingEntity caster)) return;

        if (start == null) return;

        // Calculate damage based on skill level
        int casterLevel = ctx.getSkillLevel();
        double damage = DAMAGE_BASE + (casterLevel * 1.1);

        // Spawn cut wave effect
        spawnCutWave(start, caster);
        spawnCutPath(start, caster);

        // Get forward direction
        Vec3 direction = caster.getLookAngle().normalize();

        // Check for entities along the path
        AABB pathArea = new AABB(
                start.x() - 1.5, start.y() - 1.5, start.z() - 1.5,
                start.x() + 1.5, start.y() + 1.5, start.z() + 1.5
        );

        List<LivingEntity> targets = ctx.getEntitiesWithinArea(pathArea).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());

        for (LivingEntity target : targets) {
            if (target == caster || !target.isAlive()) continue;

            // Check if target is in front of caster
            Vec3 toTarget = new Vec3(target.getX() - start.x(), 0, target.getZ() - start.z()).normalize();
            double dot = toTarget.dot(direction);
            if (dot < 0.5) continue; // Not in front

            // Apply damage - use direct damage source
            target.hurt(target.damageSources().indirectMagic(caster, caster), (float) damage);

            // Strong knockback in direction
            Vec3 knockback = new Vec3(
                    direction.x() * KNOCKBACK_STRENGTH,
                    0.3 + ThreadLocalRandom.current().nextDouble() * 0.3,
                    direction.z() * KNOCKBACK_STRENGTH
            );

            target.push(knockback.x(), knockback.y(), knockback.z());

            // Apply vacuum debuff (levitation = pulled into suction) with MobEffectInstance wrapper
            if (target instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, VACUUM_DURATION, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, VACUUM_DURATION, 1, false, false));
                // Sound on hit
                if (target.level() instanceof ServerLevel targetLevel) {
                    float pitch = (float) (1.0f + ThreadLocalRandom.current().nextDouble() * 0.3f);
                    targetLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.6f, pitch);
                }
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, VACUUM_DURATION, 0, false, false));
            }
        }

        // Pressure wave area damage (secondary)
        AABB pressureArea = new AABB(
                start.x() - PRESSURE_WAVE_RADIUS, start.y() - 1.0, start.z() - PRESSURE_WAVE_RADIUS,
                start.x() + PRESSURE_WAVE_RADIUS, start.y() + 2.0, start.z() + PRESSURE_WAVE_RADIUS
        );

        List<LivingEntity> pressureTargets = ctx.getEntitiesWithinArea(pressureArea).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());
        for (LivingEntity target : pressureTargets) {
            if (target == caster || !target.isAlive()) continue;

            // Check if in path
            Vec3 toTarget = new Vec3(target.getX() - start.x(), 0, target.getZ() - start.z()).normalize();
            double dot = toTarget.dot(direction);
            if (dot < 0.3) continue;

            double distance = Math.sqrt(Math.pow(target.getX() - start.x(), 2) + Math.pow(target.getZ() - start.z(), 2));
            double pressureDamage = damage * 0.4 * (1.0 - Math.min(1.0, distance / PRESSURE_WAVE_RADIUS));

            target.hurt(target.damageSources().indirectMagic(caster, caster), (float) pressureDamage);

            // Knockback from the wave
            Vec3 waveKnockback = new Vec3(
                    direction.x() * KNOCKBACK_STRENGTH * 0.6,
                    0.2,
                    direction.z() * KNOCKBACK_STRENGTH * 0.6
            );

            target.push(waveKnockback.x(), waveKnockback.y(), waveKnockback.z());

            // Apply wind blade effect (reduced) with MobEffectInstance wrapper
            if (target instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, VACUUM_DURATION, 0, false, false));
            }
        }

        // Final sound: Explosion of air
        PlayerAdapter casterAdapter = ctx.getCaster();
        if (casterAdapter instanceof FabricPlayerAdapter fabricCaster) {
            ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
            float pitch = (float) (0.5f + ThreadLocalRandom.current().nextDouble() * 0.2f);
            level.playSound(null, start.x(), start.y(), start.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, pitch);
        }
    }
}
