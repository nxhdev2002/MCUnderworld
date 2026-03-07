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
}
