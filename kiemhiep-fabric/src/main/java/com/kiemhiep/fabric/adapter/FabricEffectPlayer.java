package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * Fabric implementation of EffectPlayer.
 */
public class FabricEffectPlayer implements EffectPlayer {
    private final MinecraftServer server;

    public FabricEffectPlayer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void playSound(PlayerAdapter playerAdapter, String sound, float volume, float pitch) {
        if (playerAdapter instanceof FabricPlayerAdapter) {
            ServerPlayerEntity player = ((FabricPlayerAdapter) playerAdapter).getHandle();
            // TODO: Convert sound string to registry entry
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, volume, pitch);
        }
    }

    @Override
    public void playSound(WorldAdapter worldAdapter, double x, double y, double z, String sound, float volume, float pitch) {
        if (worldAdapter instanceof FabricWorldAdapter) {
            ServerWorld world = ((FabricWorldAdapter) worldAdapter).getHandle();
            // TODO: Play sound at location
        }
    }

    @Override
    public void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count) {
        if (worldAdapter instanceof FabricWorldAdapter) {
            ServerWorld world = ((FabricWorldAdapter) worldAdapter).getHandle();
            // TODO: Convert particleType and spawn particles
            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    count,
                    0.5, 0.5, 0.5,
                    0.0
            );
        }
    }

    @Override
    public void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count,
                              double offsetX, double offsetY, double offsetZ) {
        if (worldAdapter instanceof FabricWorldAdapter) {
            ServerWorld world = ((FabricWorldAdapter) worldAdapter).getHandle();
            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    count,
                    offsetX, offsetY, offsetZ,
                    0.0
            );
        }
    }

    @Override
    public void spawnParticle(WorldAdapter worldAdapter, double x, double y, double z, String particleType, int count,
                              double offsetX, double offsetY, double offsetZ, double extra, int r, int g, int b) {
        if (worldAdapter instanceof FabricWorldAdapter) {
            ServerWorld world = ((FabricWorldAdapter) worldAdapter).getHandle();
            // For colored particles like DUST
            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.DUST,
                    x, y, z,
                    count,
                    offsetX, offsetY, offsetZ,
                    extra
            );
        }
    }

    @Override
    public void sendTitle(PlayerAdapter playerAdapter, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (playerAdapter instanceof FabricPlayerAdapter) {
            ServerPlayerEntity player = ((FabricPlayerAdapter) playerAdapter).getHandle();
            // TODO: Send title packet
        }
    }

    @Override
    public void sendActionBar(PlayerAdapter playerAdapter, String message) {
        if (playerAdapter instanceof FabricPlayerAdapter) {
            ServerPlayerEntity player = ((FabricPlayerAdapter) playerAdapter).getHandle();
            player.sendMessage(net.minecraft.text.Text.literal(message), true);
        }
    }

    @Override
    public void spawnLightning(WorldAdapter worldAdapter, double x, double y, double z) {
        if (worldAdapter instanceof FabricWorldAdapter) {
            ServerWorld world = ((FabricWorldAdapter) worldAdapter).getHandle();
            // TODO: Spawn lightning bolt
        }
    }
}
