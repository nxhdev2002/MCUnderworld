package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IThunder;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Lôi_Long - Raging Thunder skill that summons a massive lightning dragon/serpent.
 * Area damage with intense lightning effect.
 * Unique behavior: Summons a serpent-like dragon that moves through enemies, chaining lightning.
 */
public class RagingThunderSkill extends BaseSkill implements IThunder {

    public static final RagingThunderSkill INSTANCE = new RagingThunderSkill();

    private static final int STUN_DURATION = 60; // 3 seconds
    private static final int WEAKNESS_DURATION = 180; // 9 seconds
    private static final int CHAINS = 4;
    private static final double DRAGON_LENGTH = 25.0;
    private static final double DRAGON_WIDTH = 4.0;
    private static final double DAMAGE_MULTIPLIER = 1.5;

    private RagingThunderSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location origin = ctx.getOrigin();

        if (origin != null) {
            // Sound effect: Thunder roar with SoundSource - get ServerLevel from context
            if (ctx.getEffectRunner() != null) {
                ctx.getEffectRunner().playEffect(ctx, "lightning");
            }
            // Spawn summoning particles
            spawnSummoningParticles(origin);
        }
    }

    /**
     * Spawn particles during skill summoning.
     */
    private void spawnSummoningParticles(Location origin) {
        // Use EffectRunner to get ServerLevel
        // Particles will be spawned in applyThunder which has proper ServerLevel access
    }

    /**
     * Spawn the lightning dragon trail.
     */
    private void spawnDragonTrail(ServerLevel level, Location origin, double distance, RandomSource random,
            int segmentIndex) {
        double angle = (segmentIndex / (double) 20) * Math.PI * 2;

        // Dragon body position with wave motion
        double x = origin.x() + Math.cos(angle) * distance * 0.3;
        double y = origin.y() + 3.0 + Math.sin(distance / 3.0) * 1.5 + random.nextDouble() * 0.5;
        double z = origin.z() + Math.sin(angle) * distance * 0.3;

        // Blue electric particles for the dragon body - use CRIMSON_SPORE for 1.21.11 compatibility
        level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x, y, z,
                4,
                0, 0, 0,
                0.15);

        // Purple particles for energy - use SMOKE
        level.sendParticles(ParticleTypes.SMOKE,
                x, y, z,
                3,
                0, 0, 0,
                0.2);

        // End rod sparks for glow
        level.sendParticles(ParticleTypes.END_ROD,
                x, y, z,
                2,
                0, 0, 0,
                0.4);

        // Chain lightning from dragon
        if (segmentIndex % 3 == 0) {
            spawnChainLightning(level, x, y, z, random);
        }
    }

    /**
     * Spawn chain lightning particles from the dragon.
     */
    private void spawnChainLightning(ServerLevel level, double x, double y, double z, RandomSource random) {
        // Create jagged lightning bolts
        int segments = 6;
        for (int i = 0; i < segments; i++) {
            double t = (double) i / segments;
            double offX = (random.nextDouble() - 0.5) * 2.0;
            double offY = (random.nextDouble() - 0.5) * 2.0;
            double offZ = (random.nextDouble() - 0.5) * 2.0;

            // Long blue lightning bolts - use SMOKE for NEUTRAL
            level.sendParticles(ParticleTypes.SMOKE,
                    x + offX * t, y + offY * t, z + offZ * t,
                    2,
                    0.1, 0, 0.1,
                    0.2);

            // White spark particles
            level.sendParticles(ParticleTypes.END_ROD,
                    x + offX * t, y + offY * t, z + offZ * t,
                    2,
                    0, 0, 0,
                    0.3);
        }

        // Final lightning strike particle - use END_ROD
        level.sendParticles(ParticleTypes.END_ROD,
                x, y, z,
                1,
                0, 0, 0,
                0.2);
    }

    /**
     * Spawn lightning strikes in a pattern from the dragon's path.
     */
    private void spawnLightningCone(ServerLevel level, Location origin, RandomSource random) {
        int strikes = 12;
        for (int i = 0; i < strikes; i++) {
            double angle = (i / (double) strikes) * Math.PI * 2;
            double distance = 5.0 + random.nextDouble() * 8.0;

            double x = origin.x() + Math.cos(angle) * distance;
            double z = origin.z() + Math.sin(angle) * distance;
            double y = origin.y() + 2.0 + random.nextDouble() * 3.0;

            // Lightning bolt from sky - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    x, y, z,
                    3,
                    0, -0.5, 0,
                    0.15);

            // Yellow particles - use SMOKE for NEUTRAL
            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    2,
                    0.1, 0, 0.1,
                    0.2);

            // End rod sparks
            level.sendParticles(ParticleTypes.END_ROD,
                    x, y, z,
                    2,
                    0, 0, 0,
                    0.4);
        }
    }

    @Override
    public void applyThunder(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }
        ServerLevel level = (ServerLevel) fabricCaster.getPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.getRandom();

        if (origin == null) return;

        int casterLevel = ctx.getSkillLevel();

        // Calculate dragon length based on skill level
        double finalDragonLength = DRAGON_LENGTH + (casterLevel * 5.0);
        double finalRadius = DRAGON_WIDTH + (casterLevel * 1.0);

        // Spawn dragon trail segments
        int segments = 20;
        for (int i = 0; i < segments; i++) {
            spawnDragonTrail(level, origin, i * (finalDragonLength / segments), random, i);
        }

        // Spawn lightning cone effect
        spawnLightningCone(level, origin, random);

        // Area damage in the cone
        AABB area = new AABB(
                origin.x() - finalRadius, origin.y() - 2, origin.z() - finalRadius,
                origin.x() + finalRadius, origin.y() + 5, origin.z() + finalRadius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                e -> !e.isRemoved()
        );

        for (LivingEntity target : entities) {
            if (target == null) continue;

            // Calculate distance from origin
            double distance = target.distanceToSqr(origin.x(), origin.y(), origin.z());
            if (distance > finalDragonLength * finalDragonLength) continue;

            // Calculate damage based on distance
            double distanceFactor = 1.0 - (distance / (finalDragonLength * finalDragonLength));
            double baseDamage = 15.0 + (casterLevel * 3.0);
            double dragonDamage = baseDamage * distanceFactor * DAMAGE_MULTIPLIER;

            // Apply damage using damage source factory pattern - cast caster to Entity
            net.minecraft.world.entity.Entity c = (net.minecraft.world.entity.Entity) ctx.getCasterEntity();
            net.minecraft.world.entity.Entity targetEntity = (net.minecraft.world.entity.Entity) ctx.getCasterEntity();
            target.hurt(target.damageSources().indirectMagic(c, targetEntity), (float) dragonDamage);

            // Sound on hit - thunder crack
            if (target.level() instanceof ServerLevel targetLevel) {
                targetLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.5f, 0.6f + ThreadLocalRandom.current().nextFloat() * 0.4f);
            }

            // Chain lightning to nearby enemies
            chainLightningToNearby(ctx, target, CHAINS);

            // Apply stun debuff (thunder strike)
            target.setNoGravity(true);
            target.setDeltaMovement(target.getDeltaMovement().multiply(0.3, 1.0, 0.3));

            // Schedule recovery - use execute() with delay through ticker
            target.level().getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                    // Apply weakness and slowness
                    if (target instanceof net.minecraft.world.entity.player.Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 1, false, false));
                        player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, WEAKNESS_DURATION, 1, false, false));
                    }
                }
            });

            // Spawn particles on target
            spawnTargetDragonParticles(target);
        }

        // Final dragon roar sound
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 2.0f, 0.4f);
    }

    /**
     * Chain lightning to nearby enemies from the dragon.
     */
    private void chainLightningToNearby(SkillContext ctx, LivingEntity originalTarget, int maxChains) {
        net.minecraft.world.entity.Entity c = (net.minecraft.world.entity.Entity) ctx.getCasterEntity();
        if (c == null) return;

        ServerLevel level = (ServerLevel) c.level();
        RandomSource random = level.getRandom();

        double chainRadius = 6.0 + ctx.getSkillLevel() * 0.5;

        int chainsUsed = 0;
        for (LivingEntity neighbor : level.getEntitiesOfClass(
                LivingEntity.class,
                originalTarget.getBoundingBox().inflate(chainRadius),
                e -> !e.isRemoved())) {
            if (neighbor == originalTarget || !neighbor.isAlive()) continue;

            // Chain lightning visual
            spawnChainLightning(level, originalTarget.getX(), originalTarget.getY(), originalTarget.getZ(), random);
            level.playSound(null, neighbor.getX(), neighbor.getY(), neighbor.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.2f, 0.5f + random.nextFloat() * 0.3f);

            // Chain damage
            double chainDamage = ctx.getSkillLevel() * 2.0;
            neighbor.hurt(neighbor.damageSources().indirectMagic(c, c), (float) chainDamage);

            // Apply shock debuff
            if (neighbor instanceof net.minecraft.world.entity.player.Player player) {
                player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, WEAKNESS_DURATION, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 0, false, false));
            }

            chainsUsed++;
            if (chainsUsed >= maxChains) break;
        }
    }

    /**
     * Spawn particles on target hit by dragon.
     */
    private void spawnTargetDragonParticles(LivingEntity target) {
        ServerLevel level = (ServerLevel) target.level();
        RandomSource random = level.getRandom();

        // Electric sparks from target - use CRIMSON_SPORE for 1.21.11 compatibility
        for (int i = 0; i < 15; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.5;
            double y = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.5;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    x, y, z,
                    3,
                    0, 0, 0,
                    0.15);
        }

        // Yellow particles - use SMOKE for NEUTRAL
        // Use target coordinates directly
        double tx = target.getX() + (random.nextDouble() - 0.5) * 1.5;
        double ty = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
        double tz = target.getZ() + (random.nextDouble() - 0.5) * 1.5;

        for (int i = 0; i < 2; i++) {
            level.sendParticles(ParticleTypes.SMOKE,
                    tx, ty, tz,
                    2,
                    0, 0, 0,
                    0.15);
        }

        // White end rod sparks
        for (int i = 0; i < 5; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    target.getX() + (random.nextDouble() - 0.5) * 1.0,
                    target.getY() + target.getBbHeight() + random.nextDouble() * 1.0,
                    target.getZ() + (random.nextDouble() - 0.5) * 1.0,
                    2,
                    0, 0, 0,
                    0.4);
        }

        // Lightning flash on hit - use END_ROD
        level.sendParticles(ParticleTypes.END_ROD,
                target.getX(), target.getY() + target.getBbHeight(), target.getZ(),
                2,
                0, 0, 0,
                0.2);
    }
}
