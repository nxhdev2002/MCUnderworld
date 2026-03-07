package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class JdbcPlayerRepository implements PlayerRepository {

    private final DataSource dataSource;

    public JdbcPlayerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Player> getById(long id) {
        String sql = "SELECT id, uuid, name, created_at, updated_at FROM kiemhiep_players WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get player by id: " + id, e);
        }
    }

    @Override
    public Optional<Player> getByUuid(String uuid) {
        String sql = "SELECT id, uuid, name, created_at, updated_at FROM kiemhiep_players WHERE uuid = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get player by uuid: " + uuid, e);
        }
    }

    @Override
    public Player save(Player player) {
        if (player.id() > 0) {
            return update(player);
        }
        return insert(player);
    }

    private Player insert(Player player) {
        String sql = "INSERT INTO kiemhiep_players (uuid, name, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.uuid());
            ps.setString(2, player.name());
            Instant now = Instant.now();
            ps.setObject(3, now);
            ps.setObject(4, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Player(id, player.uuid(), player.name(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert player", e);
        }
    }

    private Player update(Player player) {
        String sql = "UPDATE kiemhiep_players SET name = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, player.name());
            ps.setObject(2, Instant.now());
            ps.setLong(3, player.id());
            ps.executeUpdate();
            return new Player(player.id(), player.uuid(), player.name(), player.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update player: " + player.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_players WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete player: " + id, e);
        }
    }

    private static Player mapRow(ResultSet rs) throws SQLException {
        return new Player(
            rs.getLong("id"),
            rs.getString("uuid"),
            rs.getString("name"),
            timestampToInstant(rs.getTimestamp("created_at")),
            timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }

    static Instant timestampToInstant(Timestamp ts) {
        return ts != null ? ts.toInstant() : Instant.EPOCH;
    }
}
