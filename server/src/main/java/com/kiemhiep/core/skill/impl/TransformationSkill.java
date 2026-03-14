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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Biến_Hóa - Transformation skill that mutates the target with rapidly spreading poison.
 * Single target with debuff application.
 */
public class TransformationSkill extends BaseSkill implements ISingleTargetDamage {

    public static final TransformationSkill INSTANCE = new TransformationSkill();

    private TransformationSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "poison");
        }
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.getRandom();

        // Spawn slow rising dark vines before transformation
        for (int i = 0; i < 25; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 4.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 4.0;
            double startY = origin.y() + random.nextDouble();
            double endY = startY + 4.0 + random.nextDouble() * 3.0;

            // Vines rising up - use WEEPING_VINES particles (if available) or SMOKE
            for (double y = startY; y < endY; y += 0.3) {
                level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.01);
            }
        }

        // Dark magical circle at center - use END_ROD instead of DARK_SMOKE
        for (int i = 0; i < 15; i++) {
            double angle = (i / 15.0) * Math.PI * 2;
            double radius = 2.0 + (random.nextDouble() * 0.5);
            double x = origin.x() + Math.cos(angle) * radius;
            double z = origin.z() + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.END_ROD,
                x, origin.y() + 1.0, z,
                1,
                0, 0.1, 0,
                0.02);
        }

        // Purple/cursed aura around caster - use SMOKE instead of TINTED_GLASS
        for (int i = 0; i < 20; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 2.0;
            double y = origin.y() + random.nextDouble() * 2.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 2.0;

            level.sendParticles(ParticleTypes.SMOKE,
                x, y, z,
                1,
                0, 0.05, 0,
                0.03);
        }

        // Play cast sound - use AMBIENT_CAVE instead of BODY_FALL
        level.playSound(null, origin.x(), origin.y(), origin.z(),
            SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.5f, 0.3f + (random.nextFloat() * 0.2f));
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Object targetObj = ctx.getTargetEntity();
        if (!(targetObj instanceof LivingEntity target)) return;

        Object casterObj = ctx.getCasterEntity();
        if (!(casterObj instanceof LivingEntity caster)) return;

        ServerLevel level = (ServerLevel) caster.level();

        // Calculate damage based on caster level
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = 20.0 + (casterLevel * 3.0);
        double mutationDamage = 12.0 + (casterLevel * 1.5);

        // Apply damage - use indirectMagic with proper casting
        target.hurt(target.damageSources().indirectMagic(caster, caster), (float) (baseDamage + mutationDamage));

        // Apply multiple debuffs rapidly spreading with MobEffectInstance wrapper
        int poisonDuration = 120 + (casterLevel * 30); // 6-31.5 seconds - very long
        target.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, 3));

        int witherDuration = 60 + (casterLevel * 15); // 3-16.5 seconds
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, witherDuration, 2));

        int weaknessDuration = 140 + (casterLevel * 35); // 7-36.75 seconds
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, 3));

        int slownessDuration = 100 + (casterLevel * 25); // 5-26.25 seconds
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slownessDuration, 2));

        // Apply nausea effect (dizzy/spinning) - use NASTINESS available in 1.21.11
        int nauseaDuration = 40 + (casterLevel * 10); // 2-7 seconds
        target.addEffect(new MobEffectInstance(MobEffects.NAUSEA, nauseaDuration, 1));

        // Apply reduced armor effect - use REGEN for transformation effect
        int extractDuration = 80 + (casterLevel * 20); // 4-21 seconds
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, extractDuration, 1));

        // Spawn transformation particles on target
        spawnTargetMutationParticles(level, target);
    }

    private void spawnTargetMutationParticles(ServerLevel level, LivingEntity target) {
        RandomSource random = level.getRandom();

        // Rapidly swirling dark particles around mutated target - use SMOKE instead of TINTED_GLASS
        for (int i = 0; i < 40; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.5;
            double y = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.5;

            level.sendParticles(ParticleTypes.SMOKE,
                x, y, z,
                2,
                0, 0.02, 0,
                0.05);
        }

        // Wither vines emerging from target - use SMOKE instead of WEEPING_VINES
        for (int i = 0; i < 20; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.0;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.0;
            double startY = target.getY() + target.getBbHeight();
            double endY = startY + 2.0 + random.nextDouble() * 2.0;

            for (double y = startY; y < endY; y += 0.2) {
                level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    1,
                    0, 0.08, 0,
                    0.02);
            }
        }

        // Body corruption particles - use END_ROD instead of DARK_SMOKE
        for (int i = 0; i < 15; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 0.8;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 0.8;

            level.sendParticles(ParticleTypes.END_ROD,
                x, target.getY() + target.getBbHeight() / 2.0 + random.nextDouble(), z,
                1,
                0, 0.1, 0,
                0.03);
        }

        // Sound effect for mutation
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
            SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.6f, 0.2f + (random.nextFloat() * 0.3f));
    }
}
