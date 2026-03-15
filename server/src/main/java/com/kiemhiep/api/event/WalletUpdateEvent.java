package com.kiemhiep.api.event;

/**
 * Event fired when a wallet balance changes.
 */
public record WalletUpdateEvent(
    long playerId,
    String currency,
    long oldBalance,
    long newBalance,
    String reason
) {

    // Reason types
    public static final String REASON_ADD = "ADD";
    public static final String REASON_REMOVE = "REMOVE";
    public static final String REASON_TRANSFER_OUT = "TRANSFER_OUT";
    public static final String REASON_TRANSFER_IN = "TRANSFER_IN";
    public static final String REASON_REWARD = "REWARD";
    public static final String REASON_CONVERSION = "CONVERSION";
}
