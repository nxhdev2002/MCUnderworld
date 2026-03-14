package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Dau_Song_Song - Seismic Pulse skill that sends destructive waves through the ground.
 * Area damage with earth tremor effect.
 * Visual: Ground震动人 waves with dust clouds
 */
public class SeismicPulseSkill extends BaseSkill implements IAreaDamage {

    public static final SeismicPulseSkill INSTANCE = new SeismicPulseSkill();

    private SeismicPulseSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }
        // Execute the seismic pulse
        executeSeismicPulse(ctx);
    }

    private void executeSeismicPulse(SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (player == null || !(player instanceof FabricPlayerAdapter)) {
            return;
        }

        FabricPlayerAdapter fabricPlayer = (FabricPlayerAdapter) player;
        net.minecraft.world.entity.player.Player entity = fabricPlayer.getPlayer();

        if (entity == null || entity.level() instanceof ServerLevel serverLevel == false) {
            return;
        }

        int casterLevel = player.getLevel();
        if (casterLevel <= 0) return;

        // Get pulse center (player location)
        Location centerLocation = player.getLocation();
        if (centerLocation == null) {
            centerLocation = player.getLocation();
        }

        // Use player's level as ServerLevel
        ServerLevel sl = (ServerLevel) entity.level();
        double centerX = centerLocation.x();
        double centerY = centerLocation.y();
        double centerZ = centerLocation.z();

        // Calculate area and damage based on level
        // Radius: 5 + (level * 0.8)
        double radius = 5.0 + (casterLevel * 0.8);
        // Damage: 8 + (level * 2.5)
        double baseDamage = 8.0 + (casterLevel * 2.5);

        // Spawn ground震 động particles
        spawnGroundWaves(sl, centerX, centerY, centerZ, 30, casterLevel);
        spawnDustClouds(sl, centerX, centerY, centerZ, 20, radius);
        spawnCracks(sl, centerX, centerY, centerZ, 15);

        // Sound effects: Earth rumble + stone cracking with SoundSource
        sl.playSound(null, centerX, centerY, centerZ, SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.5f, 0.5f + (RandomSource.create().nextFloat() * 0.2f));

        // Apply area damage
        applyAreaDamage(sl, centerX, centerY, centerZ, radius, baseDamage, casterLevel);
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        // Area damage is applied in executeSeismicPulse
        executeSeismicPulse(ctx);
    }

    private void spawnGroundWaves(ServerLevel level, double x, double y, double z, int count, int casterLevel) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            // Expanding ring waves
            double waveRadius = 1.0 + (i * 0.3);
            double angle = random.nextDouble() * Math.PI * 2;
            double px = x + Math.cos(angle) * waveRadius;
            double pz = z + Math.sin(angle) * waveRadius;

        // Dust particles for earth waves - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y, pz,
                1, 0, 0, 0, 0.2f
            );
        }

        // Add CRIMSON_SPORE for earth energy glow
        for (int i = 0; i < count / 3; i++) {
            double waveRadius = 2.0 + (i * 0.2);
            double angle = random.nextDouble() * Math.PI * 2;
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * waveRadius, y + 0.5, z + Math.sin(angle) * waveRadius,
                1, 0.0, 0.1, 0.0, 0.25f
            );
        }
    }

    private void spawnDustClouds(ServerLevel level, double x, double y, double z, int count, double radius) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;
            double py = y + random.nextDouble() * 2.0;

            // Dust clouds rising - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, py, pz,
                1, 0, 0, 0, 0.3f
            );
        }

        // END_ROD sparks for seismic energy
        for (int i = 0; i < count / 2; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            level.sendParticles(ParticleTypes.END_ROD,
                x + Math.cos(angle) * dist, y + 1.5, z + Math.sin(angle) * dist,
                2, 0.2, 0.1, 0.0, 0.35f
            );
        }

        // MYCELIUM for earth grounding
        for (int i = 0; i < count / 3; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            level.sendParticles(ParticleTypes.SMOKE,
                x + Math.cos(angle) * dist, y + 0.2, z + Math.sin(angle) * dist,
                1, 0.0, 0.0, 0.0, 0.2f
            );
        }
    }

    private void spawnCracks(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + (random.nextDouble() * 3.0);
            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;

            // Cracks with crimson particles and END_ROD
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y, pz,
                1, 0, 0, 0, 0.2f
            );

            level.sendParticles(ParticleTypes.END_ROD,
                px, y + 0.5, pz,
                1, 0.1, 0.1, 0.1, 0.4f
            );
        }
    }

    private void applyAreaDamage(ServerLevel level, double x, double y, double z, double radius, double baseDamage, int casterLevel) {
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
            x - radius, y - 2, z - radius,
            x + radius, y + 3, z + radius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bounds);

        for (LivingEntity entity : entities) {
            if (entity != null) {
                // Calculate distance from center
                double dx = entity.getX() - x;
                double dz = entity.getZ() - z;
                double distance = Math.sqrt(dx * dx + dz * dz);

                // Damage falloff based on distance
                float damageMultiplier = 1.0f - (float) (distance / radius);
                float damage = (float) (baseDamage * damageMultiplier);

                if (damage > 0) {
                    // Apply damage
                    entity.hurt(entity.level().damageSources().magic(), damage);

                    // Knockback effect
                    double knockbackX = dx / distance;
                    double knockbackZ = dz / distance;
                    entity.push(knockbackX * 0.6, 0.3, knockbackZ * 0.6);

                    // Apply earthquake effect (slowness + weak)
                    int duration = 30 + (casterLevel * 10);
                    int knockbackAmplifier = 0;

                    if (casterLevel >= 5) knockbackAmplifier = 1;
                    if (casterLevel >= 10) knockbackAmplifier = 2;

                    entity.addEffect(new MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.SLOWNESS,
                        duration,
                        knockbackAmplifier,
                        false, false
                    ));

                    if (casterLevel >= 7) {
                        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.WEAKNESS,
                            duration,
                            1,
                            false, false
                        ));
                    }
                }
            }
        }
    }
}
