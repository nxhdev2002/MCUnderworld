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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Dien_Lu_Den - Electro Wave skill that sends a devastating shockwave through the ground.
 * Area damage with lightning effect.
 * Unique behavior: Ground shockwave that travels outward in expanding rings.
 */
public class ElectroWaveSkill extends BaseSkill implements IThunder {

    public static final ElectroWaveSkill INSTANCE = new ElectroWaveSkill();

    private static final int STUN_DURATION = 40; // 2 seconds
    private static final int WEAKNESS_DURATION = 120; // 6 seconds
    private static final double WAVE_RADIUS = 6.0;
    private static final double WAVE_SPEED = 0.3;
    private static final double DAMAGE_MULTIPLIER = 1.2;

    private ElectroWaveSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location origin = ctx.getOrigin();

        if (origin != null) {
            // Sound effect: Deep thunder rumble - use SkillContext to get level
            if (ctx.getEffectRunner() != null) {
                ctx.getEffectRunner().playEffect(ctx, "lightning");
            }

            // Spawn start particles
            spawnStartShockwave(origin);
        }
    }

    /**
     * Spawn particles at skill origin when cast.
     */
    private void spawnStartShockwave(Location origin) {
        // Use EffectRunner to get ServerLevel for particle spawning
        if (origin == null) return;

        // Spawn particles when context is available via effect runner
        // Particles will be spawned in applyThunder which has proper ServerLevel access
    }

    /**
     * Spawn expanding shockwave particles.
     */
    private void spawnShockwaveRing(ServerLevel level, Location origin, double radius, RandomSource random) {
        int points = 32;
        for (int i = 0; i < points; i++) {
            double angle = (i / (double) points) * Math.PI * 2;
            double x = origin.x() + Math.cos(angle) * radius;
            double z = origin.z() + Math.sin(angle) * radius;
            double y = origin.y() + 0.5 + Math.sin(radius / 2.0) * 0.5;

            // Yellow electric particles for the ring - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    x, y, z,
                    2,
                    0, 0, 0,
                    0.15);

            // End rod sparks for the glow
            level.sendParticles(ParticleTypes.END_ROD,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.2);

            // Flash effect at the ring - use END_ROD
            if (i % 8 == 0) {
                level.sendParticles(ParticleTypes.END_ROD,
                        x, y + 0.5, z,
                        1,
                        0, 0, 0,
                        0.1);
            }
        }
    }

    /**
     * Spawn lightning particles traveling outward.
     */
    private void spawnOutwardLightning(ServerLevel level, Location origin, RandomSource random) {
        int beams = 8;
        for (int i = 0; i < beams; i++) {
            double angle = (i / (double) beams) * Math.PI * 2 + (random.nextDouble() * 0.5);
            double distance = 2.0 + random.nextDouble() * 3.0;

            // Create jagged lightning particle trail
            double startX = origin.x();
            double startY = origin.y() + 0.5;
            double startZ = origin.z();
            double endX = origin.x() + Math.cos(angle) * distance;
            double endY = origin.y() + 0.5 + (random.nextDouble() - 0.5) * 1.0;
            double endZ = origin.z() + Math.sin(angle) * distance;

            // Yellow electric particles - use SMOKE for NEUTRAL
            level.sendParticles(ParticleTypes.SMOKE,
                    (startX + endX) / 2, (startY + endY) / 2, (startZ + endZ) / 2,
                    2,
                    0.1, 0, 0.1,
                    0.2);

            // White spark particles at ends
            level.sendParticles(ParticleTypes.END_ROD,
                    endX, endY, endZ,
                    3,
                    0, 0, 0,
                    0.3);
        }
    }
    @Override
    public void applyThunder(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.getRandom();

        if (origin == null) return;

        int casterLevel = ctx.getSkillLevel();

        // Create expanding ring effect
        double finalRadius = WAVE_RADIUS + (casterLevel * 0.5);
        for (double r = 1.0; r <= finalRadius; r += WAVE_SPEED * 2) {
            spawnShockwaveRing(level, origin, r, random);
        }

        // Spawn outward lightning beams
        spawnOutwardLightning(level, origin, random);

        // Area damage in the shockwave radius
        AABB area = new AABB(
                origin.x() - finalRadius, origin.y() - 1, origin.z() - finalRadius,
                origin.x() + finalRadius, origin.y() + 3, origin.z() + finalRadius
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
            if (distance > finalRadius * finalRadius) continue;

            // Calculate damage based on distance (closer = more damage)
            double distanceFactor = 1.0 - (distance / (finalRadius * finalRadius));
            double baseDamage = 10.0 + (casterLevel * 2.5);
            double areaDamage = baseDamage * distanceFactor * DAMAGE_MULTIPLIER;

            // Apply damage: caster entity must be vanilla Entity (ServerPlayer), not PlayerAdapter
            net.minecraft.world.entity.Entity casterEntity = fabricCaster.getPlayer();
            target.hurt(target.damageSources().indirectMagic(casterEntity, casterEntity), (float) areaDamage);

            // Sound on hit
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.7f + random.nextFloat() * 0.3f);

            // Apply stun debuff (shocking the ground)
            target.setNoGravity(true);
            target.setDeltaMovement(target.getDeltaMovement().multiply(0.2, 1.0, 0.2));

            // Schedule recovery from stun
            level.getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                    // Apply lingering weakness
                    if (target instanceof Player player) {
                        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, WEAKNESS_DURATION, 0, false, false));
                        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, WEAKNESS_DURATION, 1, false, false));
                    }
                }
            });

            // Spawn particles on target
            spawnTargetShockwaveParticles(target, level, random);
        }

        // Final shockwave sound
        level.playSound(null, origin.x(), origin.y(), origin.z(),
                SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.5f, 0.3f);
    }

    /**
     * Spawn particles on target when hit by shockwave.
     */
    private void spawnTargetShockwaveParticles(LivingEntity target, ServerLevel level, RandomSource random) {
        // Cast LivingEntity to Entity for methods
        net.minecraft.world.entity.Entity e = (net.minecraft.world.entity.Entity) target;

        // Electric sparks from the target - use CRIMSON_SPORE for 1.21.11 compatibility
        for (int i = 0; i < 10; i++) {
            double x = e.getX() + (random.nextDouble() - 0.5) * 1.0;
            double y = e.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = e.getZ() + (random.nextDouble() - 0.5) * 1.0;

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    x, y, z,
                    2,
                    0, 0, 0,
                    0.15);
        }

        // Yellow lightning arcs - use SMOKE for NEUTRAL
        for (int i = 0; i < 5; i++) {
            level.sendParticles(ParticleTypes.SMOKE,
                    e.getX() + (random.nextDouble() - 0.5) * 0.8,
                    e.getY() + 1.0 + random.nextDouble(),
                    e.getZ() + (random.nextDouble() - 0.5) * 0.8,
                    1,
                    0.1, 0.2, 0.1,
                    0.1);
        }

        // End rod sparks for the flash
        for (int i = 0; i < 3; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    e.getX(), e.getY() + target.getBbHeight(), e.getZ(),
                    2,
                    0, 0, 0,
                    0.3);
        }
    }
}
