package com.kiemhiep.data.repository.impl;

import com.kiemhiep.data.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * SQL implementation of WalletRepository.
 */
public class SqlWalletRepository implements WalletRepository {
    private static final Logger logger = LoggerFactory.getLogger(SqlWalletRepository.class);

    private final DataSource dataSource;
    private final ExecutorService executor;
    private static final String[] CURRENCY_TYPES = {"SPIRIT_STONE", "SILVER", "GOLD"};

    public SqlWalletRepository(DataSource dataSource, ExecutorService executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Integer> getBalance(UUID playerId, String currencyType) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT balance FROM wallets WHERE player_id = ? AND currency_type = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.setString(2, currencyType);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("balance");
                }
                return 0;

            } catch (Exception e) {
                logger.error("Failed to get balance for player {}", playerId, e);
                throw new RuntimeException("Failed to get balance", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getAllBalances(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Integer> balances = new HashMap<>();
            String sql = "SELECT currency_type, balance FROM wallets WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String currencyType = rs.getString("currency_type");
                    int balance = rs.getInt("balance");
                    balances.put(currencyType, balance);
                }

                // Ensure all currency types have a value
                for (String currency : CURRENCY_TYPES) {
                    balances.putIfAbsent(currency, 0);
                }

                return balances;

            } catch (Exception e) {
                logger.error("Failed to get all balances for player {}", playerId, e);
                throw new RuntimeException("Failed to get all balances", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> setBalance(UUID playerId, String currencyType, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO wallets (player_id, currency_type, balance) " +
                         "VALUES (?, ?, ?) " +
                         "ON CONFLICT (player_id, currency_type) DO UPDATE SET balance = EXCLUDED.balance";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.setString(2, currencyType);
                stmt.setInt(3, amount);
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to set balance for player {}", playerId, e);
                throw new RuntimeException("Failed to set balance", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> addBalance(UUID playerId, String currencyType, int amount) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO wallets (player_id, currency_type, balance) " +
                         "VALUES (?, ?, ?) " +
                         "ON CONFLICT (player_id, currency_type) DO UPDATE SET balance = wallets.balance + EXCLUDED.balance";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.setString(2, currencyType);
                stmt.setInt(3, amount);
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to add balance for player {}", playerId, e);
                throw new RuntimeException("Failed to add balance", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> removeBalance(UUID playerId, String currencyType, int amount) {
        return CompletableFuture.supplyAsync(() -> {
            // First check current balance
            String checkSql = "SELECT balance FROM wallets WHERE player_id = ? AND currency_type = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(checkSql)) {

                stmt.setString(1, playerId.toString());
                stmt.setString(2, currencyType);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int currentBalance = rs.getInt("balance");
                    if (currentBalance >= amount) {
                        // Sufficient funds, remove the amount
                        String updateSql = "UPDATE wallets SET balance = balance - ? WHERE player_id = ? AND currency_type = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, amount);
                            updateStmt.setString(2, playerId.toString());
                            updateStmt.setString(3, currencyType);
                            updateStmt.executeUpdate();
                        }
                        return true;
                    }
                }
                return false;

            } catch (Exception e) {
                logger.error("Failed to remove balance for player {}", playerId, e);
                throw new RuntimeException("Failed to remove balance", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> initializeWallet(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO wallets (player_id, currency_type, balance) VALUES (?, ?, 0) " +
                         "ON CONFLICT (player_id, currency_type) DO NOTHING";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                for (String currency : CURRENCY_TYPES) {
                    stmt.setString(2, currency);
                    stmt.addBatch();
                }
                stmt.executeBatch();

            } catch (Exception e) {
                logger.error("Failed to initialize wallet for player {}", playerId, e);
                throw new RuntimeException("Failed to initialize wallet", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteWallet(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM wallets WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to delete wallet for player {}", playerId, e);
                throw new RuntimeException("Failed to delete wallet", e);
            }
        }, executor);
    }
}
