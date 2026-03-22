package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;
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

    // --- Sect operations ---

    @Override
    public Optional<Sect> getById(long id) {
        String sql = "SELECT id, name, leader_id, level, exp, created_at, updated_at FROM kiemhiep_sects WHERE id = ?";
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
    public Optional<Sect> getByName(String name) {
        String sql = "SELECT id, name, leader_id, level, exp, created_at, updated_at FROM kiemhiep_sects WHERE name = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get sect by name: " + name, e);
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
        String sql = "INSERT INTO kiemhiep_sects (name, leader_id, level, exp, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sect.name());
            ps.setLong(2, sect.leaderId());
            ps.setInt(3, sect.level());
            ps.setLong(4, sect.exp());
            Instant now = Instant.now();
            ps.setTimestamp(5, Timestamp.from(now));
            ps.setTimestamp(6, Timestamp.from(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Sect(id, sect.name(), sect.leaderId(), sect.level(), sect.exp(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert sect", e);
        }
    }

    private Sect update(Sect sect) {
        String sql = "UPDATE kiemhiep_sects SET name = ?, leader_id = ?, level = ?, exp = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sect.name());
            ps.setLong(2, sect.leaderId());
            ps.setInt(3, sect.level());
            ps.setLong(4, sect.exp());
            ps.setTimestamp(5, Timestamp.from(Instant.now()));
            ps.setLong(6, sect.id());
            ps.executeUpdate();
            return new Sect(sect.id(), sect.name(), sect.leaderId(), sect.level(), sect.exp(), sect.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update sect: " + sect.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        // First delete members and relations
        deleteMembersBySect(id);
        deleteRelationsBySect(id);

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
        String sql = "SELECT id, name, leader_id, level, exp, created_at, updated_at FROM kiemhiep_sects";
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
            rs.getLong("leader_id"),
            rs.getInt("level"),
            rs.getLong("exp"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }

    // --- Member operations ---

    @Override
    public List<SectMember> getMembers(long sectId) {
        String sql = "SELECT id, sect_id, player_id, rank, contribution, joined_at, updated_at FROM kiemhiep_sect_members WHERE sect_id = ?";
        List<SectMember> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMemberRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get members for sect: " + sectId, e);
        }
        return list;
    }

    @Override
    public Optional<SectMember> getMember(long sectId, long playerId) {
        String sql = "SELECT id, sect_id, player_id, rank, contribution, joined_at, updated_at FROM kiemhiep_sect_members WHERE sect_id = ? AND player_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            ps.setLong(2, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapMemberRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get member: sect=" + sectId + ", player=" + playerId, e);
        }
    }

    @Override
    public List<SectMember> getMembersByPlayer(long playerId) {
        String sql = "SELECT id, sect_id, player_id, rank, contribution, joined_at, updated_at FROM kiemhiep_sect_members WHERE player_id = ?";
        List<SectMember> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMemberRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get members for player: " + playerId, e);
        }
        return list;
    }

    @Override
    public void deleteMember(long sectId, long playerId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_sect_members WHERE sect_id = ? AND player_id = ?")) {
            ps.setLong(1, sectId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete member: sect=" + sectId + ", player=" + playerId, e);
        }
    }

    @Override
    public List<SectMember> getMembersByRank(long sectId, SectMember.Rank rank) {
        String sql = "SELECT id, sect_id, player_id, rank, contribution, joined_at, updated_at FROM kiemhiep_sect_members WHERE sect_id = ? AND rank = ?";
        List<SectMember> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            ps.setString(2, rank.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMemberRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get members by rank for sect: " + sectId, e);
        }
        return list;
    }

    @Override
    public SectMember joinMember(long sectId, long playerId, SectMember.Rank rank) {
        String sql = "INSERT INTO kiemhiep_sect_members (sect_id, player_id, rank, contribution, joined_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sectId);
            ps.setLong(2, playerId);
            ps.setString(3, rank.name());
            ps.setInt(4, 0); // Starting contribution
            Instant now = Instant.now();
            ps.setTimestamp(5, Timestamp.from(now));
            ps.setTimestamp(6, Timestamp.from(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new SectMember(id, sectId, playerId, rank, 0, now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to join member to sect: sect=" + sectId + ", player=" + playerId, e);
        }
    }

    @Override
    public void updateMember(long sectId, long playerId, SectMember.Rank rank, int contribution) {
        String sql = "UPDATE kiemhiep_sect_members SET rank = ?, contribution = ?, updated_at = ? WHERE sect_id = ? AND player_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rank.name());
            ps.setInt(2, contribution);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, sectId);
            ps.setLong(5, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update member: sect=" + sectId + ", player=" + playerId, e);
        }
    }

    private void deleteMembersBySect(long sectId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_sect_members WHERE sect_id = ?")) {
            ps.setLong(1, sectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete members for sect: " + sectId, e);
        }
    }

    private static SectMember mapMemberRow(ResultSet rs) throws SQLException {
        return new SectMember(
            rs.getLong("id"),
            rs.getLong("sect_id"),
            rs.getLong("player_id"),
            SectMember.Rank.valueOf(rs.getString("rank")),
            rs.getInt("contribution"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("joined_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }

    // --- Relation operations ---

    @Override
    public List<SectRelation> getRelations(long sectId) {
        String sql = "SELECT id, sect_id, related_sect_id, relation_type, created_at FROM kiemhiep_sect_relations WHERE sect_id = ?";
        List<SectRelation> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRelationRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get relations for sect: " + sectId, e);
        }
        return list;
    }

    @Override
    public Optional<SectRelation> getRelation(long sectId, long relatedSectId) {
        String sql = "SELECT id, sect_id, related_sect_id, relation_type, created_at FROM kiemhiep_sect_relations WHERE sect_id = ? AND related_sect_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            ps.setLong(2, relatedSectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRelationRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get relation: sect=" + sectId + ", related=" + relatedSectId, e);
        }
    }

    @Override
    public void deleteRelation(long sectId, long relatedSectId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_sect_relations WHERE sect_id = ? AND related_sect_id = ?")) {
            ps.setLong(1, sectId);
            ps.setLong(2, relatedSectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete relation: sect=" + sectId + ", related=" + relatedSectId, e);
        }
    }

    @Override
    public List<SectRelation> getRelationsByType(long sectId, SectRelation.Type type) {
        String sql = "SELECT id, sect_id, related_sect_id, relation_type, created_at FROM kiemhiep_sect_relations WHERE sect_id = ? AND relation_type = ?";
        List<SectRelation> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sectId);
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRelationRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get relations by type for sect: " + sectId, e);
        }
        return list;
    }

    private void deleteRelationsBySect(long sectId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_sect_relations WHERE sect_id = ?")) {
            ps.setLong(1, sectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete relations for sect: " + sectId, e);
        }
    }

    private static SectRelation mapRelationRow(ResultSet rs) throws SQLException {
        return new SectRelation(
            rs.getLong("id"),
            rs.getLong("sect_id"),
            rs.getLong("related_sect_id"),
            SectRelation.Type.valueOf(rs.getString("relation_type")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at"))
        );
    }
}
