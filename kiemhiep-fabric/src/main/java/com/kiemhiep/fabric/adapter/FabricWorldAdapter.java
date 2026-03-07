package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.WorldAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.LocationAdapter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Fabric adapter for ServerWorld.
 */
public class FabricWorldAdapter implements WorldAdapter {
    private final ServerWorld world;

    public FabricWorldAdapter(ServerWorld world) {
        this.world = world;
    }

    public ServerWorld getHandle() {
        return world;
    }

    @Override
    public String getName() {
        return world.getRegistryKey().getValue().toString();
    }

    @Override
    public Location getSpawnLocation() {
        var pos = world.getSpawnPos();
        return new Location(
                new com.kiemhiep.api.platform.World(getName()),
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5
        );
    }

    @Override
    public void setSpawnLocation(Location location) {
        // TODO: Implement spawn location change
    }

    @Override
    public List<UUID> getPlayers() {
        return world.getPlayers().stream()
                .map(p -> p.getUuid())
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getEntities() {
        List<UUID> entityUuids = new ArrayList<>();
        for (var entity : world.iterateEntities()) {
            entityUuids.add(entity.getUuid());
        }
        return entityUuids;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return world.isChunkLoaded(x, z);
    }

    @Override
    public CompletableFuture<Void> loadChunk(int x, int z) {
        return CompletableFuture.runAsync(() -> {
            world.getChunk(x, z);
        });
    }

    @Override
    public void unloadChunk(int x, int z) {
        // Chunks are automatically unloaded by Minecraft
    }

    @Override
    public long getTime() {
        return world.getTimeOfDay();
    }

    @Override
    public void setTime(long time) {
        world.setTimeOfDay(time);
    }

    @Override
    public boolean isDay() {
        long time = getTime() % 24000;
        return time >= 0 && time < 13000;
    }

    @Override
    public boolean isThundering() {
        return world.isThundering();
    }

    @Override
    public void setThundering(boolean thundering) {
        world.setThundering(thundering);
    }

    @Override
    public UUID spawnEntity(String entityType, Location location) {
        // TODO: Implement entity spawning
        return null;
    }

    @Override
    public boolean removeEntity(UUID entityId) {
        var entity = world.getEntity(entityId);
        if (entity != null) {
            entity.kill();
            return true;
        }
        return false;
    }

    @Override
    public String getBlockType(int x, int y, int z) {
        var blockState = world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z));
        return net.minecraft.registry.Registries.BLOCK.getId(blockState.getBlock()).toString();
    }

    @Override
    public void setBlockType(int x, int y, int z, String blockType) {
        // TODO: Implement block setting
    }

    @Override
    public void spawnParticle(String particleType, double x, double y, double z, int count) {
        // TODO: Implement particle spawning
    }

    @Override
    public void spawnParticle(String particleType, double x, double y, double z, int count,
                              double offsetX, double offsetY, double offsetZ) {
        // TODO: Implement particle spawning with offset
    }

    @Override
    public void spawnParticle(String particleType, double x, double y, double z, int count,
                              double offsetX, double offsetY, double offsetZ, double extra,
                              int r, int g, int b) {
        // TODO: Implement colored particle spawning
    }

    @Override
    public void playSound(double x, double y, double z, String sound, float volume, float pitch) {
        // TODO: Implement sound playing at location
    }

    @Override
    public void setBlock(int x, int y, int z, String material) {
        // TODO: Implement block setting
    }

    @Override
    public String getBlock(int x, int y, int z) {
        return getBlockType(x, y, z);
    }

    @Override
    public Optional<LocationAdapter> getLocationAt(double x, double y, double z, float yaw, float pitch) {
        return Optional.of(new FabricLocationAdapter(world, x, y, z, yaw, pitch));
    }

    @Override
    public Optional<EntityAdapter> getEntity(UUID entityId) {
        var entity = world.getEntity(entityId);
        if (entity != null) {
            return Optional.of(new FabricEntityAdapter(entity));
        }
        return Optional.empty();
    }

    @Override
    public void clearEntityDrops(UUID entityId) {
        // TODO: Implement entity drop clearing
    }
}
