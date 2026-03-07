package com.kiemhiep.data.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Base repository interface for player-related data.
 */
public interface PlayerRepository {

    /**
     * Get a player's ID by name.
     */
    CompletableFuture<Optional<UUID>> getPlayerIdByName(String name);

    /**
     * Get a player's name by ID.
     */
    CompletableFuture<Optional<String>> getPlayerNameById(UUID playerId);

    /**
     * Create a new player record.
     */
    CompletableFuture<Void> createPlayer(UUID playerId, String name);

    /**
     * Check if a player exists.
     */
    CompletableFuture<Boolean> playerExists(UUID playerId);

    /**
     * Update a player's name.
     */
    CompletableFuture<Void> updatePlayerName(UUID playerId, String name);

    /**
     * Get or create a player record.
     */
    CompletableFuture<Void> getOrCreatePlayer(UUID playerId, String name);
}
