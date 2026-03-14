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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Mưa_Axit - Acid Rain skill that baths the battlefield in corrosive acid.
 * Area damage with continuous poison effect.
 */
public class AcidRainSkill extends BaseSkill implements IAreaDamage {

    public static final AcidRainSkill INSTANCE = new AcidRainSkill();

    private AcidRainSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "poison");
        }
        spawnAcidRain(ctx);
    }

    private void spawnAcidRain(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.random;

        // Rain particles from above - using FLAME instead of TINTED_GLASS (not in 1.21.11)
        for (int i = 0; i < 50; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 10.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 10.0;
            double startY = origin.y() + 10.0 + random.nextDouble() * 5.0;

            // Green acidic particles falling (using FLAME with minimal speed)
            level.sendParticles(ParticleTypes.FLAME, x, startY, z, 1, 0, -0.3, 0, 0.02);
        }

        // Spawn acid drip particles at ground level - using SMOKE instead of SPORE_BLOSSOM
        for (int i = 0; i < 20; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 6.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 6.0;

            level.sendParticles(ParticleTypes.SMOKE, x, origin.y() + 1.0, z, 3, 0, 0.1, 0, 0.05);
        }

        // Play cast sound - using WATER as sound source
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.6f, 0.4f + (random.nextFloat() * 0.2f));
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location center = ctx.getOrigin();
        int casterLevel = caster.getLevel();

        // Area of effect radius based on skill level
        double radius = 5.0 + (casterLevel * 0.5);
        double height = 3.0 + (casterLevel * 0.3);

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
                double baseDamage = 8.0 + (casterLevel * 1.2);
                double acidDamage = 5.0 + (casterLevel * 0.8);

                // Apply damage source - use damageSources().onFire() for 1.21.11
                target.hurt(level.damageSources().onFire(), (float) (baseDamage + acidDamage));

                // Apply poison debuff
                int poisonDuration = 80 + (casterLevel * 15); // 4-15.5 seconds
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, poisonDuration, 1));

                // Apply wither debuff
                int witherDuration = 30 + (casterLevel * 8); // 1.5-6.5 seconds
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WITHER, witherDuration, 0));

                // Apply weakness debuff
                int weaknessDuration = 100 + (casterLevel * 20); // 5-20 seconds
                target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, weaknessDuration, 1));

                // Spawn particles on target
                spawnTargetAcidParticles(level, target);
            }
        }
    }

    private void spawnTargetAcidParticles(ServerLevel level, LivingEntity target) {
        RandomSource random = level.random;

        // Green acidic particles from target (using FLAME instead of TINTED_GLASS)
        for (int i = 0; i < 15; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.0;
            double y = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.0;

            level.sendParticles(ParticleTypes.FLAME, x, y, z, 1, 0, 0.1, 0, 0.02);
        }

        // Smoke and acid mixing (using CRIMSON_SPORE is valid in 1.21.11)
        for (int i = 0; i < 8; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 0.8;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 0.8;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, target.getY() + target.getBbHeight() + random.nextDouble() * 1.5, z, 1, 0, 0.05, 0, 0.03);
        }
    }
}
