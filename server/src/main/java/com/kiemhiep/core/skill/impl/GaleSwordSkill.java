package com.kiemhiep.core.skill.impl;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Phong_Lôi_Kiem - Gale Sword skill that combines wind and lightning speed in slashes.
 * Single target with piercing air blade attack.
 * Unique behavior: Creates a wind blade trail with rapid forward movement,
 * causing multiple hits and air pressure damage.
 */
public class GaleSwordSkill extends BaseSkill implements ISingleTargetDamage {

    public static final GaleSwordSkill INSTANCE = new GaleSwordSkill();

    private static final int BLADE_DURATION = 60; // 3 seconds
    private static final double BLADE_RADIUS = 1.5;
    private static final double DAMAGE_PER_SLASH = 4.5;
    private static final int KNOCKBACK_STRENGTH = 2;
    private static final int MAX_SLASHES = 3;

    private GaleSwordSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (location != null && player != null) {
            // Sound effect: Sharp wind blade sound - use AMBIENT_CAVE with SoundSource
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.0f, 1.0f + ctx.getSkillLevel() * 0.1f);
            }

            // Spawn initial air blade particles - cast to LivingEntity
            LivingEntity caster = (LivingEntity) ctx.getCasterEntity();
            if (caster != null) {
                spawnAirBladeTrail(location, caster);
            }
        }
    }

    /**
     * Spawn air blade particles creating a trailing effect
     */
    private void spawnAirBladeTrail(Location start, LivingEntity caster) {
        if (!(caster.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();

        // Get forward direction from caster
        Vec3 direction = caster.getLookAngle().normalize();

        // Create multiple blade trails
        for (int slash = 0; slash < MAX_SLASHES; slash++) {
            double offsetX = direction.x() * slash * 1.5;
            double offsetY = direction.y() * slash * 1.5 + (slash == 1 ? 1.0 : 0);
            double offsetZ = direction.z() * slash * 1.5;

            double startX = start.x() + offsetX;
            double startY = start.y() + offsetY;
            double startZ = start.z() + offsetZ;

            // Create a trail of air blade particles
            for (int i = 0; i < 15; i++) {
                double t = i / 15.0;
                double x = startX + direction.x() * t * 2.5;
                double y = startY + direction.y() * t * 2.5 + Math.sin(t * Math.PI * 3) * 0.3;
                double z = startZ + direction.z() * t * 2.5;

                // Cloud particles for blade trail (WHITE_SMOKE -> SMOKE)
                level.sendParticles(ParticleTypes.SMOKE,
                        x, y, z,
                        2,
                        (random.nextDouble() - 0.5) * 0.2,
                        (random.nextDouble() - 0.5) * 0.2,
                        (random.nextDouble() - 0.5) * 0.2,
                        0.1);

                // END_ROD particles for sharp blade edge (works with 9 args)
                level.sendParticles(ParticleTypes.END_ROD,
                        x, y + 0.5, z,
                        1,
                        (random.nextDouble() - 0.5) * 0.1,
                        (random.nextDouble() - 0.5) * 0.1,
                        (random.nextDouble() - 0.5) * 0.1,
                        0.05);

                // End rod particles for speed trail
                if (i % 3 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD,
                            x, y, z,
                            1,
                            direction.x() * 0.3, 0.1, direction.z() * 0.3,
                            0.15);
                }
            }

            // Create random air pressure bursts at blade tips
            double tipX = startX + direction.x() * 2.5;
            double tipY = startY + direction.y() * 2.5;
            double tipZ = startZ + direction.z() * 2.5;

            for (int i = 0; i < 8; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double radius = 1.0 + random.nextDouble() * 0.5;

                level.sendParticles(ParticleTypes.CLOUD,
                        tipX + Math.cos(angle) * radius,
                        tipY + (random.nextDouble() - 0.5) * 1.0,
                        tipZ + Math.sin(angle) * radius,
                        3,
                        (random.nextDouble() - 0.5) * 0.4,
                        (random.nextDouble() - 0.5) * 0.2,
                        (random.nextDouble() - 0.5) * 0.4,
                        0.2);
            }
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        Location start = ctx.getOrigin();
        LivingEntity caster = (LivingEntity) ctx.getCasterEntity();

        if (start == null || caster == null) return;

        // Calculate damage based on caster's cultivation level
        int casterLevel = ctx.getSkillLevel();
        double damage = DAMAGE_PER_SLASH + (casterLevel * 0.6);

        // Get forward direction
        Vec3 direction = caster.getLookAngle().normalize();

        // Create a sweeping area
        for (int slash = 0; slash < MAX_SLASHES; slash++) {
            double forwardOffset = slash * 1.5;

            // Area for this slash
            AABB area = new AABB(
                    start.x() - BLADE_RADIUS + direction.x() * forwardOffset,
                    start.y() - 1.0 + direction.y() * forwardOffset,
                    start.z() - BLADE_RADIUS + direction.z() * forwardOffset,
                    start.x() + BLADE_RADIUS + direction.x() * forwardOffset,
                    start.y() + 2.5 + direction.y() * forwardOffset,
                    start.z() + BLADE_RADIUS + direction.z() * forwardOffset
            );

            List<LivingEntity> targets = ctx.getEntitiesWithinArea(area).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());

            for (LivingEntity target : targets) {
                if (target == caster || !target.isAlive()) continue;

                // Apply damage - use damageSources().indirectMagic() - cast to Entity
                net.minecraft.world.entity.Entity t = (net.minecraft.world.entity.Entity) target;
                net.minecraft.world.entity.Entity c = (net.minecraft.world.entity.Entity) ctx.getCasterEntity();
                target.hurt(t.damageSources().indirectMagic(c, caster), (float) damage);

                // Knockback away from caster - use Entity methods for LivingEntity
                net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity) target;
                Vec3 knockback = new Vec3(
                        e.getX() - start.x(),
                        0.5 + ThreadLocalRandom.current().nextDouble() * 0.3,
                        e.getZ() - start.z()
                ).normalize().scale(KNOCKBACK_STRENGTH * 0.7);

                target.push(knockback.x(), knockback.y(), knockback.z());

                // Apply wind blade debuff (levitation + slow) - use SLOWNESS instead of MOVEMENT_SLOWNESS
                if (target instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, BLADE_DURATION, 1, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, BLADE_DURATION, 0, false, false));
                } else {
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, BLADE_DURATION, 1, false, false));
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, BLADE_DURATION, 0, false, false));
                }

                // Sound on hit with SoundSource
                if (target.level() instanceof ServerLevel level) {
                    level.playSound(null, e.getX(), e.getY(), e.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 0.5f, 1.0f + ThreadLocalRandom.current().nextFloat() * 0.2f);
                }
            }
        }

        // Secondary damage from blade trail passing through
        double secondaryDamage = damage * 0.5;
        AABB trailArea = new AABB(
                start.x() - 2.0,
                start.y() - 2.0,
                start.z() - 2.0,
                start.x() + 2.0,
                start.y() + 2.0,
                start.z() + 2.0
        );

        List<LivingEntity> trailTargets = ctx.getEntitiesWithinArea(trailArea).stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .collect(Collectors.toList());
        for (LivingEntity target : trailTargets) {
            if (target == caster || !target.isAlive()) continue;

            // Random chance for secondary hit
            if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                net.minecraft.world.entity.Entity c = (net.minecraft.world.entity.Entity) ctx.getCasterEntity();
                net.minecraft.world.entity.Entity t = (net.minecraft.world.entity.Entity) target;
                target.hurt(t.damageSources().indirectMagic(c, caster), (float) secondaryDamage);

                // Additional knockback
                target.push(ThreadLocalRandom.current().nextDouble() - 0.5, 0.3, ThreadLocalRandom.current().nextDouble() - 0.5);
            }
        }
    }
}
