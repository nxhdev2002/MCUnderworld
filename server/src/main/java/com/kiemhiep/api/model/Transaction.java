package com.kiemhiep.api.model;

import java.time.Instant;

/**
 * Transaction record for economy operations.
 * <p>
 * Types:
 * <ul>
 *   <li>ADD - Admin add currency (e.g., reward)</li>
 *   <li>REMOVE - Admin remove currency (e.g., penalty)</li>
 *   <li>TRANSFER - Player to player transfer</li>
 *   <li>REWARD - Quest/boss reward</li>
 *   <li>PAYMENT - Payment for goods/services</li>
 *   <li>CONVERSION - Currency item to wallet conversion</li>
 * </ul>
 */
public record Transaction(
    long id,
    Long fromPlayerId,      // null for ADD/REWARD (system source)
    Long toPlayerId,        // null for REMOVE (system destination)
    long amount,
    String currencyType,
    String type,
    Instant timestamp
) {

    // Transaction types
    public static final String TYPE_ADD = "ADD";
    public static final String TYPE_REMOVE = "REMOVE";
    public static final String TYPE_TRANSFER = "TRANSFER";
    public static final String TYPE_REWARD = "REWARD";
    public static final String TYPE_PAYMENT = "PAYMENT";
    public static final String TYPE_CONVERSION = "CONVERSION";

    /**
     * Create a new transaction for ADD operation (system to player).
     */
    public static Transaction createAdd(long playerId, long amount, String currencyType) {
        return new Transaction(0, null, playerId, amount, currencyType, TYPE_ADD, Instant.now());
    }

    /**
     * Create a new transaction for REMOVE operation (player to system).
     */
    public static Transaction createRemove(long playerId, long amount, String currencyType) {
        return new Transaction(0, playerId, null, amount, currencyType, TYPE_REMOVE, Instant.now());
    }

    /**
     * Create a new transaction for TRANSFER operation (player to player).
     */
    public static Transaction createTransfer(long fromPlayerId, long toPlayerId, long amount, String currencyType) {
        return new Transaction(0, fromPlayerId, toPlayerId, amount, currencyType, TYPE_TRANSFER, Instant.now());
    }

    /**
     * Create a new transaction for REWARD operation (system to player).
     */
    public static Transaction createReward(long playerId, long amount, String currencyType) {
        return new Transaction(0, null, playerId, amount, currencyType, TYPE_REWARD, Instant.now());
    }

    /**
     * Create a new transaction for PAYMENT operation (player to player via system).
     */
    public static Transaction createPayment(long fromPlayerId, long toPlayerId, long amount, String currencyType) {
        return new Transaction(0, fromPlayerId, toPlayerId, amount, currencyType, TYPE_PAYMENT, Instant.now());
    }

    /**
     * Create a new transaction for CONVERSION operation (item to wallet).
     */
    public static Transaction createConversion(long playerId, long amount, String currencyType) {
        return new Transaction(0, null, playerId, amount, currencyType, TYPE_CONVERSION, Instant.now());
    }
}
