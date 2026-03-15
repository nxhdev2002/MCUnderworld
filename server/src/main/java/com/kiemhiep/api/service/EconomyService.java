package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Wallet;

import java.util.List;

/**
 * Service for economy management with multi-currency support.
 * <p>
 * Currency is stored as signed numbers (long) in database to avoid Minecraft's 64-item stack limit.
 * Currency types: GOLD, SILVER, SPIRIT_STONE
 */
public interface EconomyService {

    /**
     * Get wallet balance for a player.
     * Returns 0 if wallet doesn't exist.
     *
     * @param playerId   the player ID
     * @param currency   currency type (GOLD, SILVER, SPIRIT_STONE)
     * @return balance (can be negative if allowed)
     */
    long getBalance(long playerId, String currency);

    /**
     * Get all wallets for a player.
     *
     * @param playerId the player ID
     * @return list of wallets (can be empty)
     */
    List<Wallet> getWallets(long playerId);

    /**
     * Add currency to a player's wallet (admin operation).
     * Creates wallet if it doesn't exist.
     *
     * @param playerId   the player ID
     * @param currency   currency type (GOLD, SILVER, SPIRIT_STONE)
     * @param amount     amount to add (positive)
     */
    void add(long playerId, String currency, long amount);

    /**
     * Subtract currency from a player's wallet (admin operation).
     *
     * @param playerId   the player ID
     * @param currency   currency type (GOLD, SILVER, SPIRIT_STONE)
     * @param amount     amount to subtract (positive)
     * @throws IllegalArgumentException if insufficient balance
     */
    void subtract(long playerId, String currency, long amount);

    /**
     * Transfer currency from one player to another.
     *
     * @param fromPlayerId sender player ID
     * @param toPlayerId   receiver player ID
     * @param currency     currency type (GOLD, SILVER, SPIRIT_STONE)
     * @param amount       amount to transfer (positive)
     * @return true if transfer succeeded, false if insufficient balance
     */
    boolean transfer(long fromPlayerId, long toPlayerId, String currency, long amount);

    /**
     * Get list of default currency types.
     *
     * @return list of currency names (GOLD, SILVER, SPIRIT_STONE)
     */
    List<String> getDefaultCurrencies();

    /**
     * Get default wallet currency for a player (creates if not exists).
     * This is typically SPIRIT_STONE.
     *
     * @param playerId the player ID
     * @return default currency type
     */
    String getDefaultCurrency(long playerId);
}
