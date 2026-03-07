package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.repository.SkillRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSkillRepository implements SkillRepository {

    private final DataSource dataSource;

    public JdbcSkillRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Skill> getById(long id) {
        String sql = "SELECT id, player_id, skill_id, level, created_at, updated_at FROM kiemhiep_skills WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skill by id: " + id, e);
        }
    }

    @Override
    public List<Skill> getByPlayerId(long playerId) {
        String sql = "SELECT id, player_id, skill_id, level, created_at, updated_at FROM kiemhiep_skills WHERE player_id = ?";
        List<Skill> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skills by playerId: " + playerId, e);
        }
        return list;
    }

    @Override
    public Optional<Skill> getByPlayerIdAndSkillId(long playerId, String skillId) {
        String sql = "SELECT id, player_id, skill_id, level, created_at, updated_at FROM kiemhiep_skills WHERE player_id = ? AND skill_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            ps.setString(2, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skill by playerId and skillId", e);
        }
    }

    @Override
    public Skill save(Skill skill) {
        if (skill.id() > 0) {
            return update(skill);
        }
        return insert(skill);
    }

    private Skill insert(Skill skill) {
        String sql = "INSERT INTO kiemhiep_skills (player_id, skill_id, level, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, skill.playerId());
            ps.setString(2, skill.skillId());
            ps.setInt(3, skill.level());
            Instant now = Instant.now();
            ps.setObject(4, now);
            ps.setObject(5, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Skill(id, skill.playerId(), skill.skillId(), skill.level(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert skill", e);
        }
    }

    private Skill update(Skill skill) {
        String sql = "UPDATE kiemhiep_skills SET level = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, skill.level());
            ps.setObject(2, Instant.now());
            ps.setLong(3, skill.id());
            ps.executeUpdate();
            return new Skill(skill.id(), skill.playerId(), skill.skillId(), skill.level(),
                skill.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update skill: " + skill.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_skills WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete skill: " + id, e);
        }
    }

    private static Skill mapRow(ResultSet rs) throws SQLException {
        return new Skill(
            rs.getLong("id"),
            rs.getLong("player_id"),
            rs.getString("skill_id"),
            rs.getInt("level"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }
}
