package com.kiemhiep.core.service;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.event.TransactionEvent;
import com.kiemhiep.api.event.WalletUpdateEvent;
import com.kiemhiep.api.model.Transaction;
import com.kiemhiep.api.model.Wallet;
import com.kiemhiep.api.repository.TransactionRepository;
import com.kiemhiep.api.repository.WalletRepository;
import com.kiemhiep.api.service.EconomyService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for economy management with multi-currency support.
 * <p>
 * Currency is stored as signed numbers (long) in database.
 */
public class EconomyServiceImpl implements EconomyService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final EventDispatcher eventDispatcher;

    public EconomyServiceImpl(WalletRepository walletRepository, TransactionRepository transactionRepository, EventDispatcher eventDispatcher) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public long getBalance(long playerId, String currency) {
        Optional<Wallet> wallet = walletRepository.getByPlayerIdAndCurrency(playerId, currency);
        return wallet.map(Wallet::balance).orElse(0L);
    }

    @Override
    public List<Wallet> getWallets(long playerId) {
        return walletRepository.getByPlayerId(playerId);
    }

    @Override
    public void add(long playerId, String currency, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Wallet wallet = getOrCreateWallet(playerId, currency);
        long newBalance = wallet.balance() + amount;
        Wallet updated = new Wallet(wallet.id(), playerId, newBalance, currency, wallet.createdAt(), Instant.now());
        walletRepository.save(updated);

        // Create transaction record
        Transaction transaction = Transaction.createAdd(playerId, amount, currency);
        transactionRepository.save(transaction);

        // Fire events
        eventDispatcher.fire(new TransactionEvent(transaction.id(), null, playerId, amount, currency, Instant.now()));
        eventDispatcher.fire(new WalletUpdateEvent(playerId, currency, wallet.balance(), newBalance, WalletUpdateEvent.REASON_ADD));
    }

    @Override
    public void subtract(long playerId, String currency, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Wallet wallet = getOrCreateWallet(playerId, currency);
        if (wallet.balance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        long newBalance = wallet.balance() - amount;
        Wallet updated = new Wallet(wallet.id(), playerId, newBalance, currency, wallet.createdAt(), Instant.now());
        walletRepository.save(updated);

        // Create transaction record
        Transaction transaction = Transaction.createRemove(playerId, amount, currency);
        transactionRepository.save(transaction);

        // Fire events
        eventDispatcher.fire(new TransactionEvent(transaction.id(), playerId, null, amount, currency, Instant.now()));
        eventDispatcher.fire(new WalletUpdateEvent(playerId, currency, wallet.balance(), newBalance, WalletUpdateEvent.REASON_REMOVE));
    }

    @Override
    public boolean transfer(long fromPlayerId, long toPlayerId, String currency, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (fromPlayerId == toPlayerId) {
            return false;
        }

        Wallet fromWallet = walletRepository.getByPlayerIdAndCurrency(fromPlayerId, currency).orElse(null);
        if (fromWallet == null || fromWallet.balance() < amount) {
            return false;
        }

        Wallet toWallet = getOrCreateWallet(toPlayerId, currency);

        long fromNewBalance = fromWallet.balance() - amount;
        long toNewBalance = toWallet.balance() + amount;

        Wallet updatedFrom = new Wallet(fromWallet.id(), fromPlayerId, fromNewBalance, currency, fromWallet.createdAt(), Instant.now());
        Wallet updatedTo = new Wallet(toWallet.id(), toPlayerId, toNewBalance, currency, toWallet.createdAt(), Instant.now());

        // Save both wallets (atomic within single DB connection if supported)
        walletRepository.save(updatedFrom);
        walletRepository.save(updatedTo);

        // Create transaction records
        Transaction transaction = Transaction.createTransfer(fromPlayerId, toPlayerId, amount, currency);
        transactionRepository.save(transaction);

        // Fire events
        eventDispatcher.fire(new TransactionEvent(transaction.id(), fromPlayerId, toPlayerId, amount, currency, Instant.now()));
        eventDispatcher.fire(new WalletUpdateEvent(fromPlayerId, currency, fromWallet.balance(), fromNewBalance, WalletUpdateEvent.REASON_TRANSFER_OUT));
        eventDispatcher.fire(new WalletUpdateEvent(toPlayerId, currency, toWallet.balance(), toNewBalance, WalletUpdateEvent.REASON_TRANSFER_IN));

        return true;
    }

    @Override
    public List<String> getDefaultCurrencies() {
        return List.of("SPIRIT_STONE", "SILVER", "GOLD");
    }

    @Override
    public String getDefaultCurrency(long playerId) {
        return "SPIRIT_STONE";
    }

    // Helper to get or create wallet
    private Wallet getOrCreateWallet(long playerId, String currency) {
        return walletRepository.getByPlayerIdAndCurrency(playerId, currency)
            .orElseGet(() -> {
                Wallet newWallet = new Wallet(
                    0,
                    playerId,
                    0L,
                    currency,
                    Instant.now(),
                    Instant.now()
                );
                return walletRepository.save(newWallet);
            });
    }
}
