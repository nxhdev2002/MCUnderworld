package com.kiemhiep.core.database;

import com.kiemhiep.api.model.Quest;
import com.kiemhiep.api.repository.QuestRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcQuestRepository implements QuestRepository {

    private final DataSource dataSource;

    public JdbcQuestRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Quest> getById(long id) {
        String sql = "SELECT id, player_id, quest_id, progress, created_at, updated_at FROM kiemhiep_quests WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get quest by id: " + id, e);
        }
    }

    @Override
    public List<Quest> getByPlayerId(long playerId) {
        String sql = "SELECT id, player_id, quest_id, progress, created_at, updated_at FROM kiemhiep_quests WHERE player_id = ?";
        List<Quest> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get quests by playerId: " + playerId, e);
        }
        return list;
    }

    @Override
    public Optional<Quest> getByPlayerIdAndQuestId(long playerId, String questId) {
        String sql = "SELECT id, player_id, quest_id, progress, created_at, updated_at FROM kiemhiep_quests WHERE player_id = ? AND quest_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, playerId);
            ps.setString(2, questId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get quest by playerId and questId", e);
        }
    }

    @Override
    public Quest save(Quest quest) {
        if (quest.id() > 0) {
            return update(quest);
        }
        return insert(quest);
    }

    private Quest insert(Quest quest) {
        String sql = "INSERT INTO kiemhiep_quests (player_id, quest_id, progress, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, quest.playerId());
            ps.setString(2, quest.questId());
            ps.setInt(3, quest.progress());
            Instant now = Instant.now();
            ps.setObject(4, now);
            ps.setObject(5, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new Quest(id, quest.playerId(), quest.questId(), quest.progress(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert quest", e);
        }
    }

    private Quest update(Quest quest) {
        String sql = "UPDATE kiemhiep_quests SET progress = ?, updated_at = ? WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quest.progress());
            ps.setObject(2, Instant.now());
            ps.setLong(3, quest.id());
            ps.executeUpdate();
            return new Quest(quest.id(), quest.playerId(), quest.questId(), quest.progress(),
                quest.createdAt(), Instant.now());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update quest: " + quest.id(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_quests WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete quest: " + id, e);
        }
    }

    private static Quest mapRow(ResultSet rs) throws SQLException {
        return new Quest(
            rs.getLong("id"),
            rs.getLong("player_id"),
            rs.getString("quest_id"),
            rs.getInt("progress"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }
}
