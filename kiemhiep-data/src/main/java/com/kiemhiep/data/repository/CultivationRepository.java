package com.kiemhiep.data.repository;

import com.kiemhiep.api.model.Cultivation;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for cultivation data.
 */
public interface CultivationRepository {

    /**
     * Get a player's cultivation data.
     */
    CompletableFuture<Cultivation> getCultivation(UUID playerId);

    /**
     * Save a player's cultivation data.
     */
    CompletableFuture<Void> saveCultivation(Cultivation cultivation);

    /**
     * Update a player's realm level.
     */
    CompletableFuture<Void> updateRealm(UUID playerId, int realmLevel);

    /**
     * Update a player's sub-level.
     */
    CompletableFuture<Void> updateSubLevel(UUID playerId, int subLevel);

    /**
     * Update a player's experience.
     */
    CompletableFuture<Void> updateExp(UUID playerId, int exp);

    /**
     * Delete a player's cultivation data.
     */
    CompletableFuture<Void> deleteCultivation(UUID playerId);

    /**
     * Check if a player has cultivation data.
     */
    CompletableFuture<Boolean> hasCultivation(UUID playerId);
}
