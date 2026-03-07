package com.kiemhiep.api.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for economy system.
 * Handles multi-currency wallets and transactions.
 */
public interface EconomyService {

    /**
     * Get a player's balance for a specific currency.
     *
     * @param playerId     The player's UUID
     * @param currencyType The currency type (GOLD, SILVER, SPIRIT_STONE)
     * @return CompletableFuture with the balance
     */
    CompletableFuture<Integer> getBalance(UUID playerId, String currencyType);

    /**
     * Set a player's balance for a specific currency.
     *
     * @param playerId     The player's UUID
     * @param currencyType The currency type
     * @param amount       The new balance
     * @return CompletableFuture that completes when done
     */
    CompletableFuture<Void> setBalance(UUID playerId, String currencyType, int amount);

    /**
     * Add to a player's balance.
     *
     * @param playerId     The player's UUID
     * @param currencyType The currency type
     * @param amount       The amount to add
     * @return CompletableFuture that completes when done
     */
    CompletableFuture<Void> addBalance(UUID playerId, String currencyType, int amount);

    /**
     * Remove from a player's balance.
     *
     * @param playerId     The player's UUID
     * @param currencyType The currency type
     * @param amount       The amount to remove
     * @return CompletableFuture with true if successful, false if insufficient funds
     */
    CompletableFuture<Boolean> removeBalance(UUID playerId, String currencyType, int amount);

    /**
     * Check if a player has sufficient balance.
     *
     * @param playerId     The player's UUID
     * @param currencyType The currency type
     * @param amount       The amount to check
     * @return CompletableFuture with true if sufficient funds
     */
    CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyType, int amount);

    /**
     * Transfer balance between players.
     *
     * @param fromPlayerId The source player's UUID
     * @param toPlayerId   The destination player's UUID
     * @param currencyType The currency type
     * @param amount       The amount to transfer
     * @return CompletableFuture with true if successful
     */
    CompletableFuture<Boolean> transfer(UUID fromPlayerId, UUID toPlayerId, String currencyType, int amount);

    /**
     * Convert between currency types.
     *
     * @param playerId      The player's UUID
     * @param fromCurrency  The currency to convert from
     * @param toCurrency    The currency to convert to
     * @param amount        The amount to convert
     * @return CompletableFuture with the converted amount
     */
    CompletableFuture<Integer> convertCurrency(UUID playerId, String fromCurrency, String toCurrency, int amount);

    /**
     * Initialize wallet for a new player.
     *
     * @param playerId The player's UUID
     * @return CompletableFuture that completes when done
     */
    CompletableFuture<Void> initializeWallet(UUID playerId);
}
