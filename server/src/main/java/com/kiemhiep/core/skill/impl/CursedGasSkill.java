package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Khí_Ars - Cursed Gas skill that spreads an unholy miasma of death.
 * Area damage with dark poison effect and wither summoning.
 */
public class CursedGasSkill extends BaseSkill implements IAreaDamage {

    public static final CursedGasSkill INSTANCE = new CursedGasSkill();

    private CursedGasSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "poison");
        }
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.random;

        // Dark purple/black summoning circle at center
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 3.0 + (random.nextDouble() * 1.0);
            double x = origin.x() + Math.cos(angle) * radius;
            double z = origin.z() + Math.sin(angle) * radius;

            // Rotating dark particles
            level.sendParticles(ParticleTypes.SMOKE, x, origin.y() + 1.0, z, 1, 0, 0.05, 0, 0.02);
        }

        // Inner ring with purple/pink cursed particles - use FLAME instead of SCUCK_CHARGE
        for (int i = 0; i < 15; i++) {
            double angle = (i / 15.0) * Math.PI * 2;
            double radius = 2.0 + (random.nextDouble() * 0.5);
            double x = origin.x() + Math.cos(angle) * radius;
            double z = origin.z() + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.FLAME, x, origin.y() + 1.2, z, 1, 0, 0.03, 0, 0.02);
        }

        // Summoning wither heads - rising dark clouds - use SCULK_SPORE
        for (int i = 0; i < 30; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 5.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 5.0;
            double startY = origin.y();
            double endY = startY + 6.0 + random.nextDouble() * 3.0;

            for (double y = startY; y < endY; y += 0.25) {
                level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.1, 0, 0.02);
            }
        }

        // Outer aura of darkness spreading - use SMOKE
        for (int i = 0; i < 25; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 3.0;
            double y = origin.y() + random.nextDouble() * 2.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 3.0;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.08, 0, 0.03);
        }

        // Play cast sound - use AMBIENT_CAVE with SoundSource
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 0.8f, 0.2f + (random.nextFloat() * 0.3f));
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location center = ctx.getOrigin();
        int casterLevel = caster.getLevel();

        // Area of effect radius based on skill level
        double radius = 7.0 + (casterLevel * 1.0);
        double height = 5.0 + (casterLevel * 0.7);

        AABB area = new AABB(
            center.x() - radius, center.y() - 1, center.z() - radius,
            center.x() + radius, center.y() + height, center.z() + radius
        );

        // Find all entities in area
        List<LivingEntity> entities = level.getEntitiesOfClass(
            LivingEntity.class,
            area
        );

        for (LivingEntity target : entities) {
            if (target != null) {
                // Calculate damage based on caster level
                double baseDamage = 18.0 + (casterLevel * 2.5);
                double curseDamage = 14.0 + (casterLevel * 1.8);

                // Apply damage source (curse/magic damage)
                target.hurt(level.damageSources().magic(), (float) (baseDamage + curseDamage));

                // Apply wither (strongest debuff - epic curse)
                int witherDuration = 140 + (casterLevel * 35); // 7-40.5 seconds
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, witherDuration, 3));

                // Apply poison debuff
                int poisonDuration = 90 + (casterLevel * 22); // 4.5-24 seconds
                target.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, 2));

                // Apply weakness debuff
                int weaknessDuration = 100 + (casterLevel * 25); // 5-26.25 seconds
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, 2));

                // Apply slow debuff - use SLOWNESS instead of MOVEMENT_SLOWNESS
                int slowDuration = 60 + (casterLevel * 15); // 3-16.5 seconds
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slowDuration, 1));

                // Apply darkness effect (confusing curse)
                int darknessDuration = 50 + (casterLevel * 12); // 2.5-14 seconds
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, darknessDuration, 0));

                // Spawn particles on target
                spawnTargetCursedParticles(level, target);
            }
        }
    }

    private void spawnTargetCursedParticles(ServerLevel level, LivingEntity target) {
        RandomSource random = level.random;

        // Withering dark particles swirling around target - use SMOKE
        for (int i = 0; i < 35; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.8;
            double y = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.8;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 2, 0, 0.02, 0, 0.06);
        }

        // Wither vines emerging from target - use SCULK_SPORE
        for (int i = 0; i < 18; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.2;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.2;
            double startY = target.getY() + target.getBbHeight();
            double endY = startY + 3.0 + random.nextDouble() * 2.0;

            for (double y = startY; y < endY; y += 0.15) {
                level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.1, 0, 0.02);
            }
        }

        // Cursed aura particles forming ring - use FLAME instead of SCUCK_CHARGE
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2;
            double radius = 1.5 + (random.nextDouble() * 0.5);
            double x = target.getX() + Math.cos(angle) * radius;
            double z = target.getZ() + Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.FLAME, x, target.getY() + target.getBbHeight(), z, 1, 0, 0.05, 0, 0.02);
        }

        // Sound effect for each victim - use AMBIENT_CAVE with SoundSource
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 0.5f, 0.15f + (random.nextFloat() * 0.25f));
    }
}
