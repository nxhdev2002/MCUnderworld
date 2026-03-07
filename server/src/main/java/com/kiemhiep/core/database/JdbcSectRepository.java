package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.repository.SectRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSectRepository implements SectRepository {

    private final DataSource dataSource;

    public JdbcSectRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Sect> getById(long id) {
        String sql = "SELECT id, name, created_at, updated_at FROM kiemhiep_sects WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sect by id: " + id, e);
        }
    }

    @Override
    public Sect save(Sect sect) {
        if (sect.id() > 0) {
            return update(sect);
        }
        return insert(sect);
    }

    private Sect insert(Sect sect) {
        String sql = "INSERT INTO kiemhiep_sects (name, created_at, updated_at) VALUES (?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sect.name());
            Instant now = Instant.now();
            ps.setTimestamp(2, Timestamp.from(now));
            ps.setTimestamp(3, Timestamp.from(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Sect(id, sect.name(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert sect", e);
        }
    }

    private Sect update(Sect sect) {
        String sql = "UPDATE kiemhiep_sects SET name = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sect.name());
            ps.setTimestamp(2, Timestamp.from(Instant.now()));
            ps.setLong(3, sect.id());
            ps.executeUpdate();
            return new Sect(sect.id(), sect.name(), sect.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update sect: " + sect.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_sects WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete sect: " + id, e);
        }
    }

    /** For admin or small datasets only; loads full table into memory. */
    @Override
    public List<Sect> findAll() {
        String sql = "SELECT id, name, created_at, updated_at FROM kiemhiep_sects";
        List<Sect> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all sects", e);
        }
        return list;
    }

    private static Sect mapRow(ResultSet rs) throws SQLException {
        return new Sect(
            rs.getLong("id"),
            rs.getString("name"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }
}
