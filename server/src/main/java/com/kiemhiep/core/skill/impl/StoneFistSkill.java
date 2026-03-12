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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Thach_UsageId - Stone Fist skill that hardens the user's fist into unbreakable stone.
 * Single target with crushing impact.
 * Visual: Rock fist with dust particles, crushing impact
 */
public class StoneFistSkill extends BaseSkill implements ISingleTargetDamage {

    public static final StoneFistSkill INSTANCE = new StoneFistSkill();

    private StoneFistSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }
        // Execute the stone fist logic
        executeStoneFist(ctx);
    }

    private void executeStoneFist(SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (player == null || !(player instanceof FabricPlayerAdapter)) {
            return;
        }

        FabricPlayerAdapter fabricPlayer = (FabricPlayerAdapter) player;
        net.minecraft.world.entity.player.Player entity = fabricPlayer.getPlayer();

        if (entity == null) {
            return;
        }

        // Get level from entity
        ServerLevel level = null;
        if (entity.level() instanceof ServerLevel) {
            level = (ServerLevel) entity.level();
        } else {
            return;
        }

        // Get caster level for damage calculation
        int casterLevel = player.getLevel();
        if (casterLevel <= 0) return;

        // Calculate damage based on caster level
        // Base: 10 + (level * 3) damage
        double damage = 10.0 + (casterLevel * 3.0);
        float knockbackStrength = 2.0f + (casterLevel * 0.3f);

        // Spawn stone fist particles at player location
        spawnStoneFistParticles(level, entity, 20);

        // Get target entity and location from context
        LivingEntity targetEntity = ctx.getCasterEntity() instanceof LivingEntity le ? le : null;
        double targetX = entity.getX();
        double targetY = entity.getY();
        double targetZ = entity.getZ();

        // Create crushing impact effect at target location
        if (ctx.getOrigin() != null) {
            Location originLoc = ctx.getOrigin();
            targetX = originLoc.x();
            targetY = originLoc.y();
            targetZ = originLoc.z();

            // Particles: Dust (cobblestone color) + CRIMSON_SPORE for earth impact
            spawnCrushingImpact(level, targetX, targetY, targetZ, 15);

            // Sound: Stone break + heavy impact with correct signature
            float randomPitch = 1.0f + RandomSource.create().nextFloat() * 0.2f;
            level.playSound(null, targetX, targetY, targetZ, SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, randomPitch);
        }

        // Apply damage to target
        if (targetEntity != null) {
            applyDebuffsAfterImpact(targetEntity, knockbackStrength);

            // Create damage source
            targetEntity.hurt(targetEntity.level().damageSources().mobAttack(entity), (float) damage);

            // Screen shake effect on target
            targetEntity.hurtMarked = true;
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        // Damage is applied in executeStoneFist
        executeStoneFist(ctx);
    }

    private void spawnStoneFistParticles(ServerLevel level, LivingEntity entity, int count) {
        RandomSource random = level.getRandom();
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.7;
        double z = entity.getZ();

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 1.5;
            double offsetY = (random.nextDouble() - 0.5) * 1.5;
            double offsetZ = (random.nextDouble() - 0.5) * 1.5;

            // DUST particle format changed in 1.21.11 - use DUST_COLOR instead with RGB values
            // Using CRIMSON_SPORE as replacement for DUST (cobblestone color)
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + offsetX, y + 0.5 + offsetY, z + offsetZ,
                1, 0.0, 0.0, 0.0, 0.35f + random.nextFloat() * 0.15f
            );
        }

        // Add CRIMSON_SPORE for earth essence
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + (random.nextDouble() - 0.5) * 2.0,
                y + 1.0 + (random.nextDouble() * 1.5),
                z + (random.nextDouble() - 0.5) * 2.0,
                1, 0.0, 0.0, 0.0, 0.1f
            );
        }
    }

    private void spawnCrushingImpact(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // Dust cloud at impact point - use CRIMSON_SPORE instead of DUST
        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 3.0;
            double offsetY = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 3.0;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + offsetX, y + offsetY, z + offsetZ,
                1, 0.0, 0.0, 0.0, 0.4f + random.nextFloat() * 0.2f
            );
        }

        // Stone shards with END_ROD sparkles
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * 2.0,
                y + 1.0 + (random.nextDouble() - 0.5) * 1.5,
                z + (random.nextDouble() - 0.5) * 2.0,
                3, 0.1, 0.1, 0.1, 0.3f
            );
        }

        // Earth grounding - use CRIMSON_SPORE with color
        for (int i = 0; i < count / 3; i++) {
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + (random.nextDouble() - 0.5) * 2.5,
                y + (random.nextDouble() * 0.5),
                z + (random.nextDouble() - 0.5) * 2.5,
                1, 0.3f, 0.2f, 0.1f, 0.15f
            );
        }
    }

    private void applyDebuffsAfterImpact(LivingEntity target, float knockbackStrength) {
        // Apply knockback - use target's position directly
        target.push(0, knockbackStrength * 0.7, 0);

        // Apply slow fall effect - makes them vulnerable
        int duration = 40 + (target.level().random.nextInt(20));
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOW_FALLING, duration, 1, false, false));
    }
}
