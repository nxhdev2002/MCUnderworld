package com.kiemhiep.api.platform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Platform provider interface for platform-agnostic access to server functions.
 * Implemented differently for Fabric and Paper/Spigot.
 */
public interface PlatformProvider {

    /**
     * Get the current platform.
     */
    Platform getPlatform();

    /**
     * Get a player adapter by UUID.
     */
    CompletableFuture<Optional<PlayerAdapter>> getPlayerAdapter(UUID uuid);

    /**
     * Get all online players.
     */
    CompletableFuture<List<PlayerAdapter>> getOnlinePlayers();

    /**
     * Check if a player is online.
     */
    boolean isPlayerOnline(UUID uuid);

    /**
     * Broadcast a message to all players.
     */
    void broadcast(String message);

    /**
     * Get a world adapter by name.
     */
    CompletableFuture<Optional<WorldAdapter>> getWorldAdapter(String worldName);

    /**
     * Get the main/overworld adapter.
     */
    CompletableFuture<Optional<WorldAdapter>> getMainWorldAdapter();

    /**
     * Get an entity adapter by UUID.
     */
    CompletableFuture<Optional<EntityAdapter>> getEntityAdapter(UUID entityUuid);

    /**
     * Run a task on the main server thread.
     */
    void runTask(Runnable task);

    /**
     * Run a task later on the main server thread.
     */
    void runTaskLater(long delayTicks, Runnable task);

    /**
     * Run a task asynchronously.
     */
    void runTaskAsync(Runnable task);

    /**
     * Cancel a scheduled task.
     */
    void cancelTask(Object taskId);

    /**
     * Check if a player has a permission.
     */
    boolean hasPermission(UUID playerUuid, String permission);

    /**
     * Set a player's permission.
     */
    void setPermission(UUID playerUuid, String permission, boolean value);

    /**
     * Get the scheduler.
     */
    Scheduler getScheduler();

    /**
     * Get the effect player for sounds/particles.
     */
    EffectPlayer getEffectPlayer();
}
