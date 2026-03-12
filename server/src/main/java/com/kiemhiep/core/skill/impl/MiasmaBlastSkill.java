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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Hôi_Tàn - Miasma Blast skill that releases toxic gas cloud of decay.
 * Area damage with poison/smoke effect and rising particles.
 */
public class MiasmaBlastSkill extends BaseSkill implements IAreaDamage {

    public static final MiasmaBlastSkill INSTANCE = new MiasmaBlastSkill();

    private MiasmaBlastSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "poison");
        }
        spawnMiasmaCloudExplosion(ctx);
    }

    private void spawnMiasmaCloudExplosion(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.random;

        // Toxic gas cloud - rising particles with SMOKE (no color array)
        for (int i = 0; i < 40; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 8.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 8.0;
            double startY = origin.y() + 2.0;

            // Using SMOKE instead of TINTED_GLASS (no color array in 1.21.11)
            level.sendParticles(ParticleTypes.SMOKE, x, startY, z, 2, 0.1, 0.05, 0.1, 0.05);
        }

        // Rising crimson particles
        for (int i = 0; i < 25; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 6.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 6.0;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, origin.y(), z, 1, 0, 0.08, 0, 0.03);
        }

        // Outer smoke ring
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double radius = 4.0 + (random.nextDouble() * 2.0);
            double x = origin.x() + Math.cos(angle) * radius;
            double z = origin.z() + Math.sin(angle) * radius;
            double y = origin.y() + random.nextDouble() * 3.0;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0.05, 0, 0.02);
        }

        // Play cast sound - use AMBIENT_CAVE with SoundSource
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 0.7f, 0.3f + (random.nextFloat() * 0.3f));
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location center = ctx.getOrigin();
        int casterLevel = caster.getLevel();

        // Area of effect radius based on skill level
        double radius = 6.0 + (casterLevel * 0.8);
        double height = 4.0 + (casterLevel * 0.5);

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
                double baseDamage = 12.0 + (casterLevel * 2.0);
                double gasDamage = 7.0 + (casterLevel * 1.0);

                // Apply damage using damageSources().onFire()
                target.hurt(level.damageSources().onFire(), (float) (baseDamage + gasDamage));

                // Apply poison debuff (stronger than other skills)
                int poisonDuration = 100 + (casterLevel * 20); // 5-25 seconds
                target.addEffect(new MobEffectInstance(MobEffects.POISON, poisonDuration, 2));

                // Apply weakness debuff
                int weaknessDuration = 120 + (casterLevel * 25); // 6-28 seconds
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessDuration, 2));

                // Apply slow debuff - use SLOWNESS instead of MOVEMENT_SLOWNESS
                int slowDuration = 80 + (casterLevel * 15); // 4-17.5 seconds
                target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, slowDuration, 1));

                // Spawn particles on target
                spawnTargetGasParticles(level, target);
            }
        }
    }

    private void spawnTargetGasParticles(ServerLevel level, LivingEntity target) {
        RandomSource random = level.random;
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity) target;

        // Dark smoke and gas particles rising from target
        for (int i = 0; i < 20; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * 1.2;
            double z = entity.getZ() + (random.nextDouble() - 0.5) * 1.2;
            double y = entity.getY() + target.getBbHeight() + random.nextDouble() * 0.5;

            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 2, 0, 0.2, 0, 0.04);
        }

        // Pinkish wither particles mixing with target - use SMOKE instead of TINTED_GLASS
        for (int i = 0; i < 12; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * 0.8;
            double z = entity.getZ() + (random.nextDouble() - 0.5) * 0.8;

            level.sendParticles(ParticleTypes.SMOKE, x, entity.getY() + target.getBbHeight() / 2.0 + random.nextDouble(), z, 1, 0.1, 0.1, 0.0, 0.02);
        }
    }
}
