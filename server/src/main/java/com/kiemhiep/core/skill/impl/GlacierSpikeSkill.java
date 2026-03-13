package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import net.minecraft.util.RandomSource;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Băng isEnabled - Glacier Spike skill that shoots a spike of absolute zero ice.
 * Single target damage with ice effect.
 * Unique behavior: Pierces through enemies, freezing them in place.
 */
public class GlacierSpikeSkill extends BaseSkill implements ISingleTargetDamage {

    public static final GlacierSpikeSkill INSTANCE = new GlacierSpikeSkill();

    private static final int FROZEN_DURATION = 60; // 3 seconds at 20 ticks/sec
    private static final double DAMAGE_MULTIPLIER = 2.5;

    private GlacierSpikeSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "ice");
        }
        Location location = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (location != null && player != null) {
            // Sound effect: Frozen liquid splash - use AMBIENT_CAVE with SoundSource
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 1.5f);
            }

            // Spawn particles along the spike trajectory
            spawnSpikeParticles(location, player, 30);
        }
    }

    /**
     * Spawn Glacier Spike particles - creates a trail of ice shards
     */
    private void spawnSpikeParticles(Location startLoc, PlayerAdapter player, int count) {
        if (!(player instanceof FabricPlayerAdapter fabricPlayer)) return;

        ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
        Vec3 start = new Vec3(startLoc.x(), startLoc.y(), startLoc.z());
        Vec3 look = fabricPlayer.getServerPlayer().getLookAngle();

        RandomSource random = level.getRandom();

        // Create a beam of ice shards
        for (int i = 0; i < count; i++) {
            double progress = (double) i / count;

            // Primary ice particles forming the spike - use END_ROD instead of ICE
            double x = start.x + look.x * (3.0 + progress * 15.0);
            double y = start.y + look.y * (3.0 + progress * 15.0) + Math.sin(progress * Math.PI) * 0.5;
            double z = start.z + look.z * (3.0 + progress * 15.0);

            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 2, 0.1, 0.1, 0.1, 0.02);

            // Secondary snow particles for the aura
            level.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
        }
    }

    /**
     * Create shockwave particles at target location
     */
    private void spawnShockwaveParticles(Vec3 location) {
        // Use skill context to get level - for now just spawn particles at location
        // In a real implementation, you would pass the ServerLevel from the skill context
        // For now, this is a placeholder method that does nothing
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Object targetObj = ctx.getTargetEntity();
        Object casterObj = ctx.getCasterEntity();

        if (!(targetObj instanceof LivingEntity target) || !(casterObj instanceof LivingEntity caster)) return;

        // Calculate base damage from caster's level
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = 10.0 + (casterLevel * 2.5);
        double finalDamage = baseDamage * DAMAGE_MULTIPLIER;

        // Apply freeze debuff (freeze target in place)
        if (target.level() instanceof ServerLevel level) {
            // Freeze effect: disable movement
            target.setNoGravity(true);
            target.setDeltaMovement(new Vec3(0, target.getDeltaMovement().y(), 0));

            // Schedule unfreeze task
            level.getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                }
            });
        }

        // Damage source with ice theme - use damageSources().magic()
        if (target.level() instanceof ServerLevel level) {
            target.hurt(level.damageSources().magic(), (float) finalDamage);
        }

        // Sound on hit - use AMBIENT_CAVE with SoundSource
        if (target.level() instanceof ServerLevel level) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.5f + ThreadLocalRandom.current().nextFloat() * 0.25f);
        }

        // Shockwave effect at hit location
        spawnShockwaveParticles(target.position());
    }
}
