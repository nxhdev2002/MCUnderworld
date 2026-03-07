package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Wallet;
import com.kiemhiep.api.repository.WalletRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWalletRepository implements WalletRepository {

    private final DataSource dataSource;

    public JdbcWalletRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Wallet> getById(long id) {
        String sql = "SELECT id, player_id, balance, currency, created_at, updated_at FROM kiemhiep_wallets WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get wallet by id: " + id, e);
        }
    }

    @Override
    public List<Wallet> getByPlayerId(long playerId) {
        String sql = "SELECT id, player_id, balance, currency, created_at, updated_at FROM kiemhiep_wallets WHERE player_id = ?";
        List<Wallet> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get wallets by playerId: " + playerId, e);
        }
        return list;
    }

    @Override
    public Optional<Wallet> getByPlayerIdAndCurrency(long playerId, String currency) {
        String sql = "SELECT id, player_id, balance, currency, created_at, updated_at FROM kiemhiep_wallets WHERE player_id = ? AND currency = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            ps.setString(2, currency);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get wallet by playerId and currency", e);
        }
    }

    @Override
    public Wallet save(Wallet wallet) {
        if (wallet.id() > 0) {
            return update(wallet);
        }
        return insert(wallet);
    }

    private Wallet insert(Wallet wallet) {
        String sql = "INSERT INTO kiemhiep_wallets (player_id, balance, currency, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, wallet.playerId());
            ps.setLong(2, wallet.balance());
            ps.setString(3, wallet.currency());
            Instant now = Instant.now();
            ps.setObject(4, now);
            ps.setObject(5, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Wallet(id, wallet.playerId(), wallet.balance(), wallet.currency(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert wallet", e);
        }
    }

    private Wallet update(Wallet wallet) {
        String sql = "UPDATE kiemhiep_wallets SET balance = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, wallet.balance());
            ps.setObject(2, Instant.now());
            ps.setLong(3, wallet.id());
            ps.executeUpdate();
            return new Wallet(wallet.id(), wallet.playerId(), wallet.balance(), wallet.currency(),
                wallet.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update wallet: " + wallet.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_wallets WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete wallet: " + id, e);
        }
    }

    private static Wallet mapRow(ResultSet rs) throws SQLException {
        return new Wallet(
            rs.getLong("id"),
            rs.getLong("player_id"),
            rs.getLong("balance"),
            rs.getString("currency"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }
}
