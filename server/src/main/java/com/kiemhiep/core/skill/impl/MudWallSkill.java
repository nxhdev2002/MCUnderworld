package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stuck - Mud Wall skill that creates a defensive barrier of hardened mud.
 * Single target with defensive application.
 * Visual: Mud wall barrier with mud splash
 */
public class MudWallSkill extends BaseSkill implements ISingleTargetDamage {

    public static final MudWallSkill INSTANCE = new MudWallSkill();

    private MudWallSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }
        // Execute the mud wall creation
        executeMudWall(ctx);
    }

    private void executeMudWall(SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (player == null || !(player instanceof FabricPlayerAdapter)) {
            return;
        }

        FabricPlayerAdapter fabricPlayer = (FabricPlayerAdapter) player;
        net.minecraft.world.entity.player.Player entity = fabricPlayer.getPlayer();

        if (entity == null || entity.level() instanceof ServerLevel serverLevel == false) {
            return;
        }

        // Get caster level for wall strength
        int casterLevel = ctx.getSkillLevel();
        if (casterLevel <= 0) return;

        // Calculate wall health and defense boost based on level
        // Wall health: 100 + (level * 25)
        int wallHealth = 100 + (casterLevel * 25);
        float defenseBonus = 0.2f + (casterLevel * 0.05f);

        // Use getOrigin() instead of getTargetLocation()
        Location wallLocation = ctx.getOrigin();
        if (wallLocation == null) {
            // Default to player's position if no target
            wallLocation = player.getLocation();
        }

        // Use player's level as ServerLevel
        ServerLevel sl = (ServerLevel) entity.level();
        double x = wallLocation.x();
        double y = wallLocation.y();
        double z = wallLocation.z();

        // Spawn mud splash particles
        spawnMudSplash(sl, x, y, z, 25);
        spawnMudWallFormation(sl, x, y, z, 15, casterLevel);

        // Sound effects: Mud splash + earth thick sound with SoundSource
        sl.playSound(null, (int) x, (int) y, (int) z, SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.7f + (RandomSource.create().nextFloat() * 0.15f));

        // Create visual wall barrier effect
        spawnMudBarrierEffect(sl, x, y, z, 8 + (casterLevel / 2));

        // Apply debuff to enemies near the wall
        applyMudDebuffToNearby(sl, x, y, z, 3.0 + (casterLevel * 0.3));
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        // Mud Wall is defensive - no direct damage
        executeMudWall(ctx);
    }

    private void spawnMudSplash(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 3.0;
            double offsetY = (random.nextDouble() - 0.5) * 2.5;
            double offsetZ = (random.nextDouble() - 0.5) * 3.0;

            // CRIMSON_SPORE for muddy brown color
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + offsetX, y + offsetY, z + offsetZ,
                1, 0.0, 0.1, 0.0, 0.3f + random.nextFloat() * 0.2f
            );
        }

        // Mud particles
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.SMOKE,
                x + (random.nextDouble() - 0.5) * 2.5,
                y + (random.nextDouble() * 1.5),
                z + (random.nextDouble() - 0.5) * 2.5,
                1, 0.0, 0.0, 0.0, 0.2f
            );
        }
    }

    private void spawnMudWallFormation(ServerLevel level, double x, double y, double z, int count, int casterLevel) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 4.0;
            double offsetY = (random.nextDouble() * 2.5);
            double offsetZ = (random.nextDouble() - 0.5) * 4.0;

            // Circular wall formation
            double radius = 1.5 + (random.nextDouble() * 1.0);
            double angle = random.nextDouble() * Math.PI * 2;
            double wallX = x + Math.cos(angle) * radius;
            double wallY = y + offsetY;
            double wallZ = z + Math.sin(angle) * radius;

            // CRIMSON_SPORE instead of DUST for 1.21.11 compatibility
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                wallX, wallY, wallZ,
                1, 0, 0, 0, 0.25f
            );
        }

        // End Rod sparks for wall shimmer
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * 3.5,
                y + 1.5 + (random.nextDouble() * 1.5),
                z + (random.nextDouble() - 0.5) * 3.5,
                2, 0.15, 0.1, 0.05, 0.4f
            );
        }
    }

    private void spawnMudBarrierEffect(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // barriers with earth particles - use CRIMSON_SPORE instead of DUST
        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2;
            double radius = 2.0 + (random.nextDouble() * 0.5);

            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * radius, y + 1.5, z + Math.sin(angle) * radius,
                1, 0, 0, 0, 0.2f
            );
        }

        // Persistent mud particles at wall height
        for (int i = 0; i < count; i++) {
            level.sendParticles(ParticleTypes.SMOKE,
                x + (random.nextDouble() - 0.5) * 4.0,
                y + (i % 3) * 0.5 + 1.0,
                z + (random.nextDouble() - 0.5) * 4.0,
                1, 0.0, 0.0, 0.0, 0.25f
            );
        }
    }

    private void applyMudDebuffToNearby(ServerLevel level, double x, double y, double z, double radius) {
        // Find all entities in the mud radius
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
            x - radius, y - 1, z - radius,
            x + radius, y + 3, z + radius
        );

        List<net.minecraft.world.entity.LivingEntity> entities = level.getEntitiesOfClass(
            net.minecraft.world.entity.LivingEntity.class, bounds
        );

        // Get online players set from server - usegetPlayerList().getPlayers() and convert to set
        Set<net.minecraft.server.level.ServerPlayer> onlinePlayers = level.getServer().getPlayerList().getPlayers().stream()
            .collect(java.util.stream.Collectors.toSet());

        for (net.minecraft.world.entity.LivingEntity entity : entities) {
            // Skip if entity is a player on the server
            if (entity instanceof net.minecraft.world.entity.player.Player player && onlinePlayers.contains(player)) {
                continue;
            }
            // Apply slowness effect - use SLOWNESS instead of MOVEMENT_SLOWNESS
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SLOWNESS,
                60 + level.random.nextInt(40),
                1 + (level.random.nextInt(2)),
                false, false
            ));
        }
    }
}
