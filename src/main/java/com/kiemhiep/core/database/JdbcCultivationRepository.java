package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.repository.CultivationRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCultivationRepository implements CultivationRepository {

    private final DataSource dataSource;

    public JdbcCultivationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Cultivation> getById(long id) {
        String sql = "SELECT id, player_id, level, exp, created_at, updated_at FROM kiemhiep_cultivation WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get cultivation by id: " + id, e);
        }
    }

    @Override
    public Optional<Cultivation> getByPlayerId(long playerId) {
        String sql = "SELECT id, player_id, level, exp, created_at, updated_at FROM kiemhiep_cultivation WHERE player_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get cultivation by playerId: " + playerId, e);
        }
    }

    @Override
    public Cultivation save(Cultivation cultivation) {
        if (cultivation.id() > 0) {
            return update(cultivation);
        }
        return insert(cultivation);
    }

    private Cultivation insert(Cultivation cultivation) {
        String sql = "INSERT INTO kiemhiep_cultivation (player_id, level, exp, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cultivation.playerId());
            ps.setInt(2, cultivation.level());
            ps.setLong(3, cultivation.exp());
            Instant now = Instant.now();
            ps.setObject(4, now);
            ps.setObject(5, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Cultivation(id, cultivation.playerId(), cultivation.level(), cultivation.exp(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert cultivation", e);
        }
    }

    private Cultivation update(Cultivation cultivation) {
        String sql = "UPDATE kiemhiep_cultivation SET level = ?, exp = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cultivation.level());
            ps.setLong(2, cultivation.exp());
            ps.setObject(3, Instant.now());
            ps.setLong(4, cultivation.id());
            ps.executeUpdate();
            return new Cultivation(cultivation.id(), cultivation.playerId(), cultivation.level(), cultivation.exp(),
                cultivation.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update cultivation: " + cultivation.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_cultivation WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete cultivation: " + id, e);
        }
    }

    /** For admin or small datasets only; loads full table into memory. */
    @Override
    public List<Cultivation> findAll() {
        String sql = "SELECT id, player_id, level, exp, created_at, updated_at FROM kiemhiep_cultivation";
        List<Cultivation> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all cultivation", e);
        }
        return list;
    }

    private static Cultivation mapRow(ResultSet rs) throws SQLException {
        return new Cultivation(
            rs.getLong("id"),
            rs.getLong("player_id"),
            rs.getInt("level"),
            rs.getLong("exp"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }
}
