package com.kiemhiep.api.platform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter interface for world operations.
 */
public interface WorldAdapter {

    /**
     * Get the world name.
     */
    String getName();

    /**
     * Get the spawn location.
     */
    Location getSpawnLocation();

    /**
     * Set the spawn location.
     */
    void setSpawnLocation(Location location);

    /**
     * Get all players in the world.
     */
    List<UUID> getPlayers();

    /**
     * Get all entities in the world.
     */
    List<UUID> getEntities();

    /**
     * Check if a chunk is loaded.
     */
    boolean isChunkLoaded(int x, int z);

    /**
     * Load a chunk asynchronously.
     */
    CompletableFuture<Void> loadChunk(int x, int z);

    /**
     * Unload a chunk.
     */
    void unloadChunk(int x, int z);

    /**
     * Get the world time.
     */
    long getTime();

    /**
     * Set the world time.
     */
    void setTime(long time);

    /**
     * Check if it's day time.
     */
    boolean isDay();

    /**
     * Check if it's thundering.
     */
    boolean isThundering();

    /**
     * Set thundering state.
     */
    void setThundering(boolean thundering);

    /**
     * Spawn an entity at a location.
     */
    UUID spawnEntity(String entityType, Location location);

    /**
     * Remove an entity.
     */
    boolean removeEntity(UUID entityId);

    /**
     * Get the block type at a position.
     */
    String getBlockType(int x, int y, int z);

    /**
     * Set the block type at a position.
     */
    void setBlockType(int x, int y, int z, String blockType);

    /**
     * Spawn particles at a location.
     */
    void spawnParticle(String particleType, double x, double y, double z, int count);

    /**
     * Spawn particles with offset.
     */
    void spawnParticle(String particleType, double x, double y, double z, int count,
                       double offsetX, double offsetY, double offsetZ);

    /**
     * Spawn particles with color (for redstone).
     */
    void spawnParticle(String particleType, double x, double y, double z, int count,
                       double offsetX, double offsetY, double offsetZ, double extra,
                       int r, int g, int b);

    /**
     * Play a sound at a location.
     */
    void playSound(double x, double y, double z, String sound, float volume, float pitch);

    /**
     * Set a block.
     */
    void setBlock(int x, int y, int z, String material);

    /**
     * Get a block.
     */
    String getBlock(int x, int y, int z);

    /**
     * Get a location adapter.
     */
    Optional<LocationAdapter> getLocationAt(double x, double y, double z, float yaw, float pitch);

    /**
     * Get an entity adapter.
     */
    Optional<EntityAdapter> getEntity(UUID entityId);

    /**
     * Clear entity drops.
     */
    void clearEntityDrops(UUID entityId);
}
