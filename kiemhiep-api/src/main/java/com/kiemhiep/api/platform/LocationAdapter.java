package com.kiemhiep.api.platform;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter interface for location operations.
 */
public interface LocationAdapter {

    /**
     * Get the world.
     */
    World getWorld();

    /**
     * Get the X coordinate.
     */
    double getX();

    /**
     * Get the Y coordinate.
     */
    double getY();

    /**
     * Get the Z coordinate.
     */
    double getZ();

    /**
     * Get the block X coordinate.
     */
    int getBlockX();

    /**
     * Get the block Y coordinate.
     */
    int getBlockY();

    /**
     * Get the block Z coordinate.
     */
    int getBlockZ();

    /**
     * Get the yaw.
     */
    float getYaw();

    /**
     * Get the pitch.
     */
    float getPitch();

    /**
     * Get the chunk X coordinate.
     */
    default int getChunkX() {
        return getBlockX() >> 4;
    }

    /**
     * Get the chunk Z coordinate.
     */
    default int getChunkZ() {
        return getBlockZ() >> 4;
    }

    /**
     * Get the block type at this location.
     */
    String getBlockType();

    /**
     * Set the block type at this location.
     */
    void setBlockType(String blockType);

    /**
     * Check if this location is in a different world than another location.
     */
    boolean isDifferentWorld(LocationAdapter other);

    /**
     * Get the distance to another location.
     */
    double distance(LocationAdapter other);

    /**
     * Get the squared distance to another location.
     */
    double distanceSquared(LocationAdapter other);

    /**
     * Spawn particles at this location.
     */
    void spawnParticle(String particleType, int count);

    /**
     * Play a sound at this location.
     */
    void playSound(String sound, float volume, float pitch);

    /**
     * Get a nearby entity by UUID.
     */
    Optional<EntityAdapter> getEntity(UUID entityId);
}
