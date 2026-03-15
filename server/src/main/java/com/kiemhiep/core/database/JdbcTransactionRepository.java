package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Transaction;
import com.kiemhiep.api.repository.TransactionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTransactionRepository implements TransactionRepository {

    private final DataSource dataSource;

    public JdbcTransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Transaction> getById(long id) {
        String sql = "SELECT id, from_player_id, to_player_id, amount, currency_type, type, created_at FROM kiemhiep_transactions WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transaction by id: " + id, e);
        }
    }

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.id() > 0) {
            return update(transaction);
        }
        return insert(transaction);
    }

    private Transaction insert(Transaction t) {
        String sql = "INSERT INTO kiemhiep_transactions (from_player_id, to_player_id, amount, currency_type, type, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setOptionalLong(ps, 1, t.fromPlayerId());
            setOptionalLong(ps, 2, t.toPlayerId());
            ps.setLong(3, t.amount());
            ps.setString(4, t.currencyType());
            ps.setString(5, t.type());
            ps.setTimestamp(6, Timestamp.from(Instant.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Transaction(id, t.fromPlayerId(), t.toPlayerId(), t.amount(), t.currencyType(), t.type(), Instant.now());
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert transaction", e);
        }
    }

    private Transaction update(Transaction t) {
        String sql = "UPDATE kiemhiep_transactions SET from_player_id = ?, to_player_id = ?, amount = ?, currency_type = ?, type = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            setOptionalLong(ps, 1, t.fromPlayerId());
            setOptionalLong(ps, 2, t.toPlayerId());
            ps.setLong(3, t.amount());
            ps.setString(4, t.currencyType());
            ps.setString(5, t.type());
            ps.setLong(6, t.id());
            ps.executeUpdate();
            return new Transaction(t.id(), t.fromPlayerId(), t.toPlayerId(), t.amount(), t.currencyType(), t.type(), t.timestamp());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update transaction: " + t.id(), e);
        }
    }

    @Override
    public List<Transaction> findAllByPlayerId(long playerId) {
        String sql = "SELECT id, from_player_id, to_player_id, amount, currency_type, type, created_at FROM kiemhiep_transactions WHERE from_player_id = ? OR to_player_id = ? ORDER BY created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            ps.setLong(2, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transactions by playerId: " + playerId, e);
        }
        return list;
    }

    @Override
    public List<Transaction> findAllByFromPlayerId(long fromPlayerId) {
        String sql = "SELECT id, from_player_id, to_player_id, amount, currency_type, type, created_at FROM kiemhiep_transactions WHERE from_player_id = ? ORDER BY created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, fromPlayerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transactions by fromPlayerId: " + fromPlayerId, e);
        }
        return list;
    }

    @Override
    public List<Transaction> findAllByToPlayerId(long toPlayerId) {
        String sql = "SELECT id, from_player_id, to_player_id, amount, currency_type, type, created_at FROM kiemhiep_transactions WHERE to_player_id = ? ORDER BY created_at DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, toPlayerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transactions by toPlayerId: " + toPlayerId, e);
        }
        return list;
    }

    private void setOptionalLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value != null) {
            ps.setLong(index, value);
        } else {
            ps.setNull(index, Types.BIGINT);
        }
    }

    private static Transaction mapRow(ResultSet rs) throws SQLException {
        Long fromPlayerId = rs.getLong("from_player_id");
        Long toPlayerId = rs.getLong("to_player_id");
        return new Transaction(
            rs.getLong("id"),
            rs.wasNull() ? null : fromPlayerId,
            rs.wasNull() ? null : toPlayerId,
            rs.getLong("amount"),
            rs.getString("currency_type"),
            rs.getString("type"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at"))
        );
    }
}
