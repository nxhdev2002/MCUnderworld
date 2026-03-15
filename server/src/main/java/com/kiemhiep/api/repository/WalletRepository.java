package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {

    Optional<Wallet> getById(long id);

    List<Wallet> getByPlayerId(long playerId);

    Optional<Wallet> getByPlayerIdAndCurrency(long playerId, String currency);

    Wallet save(Wallet wallet);

    void deleteById(long id);

    /**
     * Atomically transfer amount from one wallet to another.
     * Both updates happen in a single JDBC transaction.
     *
     * @param fromId      sender wallet ID
     * @param toId        receiver wallet ID
     * @param amount      amount to transfer
     * @param currency    currency type
     * @return true if transfer succeeded, false if insufficient balance
     */
    boolean transferAtomic(long fromId, long toId, long amount, String currency);
}
