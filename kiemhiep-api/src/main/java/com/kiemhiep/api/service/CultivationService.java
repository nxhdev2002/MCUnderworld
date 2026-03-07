package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.model.CultivationBreakthroughResult;
import com.kiemhiep.api.model.CultivationRealm;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for cultivation system.
 * Handles cultivation progress, breakthroughs, and experience.
 */
public interface CultivationService {

    /**
     * Get a player's cultivation data.
     *
     * @param playerId The player's UUID
     * @return CompletableFuture with the player's cultivation data
     */
    CompletableFuture<Cultivation> getCultivation(UUID playerId);

    /**
     * Set a player's cultivation data.
     *
     * @param playerId   The player's UUID
     * @param cultivation The new cultivation data
     * @return CompletableFuture that completes when done
     */
    CompletableFuture<Void> setCultivation(UUID playerId, Cultivation cultivation);

    /**
     * Add experience to a player's cultivation.
     * May trigger sub-level up or breakthrough.
     *
     * @param playerId The player's UUID
     * @param amount   The amount of experience to add
     * @return CompletableFuture with the actual exp added
     */
    CompletableFuture<Integer> addExp(UUID playerId, int amount);

    /**
     * Set a player's sub-level directly.
     *
     * @param playerId  The player's UUID
     * @param subLevel  The new sub-level (0-8)
     * @return CompletableFuture that completes when done
     */
    CompletableFuture<Void> setSubLevel(UUID playerId, int subLevel);

    /**
     * Attempt a cultivation breakthrough to the next realm.
     *
     * @param playerId The player's UUID
     * @return CompletableFuture with the breakthrough result
     */
    CompletableFuture<CultivationBreakthroughResult> breakthrough(UUID playerId);

    /**
     * Get all cultivation realms.
     *
     * @return List of all cultivation realms
     */
    List<CultivationRealm> getRealms();

    /**
     * Get the experience required to reach next sub-level for a given realm.
     *
     * @param realmLevel The realm level
     * @return Experience required
     */
    int getExpRequired(int realmLevel);

    /**
     * Initialize cultivation data for a new player.
     *
     * @param playerId The player's UUID
     * @return CompletableFuture with the initial cultivation data
     */
    CompletableFuture<Cultivation> initializeCultivation(UUID playerId);
}
