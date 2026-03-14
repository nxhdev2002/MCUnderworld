package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Lôi Nha - Thunder Fang skill that fires a shard of compressed lightning.
 * Single target lightning damage.
 * Unique behavior: Chains to nearby enemies and causes temporary stun.
 */
public class ThunderFangSkill extends BaseSkill implements ISingleTargetDamage {

    public static final ThunderFangSkill INSTANCE = new ThunderFangSkill();

    private static final int STUN_DURATION = 20; // 1 second
    private static final int SHOCK_DURATION = 60; // 3 seconds
    private static final int CHAIN_COUNT = 3;
    private static final double CHAIN_RADIUS = 4.0;
    private static final double CHAIN_DAMAGE_MULTIPLIER = 0.6;
    private static final double DIRECT_DAMAGE_MULTIPLIER = 3.0;

    private ThunderFangSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getSkillLocation();
        PlayerAdapter player = ctx.getSkillCaster();

        if (location != null && player != null) {
            // Sound effect: Lightning crack - use AMBIENT_CAVE instead of LIGHTNING_BOLT_THUNDER
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            // Spawn lightning trail
            spawnLightningTrail(location, player, 40);
        }
    }

    /**
     * Spawn lightning trail particles
     */
    private void spawnLightningTrail(Location startLoc, PlayerAdapter player, int segments) {
        if (!(player instanceof FabricPlayerAdapter fabricPlayer)) return;

        ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
        Vec3 start = new Vec3(startLoc.x(), startLoc.y(), startLoc.z());
        Vec3 look = fabricPlayer.getServerPlayer().getLookAngle();

        RandomSource random = level.getRandom();

        // Create jagged lightning bolt
        Vec3 current = start;
        for (int i = 0; i < segments; i++) {
            double progress = (double) i / segments;
            double distance = 3.0 + progress * 20.0;

            // Calculate end point
            Vec3 end = start.add(look.multiply(distance, distance, distance));

            // Add jitter for jagged effect
            double jitterX = (random.nextDouble() - 0.5) * 0.8;
            double jitterY = (random.nextDouble() - 0.5) * 0.8;
            double jitterZ = (random.nextDouble() - 0.5) * 0.8;

            // Lightning particles - use END_ROD instead of SCULK_CHARGE (not available in 1.21.11)
            level.sendParticles(ParticleTypes.END_ROD, current.x + jitterX, current.y + jitterY, current.z + jitterZ, 3, 0.2, 0.2, 0.2, 0.15);

            // White spark particles - END_ROD without color array
            level.sendParticles(ParticleTypes.END_ROD, current.x + jitterX, current.y + jitterY, current.z + jitterZ, 2, 0, 0, 0, 0.3);

            current = end;
        }

        // Final lightning strike - use LARGE_SMOKE is valid
        level.sendParticles(ParticleTypes.LARGE_SMOKE, current.x, current.y, current.z, 5, 0.3, 0.3, 0.3, 0.05);
    }

    /**
     * Spawn chain lightning particles
     */
    private void spawnChainLightning(ServerLevel level, Vec3 source, Vec3 target, RandomSource random) {
        // Chain lightning visual
        Vec3 current = source;
        int segments = 10;
        for (int i = 0; i < segments; i++) {
            double t = (double) i / segments;
            double x = source.x + (target.x - source.x) * t;
            double y = source.y + (target.y - source.y) * t;
            double z = source.z + (target.z - source.z) * t;

            // Add jagged effect
            double jitter = (random.nextDouble() - 0.5) * 0.5;
            x += jitter;
            y += jitter;
            z += jitter;

            // Yellow lightning - use END_ROD instead of SCULK_CHARGE
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 2, 0.1, 0.1, 0.1, 0.1);

            // White sparks - END_ROD without color array
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 0.2);
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Object targetObj = ctx.getTargetEntity();
        if (!(targetObj instanceof LivingEntity target)) return;

        Object casterObj = ctx.getCasterEntity();
        if (!(casterObj instanceof LivingEntity caster)) return;

        // Calculate base damage from caster's level
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = 12.0 + (casterLevel * 2.2);
        double directDamage = baseDamage * DIRECT_DAMAGE_MULTIPLIER;

        // Damage source with lightning theme
        DamageSource damageSource = ctx.getDamageSource("indirectMagic");
        if (damageSource != null) {
            target.hurt(damageSource, (float) directDamage);
        } else if (target.level() instanceof ServerLevel level) {
            target.hurt(level.damageSources().magic(), (float) directDamage);
        }

        // Sound on hit - use AMBIENT_CAVE instead of ELECTRIC_SPIT
        if (target.level() instanceof ServerLevel level) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 0.8f + ThreadLocalRandom.current().nextFloat() * 0.2f);
        }

        // Apply stun debuff
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);

        if (target.level().getServer() != null) {
            // Use runTaskLater for delayed execution
            target.level().getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                }
            });
        }

        // Spawn lightning strike effect
        if (target.level() instanceof ServerLevel level) {
            spawnChainLightning(level, target.position(), target.position().add(0, -2, 0), target.level().getRandom());
        }

        // Chain to nearby enemies
        chainLightningToNearby(ctx, target, CHAIN_COUNT);
    }

    /**
     * Chain lightning to nearby enemies
     */
    private void chainLightningToNearby(SkillContext ctx, LivingEntity originalTarget, int maxChains) {
        Object casterObj = ctx.getCasterEntity();
        if (!(casterObj instanceof LivingEntity caster)) return;

        if (caster.level() instanceof ServerLevel level) {
            RandomSource random = level.getRandom();

            Vec3 source = originalTarget.position();
            int chainsUsed = 0;

            for (EntityAdapter neighborAdapter : ctx.getTargetsInRadius()) {
                if (!(neighborAdapter instanceof LivingEntity le)) continue;
                if (le == caster || le == originalTarget || !le.isAlive()) continue;

                // Chain lightning
                spawnChainLightning(level, source, le.position(), random);
                level.playSound(null, le.getX(), le.getY(), le.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.8f, 0.5f + random.nextFloat() * 0.3f);

                // Chain damage
                double chainDamage = ctx.getSkillLevel() * 1.5;
                DamageSource ds = ctx.getDamageSource("indirectMagic");
                if (ds != null) {
                    le.hurt(ds, (float) chainDamage);
                } else {
                    le.hurt(level.damageSources().magic(), (float) chainDamage);
                }

                // Apply shock debuff (slowness and weakness) - use MobEffectInstance constructor
                if (le instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, SHOCK_DURATION, 1, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, SHOCK_DURATION, 0, false, false));
                }

                chainsUsed++;
                if (chainsUsed >= maxChains) break;
            }
        }
    }
}
