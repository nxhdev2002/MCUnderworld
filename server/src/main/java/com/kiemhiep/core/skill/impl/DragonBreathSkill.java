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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Rồng Hào Hỏa (Dragon Breath) - Dragon Breath skill
 *
 * Mô tả: Một luồng lửa khổng lồ từ miệng rồng, áp đảo kẻ thù với nhiệt độ bỏng rát.
 *
 * Hiệu ứng:
 * - Cone-shaped fire stream from caster to target
 * - Area damage in front of caster
 * - Particle effect: FLAME, LARGE_SMOKE
 * - Sound effect: Dragon roar
 *
 * Nguồn cảm hứng: Smaug's breath, Presto's Dragon's Breath, various dragon skills
 */
public class DragonBreathSkill extends BaseSkill implements IAreaDamage {

    public static final DragonBreathSkill INSTANCE = new DragonBreathSkill();

    // Configuration
    private static final double CONE_ANGLE = Math.PI / 3; // 60 degrees cone
    private static final double LENGTH = 20.0; // Length of breath
    private static final double RADIUS_AT_END = 6.0; // Width at end of cone
    private static final double DAMAGE_MULTIPLIER = 3.0;
    private static final int BURN_DURATION = 80; // 4 seconds burn

    private DragonBreathSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();

        // Visual: Dragon breath particle stream
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "fire");
        }

        spawnDragonBreathStream(level, fabricCaster, origin);

        // Sound effect for dragon roar - use AMBIENT_CAVE with SoundSource
        level.playSound(null, origin.x(), origin.y(), origin.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4f);
    }

    private void spawnDragonBreathStream(ServerLevel level, FabricPlayerAdapter caster, Location origin) {
        RandomSource random = level.random;

        // Get look angles from Player
        double rotX = Math.toRadians(caster.getPlayer().getYRot());
        double rotY = Math.toRadians(caster.getPlayer().getXRot());

        // Calculate direction vector
        double dx = -Math.sin(rotX) * Math.cos(rotY);
        double dy = -Math.sin(rotY);
        double dz = Math.cos(rotX) * Math.cos(rotY);

        // Normalize
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= len;
        dy /= len;
        dz /= len;

        // Stream particles along the cone
        int steps = (int) (LENGTH * 1.5);
        for (int i = 0; i < steps; i++) {
            double progress = (double) i / steps;
            double currentLength = progress * LENGTH;
            double currentRadius = progress * RADIUS_AT_END;

            double px = origin.x() + dx * currentLength;
            double py = origin.y() + 1.5 + dy * currentLength; // Aim at eye level
            double pz = origin.z() + dz * currentLength;

            // Random spread within cone
            double spreadAngle = random.nextDouble() * CONE_ANGLE;
            double spreadRot = random.nextDouble() * Math.PI * 2;

            double offsetX = Math.sin(spreadAngle) * Math.cos(spreadRot) * currentRadius;
            double offsetZ = Math.sin(spreadAngle) * Math.sin(spreadRot) * currentRadius;
            double offsetY = random.nextDouble() * currentRadius * 0.5;

            // Fire particles
            level.sendParticles(ParticleTypes.FLAME, px + offsetX, py + offsetY, pz + offsetZ, 3, 0, 0, 0, 0.1);
            level.sendParticles(ParticleTypes.SMOKE, px + offsetX, py + offsetY + 0.5, pz + offsetZ, 2, 0, 0, 0, 0.05);
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }

        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        double baseDamage = 35.0 + (caster.getLevel() * 2.0);
        double finalDamage = baseDamage * DAMAGE_MULTIPLIER;

        // Direction calculation from player rotation
        double rotX = Math.toRadians(fabricCaster.getPlayer().getYRot());
        double rotY = Math.toRadians(fabricCaster.getPlayer().getXRot());
        double dx = -Math.sin(rotX) * Math.cos(rotY);
        double dy = -Math.sin(rotY);
        double dz = Math.cos(rotX) * Math.cos(rotY);
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= len;
        dy /= len;
        dz /= len;

        // Check for entities in cone
        for (double dist = 0; dist < LENGTH; dist += 0.5) {
            double px = origin.x() + dx * dist;
            double py = origin.y() + 1.5 + dy * dist;
            double pz = origin.z() + dz * dist;

            double radiusAtDist = (dist / LENGTH) * RADIUS_AT_END;

            AABB searchBox = new AABB(
                px - radiusAtDist, py - radiusAtDist, pz - radiusAtDist,
                px + radiusAtDist, py + radiusAtDist, pz + radiusAtDist
            );

            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox);

            for (LivingEntity entity : targets) {
                if (entity instanceof Player player) {
                    // Cast to Entity for x/z methods
                    net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity) entity;
                    // Check if entity is in front of caster
                    double entityDx = e.getX() - origin.x();
                    double entityDz = e.getZ() - origin.z();
                    double entityLen = Math.sqrt(entityDx * entityDx + entityDz * entityDz);
                    double dot = (entityDx * dx + entityDz * dz) / entityLen;

                    if (entityLen > 0 && dot > 0.3) { // Within cone angle
                        entity.hurt(level.damageSources().onFire(), (float) finalDamage);
                        // Use setRemainingFireTicks for LivingEntity
                        entity.setRemainingFireTicks(BURN_DURATION);

                        // Knockback effect for dragon breath
                        double knockbackX = (e.getX() - origin.x()) / entityLen * 1.5;
                        double knockbackZ = (e.getZ() - origin.z()) / entityLen * 1.5;
                        entity.setDeltaMovement(entity.getDeltaMovement().add(knockbackX, 0.5, knockbackZ));
                    }
                }
            }
        }
    }
}
