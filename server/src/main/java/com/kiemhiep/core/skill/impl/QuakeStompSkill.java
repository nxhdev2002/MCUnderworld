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
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Set;

/**
 * Dị_Chấn_Đạp - Quake Stomp skill that creates a massive earthquake with each footstep.
 * Area damage with intense earth shaking.
 * Visual: Massive earthquake with ground cracking
 */
public class QuakeStompSkill extends BaseSkill implements IAreaDamage {

    public static final QuakeStompSkill INSTANCE = new QuakeStompSkill();

    private QuakeStompSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }
        // Execute the quake stomp
        executeQuakeStomp(ctx);
    }

    private void executeQuakeStomp(SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (player == null || !(player instanceof FabricPlayerAdapter)) {
            return;
        }

        FabricPlayerAdapter fabricPlayer = (FabricPlayerAdapter) player;
        LivingEntity entity = fabricPlayer.getPlayer();

        if (entity == null || !(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        int casterLevel = ctx.getSkillLevel();
        if (casterLevel <= 0) return;

        // Get stomp center - use getOrigin() instead of getTargetLocation()
        Location centerLocation = ctx.getOrigin();
        if (centerLocation == null) {
            centerLocation = player.getLocation();
        }

        double centerX = centerLocation.x();
        double centerY = centerLocation.y();
        double centerZ = centerLocation.z();

        // Calculate massive area and damage based on level
        // Radius: 8 + (level * 1.2)
        double radius = 8.0 + (casterLevel * 1.2);
        // Damage: 12 + (level * 3.5)
        double baseDamage = 12.0 + (casterLevel * 3.5);

        // Spawn massive ground cracking
        spawnGroundCracks(serverLevel, centerX, centerY, centerZ, 40, casterLevel);
        spawnDustCloudBurst(serverLevel, centerX, centerY, centerZ, 30, radius);
        spawnEarthRipples(serverLevel, centerX, centerY, centerZ, 20, radius);
        spawnCrustBurst(serverLevel, centerX, centerY, centerZ, 25);

        // Sound effects: Massive stone breaking + deep earth rumble - use AMBIENT_CAVE with SoundSource
        serverLevel.playSound(null, (int) centerX, (int) centerY, (int) centerZ, SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.BLOCKS, 2.5f, 0.3f + (RandomSource.create().nextFloat() * 0.15f));
        serverLevel.playSound(null, (int) centerX, (int) centerY, (int) centerZ, SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.BLOCKS, 2.0f, 0.4f + (RandomSource.create().nextFloat() * 0.2f));
        serverLevel.playSound(null, (int) centerX, (int) centerY, (int) centerZ, SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.BLOCKS, 1.8f, 0.9f + (casterLevel * 0.04f));

        // Apply massive area damage
        applyMassiveAreaDamage(serverLevel, centerX, centerY, centerZ, radius, baseDamage, casterLevel);
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        // Area damage is applied in executeQuakeStomp
        executeQuakeStomp(ctx);
    }

    private void spawnGroundCracks(ServerLevel level, double x, double y, double z, int count, int casterLevel) {
        RandomSource random = level.getRandom();

        // Primary cracks radiating outward
        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2;
            double maxDist = 3.0 + (casterLevel * 0.5);
            for (double dist = 1.0; dist < maxDist; dist += 0.3) {
                double px = x + Math.cos(angle) * dist;
                double pz = z + Math.sin(angle) * dist;

                // Dark dust for cracks - use CRIMSON_SPORE instead of DUST for 1.21.11 compatibility
                level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    px, y, pz,
                    1, 0, 0, 0, 0.15
                );

                // END_ROD sparks for cracked earth
                level.sendParticles(ParticleTypes.END_ROD,
                    px, y + 0.5, pz,
                    1, 0.12, 0.08, 0.04, 0.5f
                );
            }
        }

        // Secondary cracks in random directions - use CRIMSON_SPORE
        for (int i = 0; i < count / 2; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * (4.0 + (casterLevel * 0.6));

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * dist, y,
                z + Math.sin(angle) * dist,
                1, 0, 0, 0, 0.18
            );
        }

        // CRIMSON_SPORE for earth displacement
        for (int i = 0; i < count / 3; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * (5.0 + (casterLevel * 0.4));
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * dist, y + 0.3,
                z + Math.sin(angle) * dist,
                1, 0, 0, 0, 0.12
            );
        }
    }

    private void spawnDustCloudBurst(ServerLevel level, double x, double y, double z, int count, double radius) {
        RandomSource random = level.getRandom();

        // Burst dust clouds - use CRIMSON_SPORE for 1.21.11 compatibility
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            double height = random.nextDouble() * 3.0;

            // Thick dust clouds
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * dist, y + height, z + Math.sin(angle) * dist,
                1, 0, 0, 0, 0.2
            );
        }

        // CRIMSON_SPORE for earth energy
        for (int i = 0; i < count / 2; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * dist, y + 2.0 + (i / (double) (count / 2)) * 2.0,
                z + Math.sin(angle) * dist,
                1, 0, 0, 0, 0.15
            );
        }

        // END_ROD sparks for massive energy release
        for (int i = 0; i < count; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * radius,
                y + 1.5 + (random.nextDouble() * 2.5),
                z + (random.nextDouble() - 0.5) * radius,
                2, 0.25, 0.15, 0.08, 0.6f
            );
        }
    }

    private void spawnEarthRipples(ServerLevel level, double x, double y, double z, int count, double radius) {
        RandomSource random = level.getRandom();

        // Expanding ripples
        for (int i = 0; i < count; i++) {
            double waveRadius = 2.0 + (i * 0.5);
            if (waveRadius > radius) break;

            double angle = random.nextDouble() * Math.PI * 2;
            double px = x + Math.cos(angle) * waveRadius;
            double pz = z + Math.sin(angle) * waveRadius;

            // Ripples with dust - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y + 0.5, pz,
                1, 0, 0, 0, 0.12
            );

            // CRIMSON_SPORE for ground ripple
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y + 0.3, pz,
                1, 0, 0, 0, 0.08
            );
        }

        // Circular aura of END_ROD sparks
        for (int i = 0; i < count / 2; i++) {
            double waveRadius = 3.0 + (i * 0.6);
            if (waveRadius > radius) break;

            for (int a = 0; a < 8; a++) {
                double angle = (a / 8.0) * Math.PI * 2;
                level.sendParticles(ParticleTypes.END_ROD,
                    x + Math.cos(angle) * waveRadius, y + 1.0,
                    z + Math.sin(angle) * waveRadius,
                    1, 0.2, 0.12, 0.06, 0.5f
                );
            }
        }
    }

    private void spawnCrustBurst(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // Crust shards flying upward
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * 4.0;
            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;

            // Stone shards with dust - use CRIMSON_SPORE for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y + 1.0, pz,
                1, 0, 0, 0, 0.15
            );

            // END_ROD for shard sparkles
            level.sendParticles(ParticleTypes.END_ROD,
                px, y + 2.0 + (random.nextDouble() * 2.0), pz,
                2, 0.18, 0.1, 0.05, 0.45f
            );
        }

        // CRIMSON_SPORE for earth settling after burst
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + (random.nextDouble() - 0.5) * 6.0,
                y + 0.2 + (random.nextDouble() * 0.5),
                z + (random.nextDouble() - 0.5) * 6.0,
                1, 0, 0, 0, 0.1
            );
        }

        // Final dust cloud at center - use CRIMSON_SPORE
        for (int i = 0; i < 20; i++) {
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + (random.nextDouble() - 0.5) * 2.0,
                y + 1.0 + (random.nextDouble() * 1.5),
                z + (random.nextDouble() - 0.5) * 2.0,
                1, 0, 0, 0, 0.12
            );
        }
    }

    private void applyMassiveAreaDamage(ServerLevel level, double x, double y, double z, double radius, double baseDamage, int casterLevel) {
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
            x - radius, y - 3, z - radius,
            x + radius, y + 4, z + radius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bounds);

        // Get online players list from server
        List<net.minecraft.server.level.ServerPlayer> onlinePlayersList = level.getServer().getPlayerList().getPlayers();

        for (LivingEntity entity : entities) {
            // Skip if entity is a player on the server
            if (entity instanceof net.minecraft.world.entity.player.Player player && onlinePlayersList.contains(player)) {
                continue;
            }
            // Calculate distance from epicenter
            double dx = ((net.minecraft.world.entity.Entity) entity).getX() - x;
            double dz = ((net.minecraft.world.entity.Entity) entity).getZ() - z;
            double distance = Math.sqrt(dx * dx + dz * dz);

            // Damage falloff with distance (closer = more damage)
            float damageMultiplier = 1.0f - (float) (distance / radius);
            float damage = (float) (baseDamage * damageMultiplier);

            if (damage > 0) {
                // Apply damage using damageSources().magic()
                entity.hurt(entity.damageSources().magic(), damage);

                // Massive knockback effect based on level
                double knockbackX = dx / distance;
                double knockbackZ = dz / distance;

                // Knockback strength increases with level
                double knockbackForce = 1.5 + (casterLevel * 0.25);
                entity.push(knockbackX * knockbackForce, 0.5 + (casterLevel * 0.08), knockbackZ * knockbackForce);

                // Apply earthquake debuffs
                int baseDuration = 80 + (casterLevel * 20);

                // SLOWNESS instead of MOVEMENT_SLOWNESS
                int slowLevel = 1 + (casterLevel >= 5 ? 1 : 0) + (casterLevel >= 10 ? 1 : 0);
                entity.addEffect(new MobEffectInstance(
                    MobEffects.SLOWNESS,
                    baseDuration,
                    slowLevel,
                    false, false
                ));

                // Weakness (reduced damage output)
                if (casterLevel >= 3) {
                    entity.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        baseDuration,
                        1 + (casterLevel >= 8 ? 1 : 0),
                        false, false
                    ));
                }

                // Mining fatigue (slower attacks) - use SLOWNESS instead of DIG_SLOWDOWN
                if (casterLevel >= 6) {
                    entity.addEffect(new MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.SLOWNESS,
                        baseDuration,
                        1 + (casterLevel >= 12 ? 1 : 0),
                        false, false
                    ));
                }

                // Small chance for STUN effect (高度震_中断)
                if (casterLevel >= 8 && level.getRandom().nextFloat() < 0.3 + (casterLevel * 0.03)) {
                    // Stun = no attack + slow
                    entity.addEffect(new MobEffectInstance(
                        MobEffects.DARKNESS,
                        20,
                        0,
                        false, false
                    ));
                }
            }
        }
    }
}
