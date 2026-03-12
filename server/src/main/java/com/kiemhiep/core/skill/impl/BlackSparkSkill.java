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
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Hắc_Diệm - Black Spark skill that shoots a ball of dark, corrupting energy.
 * Single target with poison/lightning mix.
 */
public class BlackSparkSkill extends BaseSkill implements ISingleTargetDamage {

    public static final BlackSparkSkill INSTANCE = new BlackSparkSkill();

    private BlackSparkSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "poison");
        }
        spawnBlackSparkProjectile(fabricCaster);
    }

    private void spawnBlackSparkProjectile(FabricPlayerAdapter player) {
        ServerLevel level = (ServerLevel) player.getServerPlayer().level();
        RandomSource random = level.random;
        Player entity = player.getPlayer();  // Fixed: use getPlayer() instead of getRawPlayer()

        if (entity == null) return;

        // Spawn black energy ball - using SMOKE instead of SCATTER (not in 1.21.11)
        level.sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), 20, 0.5, 0.5, 0.5, 0.1);

        // Spawn dark smoke particles along trajectory - using SMOKE instead of DARK_SMOKE
        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;

            level.sendParticles(ParticleTypes.SMOKE, entity.getX() + offsetX, entity.getY() + 1.5, entity.getZ() + offsetZ, 5, 0.2, 0.2, 0.2, 0.05);
        }

        // Play cast sound - using EVOKER_CAST with SoundSource
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 0.6f + (random.nextFloat() * 0.2f));
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        Player entity = fabricCaster.getPlayer();  // Fixed: use getPlayer() instead of getRawPlayer()
        if (entity == null) return;

        ServerLevel level = (ServerLevel) entity.level();

        // Calculate damage based on caster level
        int casterLevel = caster.getLevel();
        double baseDamage = 15.0 + (casterLevel * 2.5);
        double bonusDamage = 8.0; // Poison bonus

        // Apply damage - use damageSources().onFire() for 1.21.11
        entity.hurt(level.damageSources().onFire(), (float) (baseDamage + bonusDamage));

        // Apply poison debuff
        int poisonDuration = 60 + (casterLevel * 10); // 3-11 seconds
        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, poisonDuration, 1));

        // Apply wither debuff
        int witherDuration = 40 + (casterLevel * 5); // 2-6 seconds
        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER, witherDuration, 0));

        // Spawn particles on target
        spawnTargetParticles(level, entity);
    }

    private void spawnTargetParticles(ServerLevel level, LivingEntity target) {
        RandomSource random = level.random;

        // Black energy particles swirling around target - using SCULK_CHARGE instead of DRAGON_BREATH
        for (int i = 0; i < 30; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.5;
            double y = target.getY() + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.5;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0, 0, 0);
        }

        // Dark smoke particles rising - using SMOKE instead of DARK_SMOKE
        for (int i = 0; i < 15; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.0;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.0;
            double y = target.getY() + target.getBbHeight() + random.nextDouble() * 2.0;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.1, 0, 0.02);
        }

        // Play hit sound with SoundSource
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SKELETON_HURT, net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 0.5f + (random.nextFloat() * 0.2f));
    }
}
