package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Repository for economy transactions.
 */
public interface TransactionRepository {

    Optional<Transaction> getById(long id);

    Transaction save(Transaction transaction);

    List<Transaction> findAllByPlayerId(long playerId);

    List<Transaction> findAllByFromPlayerId(long fromPlayerId);

    List<Transaction> findAllByToPlayerId(long toPlayerId);
}
