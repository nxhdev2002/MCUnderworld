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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Lien_Hoan_Chich - Chain Thrust skill that rapidly strikes multiple times with lightning speed.
 * Single target with multi-hit potential.
 * Unique behavior: Rapid rapid alternating strikes with lightning trail particles and chain damage.
 */
public class ChainThrustSkill extends BaseSkill implements ISingleTargetDamage {

    public static final ChainThrustSkill INSTANCE = new ChainThrustSkill();

    private static final int STUN_DURATION = 30; // 1.5 seconds
    private static final int SHOCK_DURATION = 90; // 4.5 seconds
    private static final int MULTI_HIT_COUNT = 4;
    private static final int CHAIN_COUNT = 4;
    private static final double CHAIN_RADIUS = 5.0;
    private static final double CHAIN_DAMAGE_MULTIPLIER = 0.7;
    private static final double DIRECT_DAMAGE_MULTIPLIER = 2.8;

    private ChainThrustSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getSkillLocation();
        PlayerAdapter player = ctx.getSkillCaster();

        if (location != null && player != null) {
            // Sound effect: Rapid lightning zaps - use AMBIENT_CAVE instead of ELECTRIC_SPIT
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                for (int i = 0; i < 4; i++) {
                    level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 0.8f + i * 0.1f, 0.5f + ThreadLocalRandom.current().nextFloat() * 0.3f);
                }
            }
            // Spawn initial lightning trail
            spawnInitialTrail(location, player);
        }
    }

    /**
     * Spawn initial lightning trail particles when skill is cast.
     */
    private void spawnInitialTrail(Location startLoc, PlayerAdapter player) {
        if (!(player instanceof FabricPlayerAdapter fabricPlayer)) return;

        ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
        RandomSource random = level.getRandom();

        Vec3 start = new Vec3(startLoc.x(), startLoc.y(), startLoc.z());
        Vec3 look = fabricPlayer.getServerPlayer().getLookAngle();

        // Create multiple jagged lightning paths
        for (int path = 0; path < 3; path++) {
            Vec3 current = start;
            for (int i = 0; i < 30; i++) {
                double progress = (double) i / 30;
                double distance = 2.0 + progress * 25.0;

                // Calculate end point based on look direction
                Vec3 end = start.add(look.multiply(distance, distance, distance));

                // Add path-specific jitter
                double jitterX = (random.nextDouble() - 0.5) * (0.5 + path * 0.2);
                double jitterY = (random.nextDouble() - 0.5) * (0.5 + path * 0.2);
                double jitterZ = (random.nextDouble() - 0.5) * (0.5 + path * 0.2);

                // Blue electric particles - use FLAME instead of SCUCK_CHARGE (typo)
                level.sendParticles(ParticleTypes.FLAME, current.x + jitterX, current.y + jitterY, current.z + jitterZ, 2, 0.1, 0.1, 0.1, 0.15);

                // Purple particles - use SMOKE instead of SCUCK_SPORE
                level.sendParticles(ParticleTypes.SMOKE, current.x + jitterX, current.y + jitterY, current.z + jitterZ, 1, 0.1, 0, 0.1, 0.1);

                // White spark particles at trail ends - use END_ROD without color array
                if (i % 5 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD, current.x + jitterX, current.y + jitterY, current.z + jitterZ, 2, 0, 0, 0, 0.3);
                }

                current = end;
            }
        }

        // Final flash - use END_ROD instead of FLASH
        level.sendParticles(ParticleTypes.END_ROD, start.x + look.x * 25, start.y + look.y * 25, start.z + look.z * 25, 3, 0, 0, 0, 0.2);
    }

    /**
     * Spawn rapid alternating strike particles for multi-hit effect.
     */
    private void spawnMultiHitParticles(ServerLevel level, Vec3 targetPos, int strikeIndex, RandomSource random) {
        // Create multiple lightning strikes alternating left/right
        double offset = (strikeIndex % 2 == 0 ? 1.0 : -1.0) * (0.5 + strikeIndex * 0.2);

        // Light strike 1
        spawnSingleLightningStrike(level, targetPos.add(new Vec3(offset, 0, 0)), random);

        // Light strike 2
        spawnSingleLightningStrike(level, targetPos.add(new Vec3(-offset, 0.5, 0)), random);

        // Rapid chain spark particles - use FLAME
        for (int i = 0; i < 4; i++) {
            level.sendParticles(ParticleTypes.FLAME, targetPos.x + (random.nextDouble() - 0.5) * 1.0, targetPos.y + random.nextDouble(), targetPos.z + (random.nextDouble() - 0.5) * 1.0, 2, 0, 0, 0, 0.2);
        }
    }

    /**
     * Spawn a single lightning strike visualization.
     */
    private void spawnSingleLightningStrike(ServerLevel level, Vec3 targetPos, RandomSource random) {
        // Zigzag lightning bolt
        int segments = 5;
        for (int i = 0; i < segments; i++) {
            double t = (double) i / segments;
            double endT = (double) (i + 1) / segments;

            double startX = targetPos.x;
            double startY = targetPos.y + (1.0 - t) * 2.0;
            double startZ = targetPos.z;

            double endX = targetPos.x + (random.nextDouble() - 0.5) * 1.5;
            double endY = targetPos.y + (1.0 - endT) * 2.0;
            double endZ = targetPos.z + (random.nextDouble() - 0.5) * 1.5;

            // Blue electric particles - use FLAME
            level.sendParticles(ParticleTypes.FLAME, (startX + endX) / 2, (startY + endY) / 2, (startZ + endZ) / 2, 2, 0, 0, 0, 0.15);

            // Purple particles - use SMOKE
            level.sendParticles(ParticleTypes.SMOKE, (startX + endX) / 2, (startY + endY) / 2, (startZ + endZ) / 2, 1, 0.1, 0, 0.1, 0.1);

            // White spark at end - use END_ROD without color array
            level.sendParticles(ParticleTypes.END_ROD, endX, endY, endZ, 2, 0, 0, 0, 0.3);

            // Final flash - use END_ROD
            if (i == segments - 1) {
                level.sendParticles(ParticleTypes.END_ROD, endX, endY, endZ, 2, 0, 0, 0, 0.2);
            }
        }
    }

    /**
     * Spawn chain lightning to nearby enemies.
     */
    private void spawnChainLightning(ServerLevel level, Vec3 source, Vec3 target, RandomSource random) {
        int segments = 8;
        for (int i = 0; i < segments; i++) {
            double t = (double) i / segments;
            double x = source.x + (target.x - source.x) * t;
            double y = source.y + (target.y - source.y) * t;
            double z = source.z + (target.z - source.z) * t;

            // Jagged lightning effect
            double jitter = (random.nextDouble() - 0.5) * 0.6;
            x += jitter;
            y += jitter;
            z += jitter;

            // Yellow electric particles - use FLAME
            level.sendParticles(ParticleTypes.FLAME, x, y, z, 2, 0.1, 0.1, 0.1, 0.15);

            // White spark particles - use END_ROD without color array
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 0.3);
        }

        // Final strike - use END_ROD instead of FLASH
        level.sendParticles(ParticleTypes.END_ROD, target.x, target.y, target.z, 2, 0, 0, 0, 0.2);
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        LivingEntity target = (LivingEntity) ctx.getTargetEntity();
        LivingEntity caster = (LivingEntity) ctx.getCasterEntity();

        if (target == null || caster == null) return;

        ServerLevel level = (ServerLevel) caster.level();
        RandomSource random = level.getRandom();

        // Calculate base damage from caster's level
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = 14.0 + (casterLevel * 2.8);
        double directDamage = baseDamage * DIRECT_DAMAGE_MULTIPLIER;

        // Apply direct damage with sound using factory pattern
        target.hurt(level.damageSources().indirectMagic(caster, caster), (float) directDamage);

        // Sound on hit - thunder crack with SoundSource
        if (level != null) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.2f, 0.7f + ThreadLocalRandom.current().nextFloat() * 0.3f);
        }

        // Apply brief initial stun
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);

        if (level.getServer() != null) {
            level.getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                }
            });
        }

        // Spawn multi-hit particles for each strike
        for (int i = 0; i < MULTI_HIT_COUNT; i++) {
            spawnMultiHitParticles(level, target.position(), i, random);

            // Delay each strike slightly for visual effect
            final int strikeIndex = i;
            if (level.getServer() != null) {
                level.getServer().execute(() -> {
                    if (target.isAlive()) {
                        // Apply damage for this strike using factory pattern
                        double strikeDamage = baseDamage * (0.5 + strikeIndex * 0.15);
                        target.hurt(level.damageSources().indirectMagic(caster, caster), (float) strikeDamage);

                        // Sound for each strike with SoundSource
                        if (level != null) {
                            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.9f, 0.5f + random.nextFloat() * 0.4f);
                        }

                        // Chain to nearby enemies on each strike
                        chainLightningToNearby(ctx, target, strikeIndex, baseDamage);
                    }
                });
            }
        }

        // Shockwave particle effect
        spawnShockwave(level, target.position(), random);
    }

    /**
     * Spawn shockwave particles radiating from target.
     */
    private void spawnShockwave(ServerLevel level, Vec3 origin, RandomSource random) {
        int rings = 4;
        for (int ring = 0; ring < rings; ring++) {
            double radius = 2.0 + ring * 1.5;

            for (int i = 0; i < 16; i++) {
                double angle = (i / 16.0) * Math.PI * 2 + (ring * 0.2);
                double x = origin.x + Math.cos(angle) * radius;
                double y = origin.y + 1.0 + Math.sin(ring * 0.5) * 0.5;
                double z = origin.z + Math.sin(angle) * radius;

                // Electric particles - use FLAME instead of SCUCK_CHARGE
                level.sendParticles(ParticleTypes.FLAME, x, y, z, 2, 0, 0, 0, 0.15);

                // Flash ring - use END_ROD instead of FLASH
                if (ring % 2 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0, 0, 0, 0.1);
                }
            }
        }
    }

    /**
     * Chain lightning to nearby enemies on each strike.
     */
    private void chainLightningToNearby(SkillContext ctx, LivingEntity originalTarget, int strikeIndex, double baseDamage) {
        LivingEntity caster = (LivingEntity) ctx.getCasterEntity();
        if (caster == null) return;

        ServerLevel level = (ServerLevel) caster.level();
        RandomSource random = level.getRandom();

        Vec3 source = originalTarget.position();
        int chains = Math.max(1, CHAIN_COUNT - strikeIndex);

        for (LivingEntity neighbor : level.getEntitiesOfClass(
                LivingEntity.class,
                originalTarget.getBoundingBox().inflate(CHAIN_RADIUS + strikeIndex * 0.5),
                e -> !e.isRemoved())) {
            if (neighbor == caster || neighbor == originalTarget || !neighbor.isAlive()) continue;

            // Chain lightning visual
            spawnChainLightning(level, source, neighbor.position(), random);

            // Chain sound with SoundSource
            level.playSound(null, neighbor.getX(), neighbor.getY(), neighbor.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.8f + strikeIndex * 0.1f, 0.4f + random.nextFloat() * 0.3f);

            // Chain damage
            double chainDamage = baseDamage * (CHAIN_DAMAGE_MULTIPLIER - strikeIndex * 0.05);

            // Use damageSources factory pattern for 1.21.11
            neighbor.hurt(neighbor.damageSources().indirectMagic(caster, caster), (float) chainDamage);

            // Apply shock debuff (slowness and weakness) - use MobEffectInstance constructor
            if (neighbor instanceof net.minecraft.world.entity.player.Player player) {
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, SHOCK_DURATION, 0, false, false));
                player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, SHOCK_DURATION, 0, false, false));
            }

            chains--;
            if (chains <= 0) break;
        }
    }
}
