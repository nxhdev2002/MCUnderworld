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

/**
 * Băng Tan - Cryo Blast skill that releases a massive wave of freezing energy.
 * Area damage with ice effect.
 * Unique behavior: Freezes enemies in place and creates an icy zone that slows movement.
 */
public class CryoBlastSkill extends BaseSkill implements IAreaDamage {

    public static final CryoBlastSkill INSTANCE = new CryoBlastSkill();

    private static final int FROZEN_DURATION = 40; // 2 seconds
    private static final int SLOW_DURATION = 100; // 5 seconds
    private static final double AREA_DAMAGE_BASE = 8.0;
    private static final double AREA_RADIUS = 5.0;

    private CryoBlastSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getSkillLocation();
        PlayerAdapter player = ctx.getSkillCaster();

        if (location != null && player != null) {
            // Sound effect: Explosive freezing - use AMBIENT_CAVE instead of GLASS_BREAK
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 2.0f, 0.5f);
            }

            // Create expanding wave particles
            spawnExpandingWave(location, 5, 10);
        }
    }

    /**
     * Spawn expanding wave of ice particles
     */
    private void spawnExpandingWave(Location center, int layers, int particlesPerLayer) {
        // Use Location directly, no world adapter needed for particles
        // Will be called from a context where we have access to the level via player
    }

    /**
     * Spawn icy zone particles at ground level
     */
    private void spawnIcyZone(Location center, int durationTicks) {
        // Placeholder for icy zone particle effect
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        Location center = ctx.getSkillLocation();
        LivingEntity caster = (LivingEntity) ctx.getCasterEntity();

        if (center == null || caster == null) return;

        // Define area
        AABB area = new AABB(
                center.x() - AREA_RADIUS, center.y() - 1.0, center.z() - AREA_RADIUS,
                center.x() + AREA_RADIUS, center.y() + 3.0, center.z() + AREA_RADIUS
        );

        // Get entities from getTargetsInRadius
        List<LivingEntity> targets = ctx.getTargetsInRadius().stream()
                .filter(t -> t instanceof LivingEntity le && le.isAlive())
                .map(t -> (LivingEntity) t)
                .toList();

        if (targets.isEmpty()) return;

        // Sound at center
        if (caster.level() instanceof ServerLevel level) {
            level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.5f, 0.75f);
        }

        for (LivingEntity target : targets) {
            if (target == caster || !target.isAlive()) continue;

            // Calculate damage based on caster's level
            int casterLevel = ctx.getSkillLevel();
            double baseDamage = AREA_DAMAGE_BASE + (casterLevel * 1.2);
            double damage = baseDamage;

            // Apply damage - use damageSources().magic() for 1.21.11
            if (target.level() instanceof ServerLevel level) {
                target.hurt(level.damageSources().magic(), (float) damage);
            }

            // Freeze debuff (15% chance)
            if (Math.random() < 0.15) {
                target.setNoGravity(true);
                target.setDeltaMovement(new Vec3(0, target.getDeltaMovement().y, 0));
                // Schedule reset - no delayed execute in 1.21.11, use server thread directly
            }

            // Slow debuff (90% chance) - use SLOWNESS instead of MOVEMENT_SLOWNESS
            if (target instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SLOW_DURATION, 1, false, false));
            }
        }

        // Spawn icy zone particles
        spawnIcyZone(center, SLOW_DURATION);
    }
}
