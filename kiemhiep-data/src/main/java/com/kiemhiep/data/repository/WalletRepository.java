package com.kiemhiep.data.repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for economy/wallet data.
 */
public interface WalletRepository {

    /**
     * Get a player's balance for a specific currency.
     */
    CompletableFuture<Integer> getBalance(UUID playerId, String currencyType);

    /**
     * Get a player's balances for all currencies.
     */
    CompletableFuture<Map<String, Integer>> getAllBalances(UUID playerId);

    /**
     * Set a player's balance for a specific currency.
     */
    CompletableFuture<Void> setBalance(UUID playerId, String currencyType, int amount);

    /**
     * Add to a player's balance.
     */
    CompletableFuture<Void> addBalance(UUID playerId, String currencyType, int amount);

    /**
     * Remove from a player's balance.
     */
    CompletableFuture<Boolean> removeBalance(UUID playerId, String currencyType, int amount);

    /**
     * Initialize a wallet for a new player.
     */
    CompletableFuture<Void> initializeWallet(UUID playerId);

    /**
     * Delete a player's wallet data.
     */
    CompletableFuture<Void> deleteWallet(UUID playerId);
}
