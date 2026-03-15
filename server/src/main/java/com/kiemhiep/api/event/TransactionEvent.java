package com.kiemhiep.api.event;

import com.kiemhiep.api.model.Transaction;

import java.time.Instant;

/**
 * Event fired when a transaction occurs.
 */
public record TransactionEvent(
    long transactionId,
    Long fromPlayerId,
    Long toPlayerId,
    long amount,
    String currency,
    Instant timestamp
) {

    public static TransactionEvent fromTransaction(Transaction t) {
        return new TransactionEvent(
            t.id(),
            t.fromPlayerId(),
            t.toPlayerId(),
            t.amount(),
            t.currencyType(),
            t.timestamp()
        );
    }
}
