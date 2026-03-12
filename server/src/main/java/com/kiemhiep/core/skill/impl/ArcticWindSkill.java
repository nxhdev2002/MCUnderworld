package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Băng Phong - Arctic Wind skill that wraps the battlefield in freezing winds.
 * Area damage with ice/wind effect.
 * Unique behavior: Lifts enemies and creates a swirling vortex of ice particles.
 */
public class ArcticWindSkill extends BaseSkill implements IAreaDamage {

    public static final ArcticWindSkill INSTANCE = new ArcticWindSkill();

    private static final int WIND_DURATION = 80; // 4 seconds
    private static final int KNOCKBACK_FORCE = 3;
    private static final double AREA_DAMAGE_BASE = 5.0;
    private static final double AREA_RADIUS = 6.0;

    private ArcticWindSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Sound effect: Howling wind - using ambient sound with SoundSource
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.5f + ThreadLocalRandom.current().nextFloat() * 0.2f);

        // Spawn vortex particles
        spawnVortexParticles(level, origin);
    }

    /**
     * Spawn swirling vortex particles
     */
    private void spawnVortexParticles(ServerLevel level, Location center) {
        RandomSource random = level.getRandom();

        // Create a vertical vortex
        for (int layer = 0; layer < 8; layer++) {
            double y = center.y() - 1.0 + layer * 0.75;
            double radius = 1.5 + (layer * 0.6);

            for (int i = 0; i < 12; i++) {
                double angle = (System.currentTimeMillis() / 50.0) + (i * (Math.PI * 2) / 12);
                double x = center.x() + Math.cos(angle) * radius;
                double z = center.z() + Math.sin(angle) * radius;

                // Ice particles swirling in vortex (using SNOWFLAKE - valid in 1.21.11)
                // Fixed sendParticles signature: 9 args (particle, x, y, z, count, deltaX, deltaY, deltaZ, speed)
                level.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 2, 0, 0, 0, 0.03);

                // Snow particles being吸入 vortex - using END_ROD instead of SNOWBALL (not in 1.21.11)
                level.sendParticles(ParticleTypes.END_ROD, x + (random.nextDouble() - 0.5) * 0.3, y + (random.nextDouble() - 0.5) * 0.3, z + (random.nextDouble() - 0.5) * 0.3, 1, 0.05, 0.05, 0.05, 0.02);

                // Wind trails
                if (random.nextFloat() < 0.3) {
                    level.sendParticles(ParticleTypes.CLOUD, x, y + 0.5, z, 1, 0, 0, 0, 0.08);
                }
            }
        }

        // Whirlwind center - using END_ROD, fixed signature
        for (int i = 0; i < 5; i++) {
            level.sendParticles(ParticleTypes.END_ROD, center.x(), center.y() + 2.0, center.z(), 1, 0, 0, 0, 0.2);
        }
    }

    /**
     * Create ice shards raining down
     */
    private void spawnFallingShards(ServerLevel level, Location center) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < 15; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 8.0;
            double offsetZ = (random.nextDouble() - 0.5) * 8.0;
            double startX = center.x() + offsetX;
            double startZ = center.z() + offsetZ;
            double startY = center.y() + 6.0 + random.nextDouble() * 3.0;

            // Falling ice shard trail
            for (int j = 0; j < 8; j++) {
                double t = (double) j / 8;
                double y = startY - t * 6.0;
                double x = startX + offsetX * t * 0.1;
                double z = startZ + offsetZ * t * 0.1;

                level.sendParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0, 0, 0, 0.02);
            }
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) return;

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location center = ctx.getOrigin();

        // Define area
        AABB area = new AABB(
                center.x() - AREA_RADIUS, center.y() - 1.0, center.z() - AREA_RADIUS,
                center.x() + AREA_RADIUS, center.y() + 4.0, center.z() + AREA_RADIUS
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area);

        if (targets.isEmpty()) return;

        // Sound at center - using ambient sound with SoundSource
        level.playSound(null, center.x(), center.y(), center.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.5f);

        int casterLevel = caster.getLevel();

        for (LivingEntity target : targets) {
            if (target instanceof Player player && player.getUUID().equals(caster.getUniqueId())) continue;
            if (!target.isAlive()) continue;

            // Calculate damage based on caster's level
            double baseDamage = AREA_DAMAGE_BASE + (casterLevel * 0.8);
            double damage = baseDamage;

            // Apply damage - use damageSources().onFire() for 1.21.11
            target.hurt(level.damageSources().onFire(), (float) damage);

            // knockback upward and outward
            // Cast LivingEntity to Entity for x()/y()/z() methods
            net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity) target;
            Vec3 knockback = new Vec3(
                    e.getX() - center.x(),
                    1.5 + Math.random() * 0.5,
                    e.getZ() - center.z()
            ).normalize().scale(KNOCKBACK_FORCE * 0.5);

            target.push(knockback.x, knockback.y, knockback.z);

            // Apply levitation effect
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, WIND_DURATION, 0));

            // Spawn falling shards at target location - get dimension name directly
            spawnFallingShards(level, new Location(level.dimension().toString(), e.getX(), e.getY(), e.getZ()));
        }
    }
}
