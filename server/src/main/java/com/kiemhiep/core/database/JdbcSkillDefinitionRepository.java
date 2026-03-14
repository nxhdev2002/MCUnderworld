package com.kiemhiep.core.database;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.SkillDefinitionRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSkillDefinitionRepository implements SkillDefinitionRepository {

    private final DataSource dataSource;

    public JdbcSkillDefinitionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<SkillDefinition> getById(long id) {
        String sql = "SELECT id, skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, " +
            "is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, " +
            "evolution_level, level, created_at, updated_at FROM kiemhiep_skill_definitions WHERE id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skill definition by id: " + id, e);
        }
    }

    @Override
    public Optional<SkillDefinition> getBySkillId(String skillId) {
        String sql = "SELECT id, skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, " +
            "is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, " +
            "evolution_level, level, created_at, updated_at FROM kiemhiep_skill_definitions WHERE skill_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, skillId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skill definition by skillId: " + skillId, e);
        }
    }

    @Override
    public Optional<SkillDefinition> getByItemId(String itemId) {
        String sql = "SELECT id, skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, " +
            "is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, " +
            "evolution_level, level, created_at, updated_at FROM kiemhiep_skill_definitions WHERE item_id = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get skill definition by itemId: " + itemId, e);
        }
    }

    @Override
    public List<SkillDefinition> findAll() {
        String sql = "SELECT id, skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, " +
            "is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, " +
            "evolution_level, level, created_at, updated_at FROM kiemhiep_skill_definitions ORDER BY id";
        List<SkillDefinition> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all skill definitions", e);
        }
        return list;
    }

    @Override
    public SkillDefinition save(SkillDefinition definition) {
        if (definition.id() > 0) return update(definition);
        return insert(definition);
    }

    private SkillDefinition insert(SkillDefinition d) {
        String sql = "INSERT INTO kiemhiep_skill_definitions (skill_id, behavior_id, item_id, name, mana_cost, " +
            "cooldown_ticks, max_radius, is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, " +
            "parent_skill_id, evolution_level, level, created_at, updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Instant now = Instant.now();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.skillId());
            ps.setString(2, d.behaviorId());
            ps.setString(3, d.itemId());
            ps.setString(4, d.name());
            ps.setInt(5, d.manaCost());
            ps.setInt(6, d.cooldownTicks());
            ps.setDouble(7, d.maxRadius());
            ps.setBoolean(8, d.isAoe());
            ps.setBoolean(9, d.isMelee());
            ps.setString(10, d.skillType());
            ps.setInt(11, d.castTimeTicks());
            ps.setBoolean(12, d.castCancellable());
            ps.setBoolean(13, d.consumable());
            ps.setString(14, d.parentSkillId());
            ps.setInt(15, d.evolutionLevel());
            ps.setInt(16, d.level());
            ps.setObject(17, now);
            ps.setObject(18, now);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new SkillDefinition(id, d.skillId(), d.behaviorId(), d.itemId(), d.name(), d.manaCost(),
                        d.cooldownTicks(), d.maxRadius(), d.isAoe(), d.isMelee(), d.skillType(), d.castTimeTicks(),
                        d.castCancellable(), d.consumable(), d.elementalType(), d.parentSkillId(), d.evolutionLevel(), d.level(), now, now);
                }
            }
            throw new RuntimeException("Insert failed, no generated key");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert skill definition", e);
        }
    }

    private SkillDefinition update(SkillDefinition d) {
        String sql = "UPDATE kiemhiep_skill_definitions SET skill_id=?, behavior_id=?, item_id=?, name=?, mana_cost=?, " +
            "cooldown_ticks=?, max_radius=?, is_aoe=?, is_melee=?, skill_type=?, cast_time_ticks=?, cast_cancellable=?, " +
            "consumable=?, elemental_type=?, parent_skill_id=?, evolution_level=?, level=?, updated_at=? WHERE id=?";
        Instant now = Instant.now();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.skillId());
            ps.setString(2, d.behaviorId());
            ps.setString(3, d.itemId());
            ps.setString(4, d.name());
            ps.setInt(5, d.manaCost());
            ps.setInt(6, d.cooldownTicks());
            ps.setDouble(7, d.maxRadius());
            ps.setBoolean(8, d.isAoe());
            ps.setBoolean(9, d.isMelee());
            ps.setString(10, d.skillType());
            ps.setInt(11, d.castTimeTicks());
            ps.setBoolean(12, d.castCancellable());
            ps.setBoolean(13, d.consumable());
            ps.setString(14, d.elementalType());
            ps.setString(15, d.parentSkillId());
            ps.setInt(16, d.evolutionLevel());
            ps.setInt(17, d.level());
            ps.setObject(18, now);
            ps.setLong(19, d.id());
            ps.executeUpdate();
            return new SkillDefinition(d.id(), d.skillId(), d.behaviorId(), d.itemId(), d.name(), d.manaCost(),
                d.cooldownTicks(), d.maxRadius(), d.isAoe(), d.isMelee(), d.skillType(), d.castTimeTicks(),
                d.castCancellable(), d.consumable(), d.elementalType(), d.parentSkillId(), d.evolutionLevel(), d.level(), d.createdAt(), now);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update skill definition: " + d.id(), e);
        }
    }

    private static SkillDefinition mapRow(ResultSet rs) throws SQLException {
        return new SkillDefinition(
            rs.getLong("id"),
            rs.getString("skill_id"),
            rs.getString("behavior_id"),
            rs.getString("item_id"),
            rs.getString("name"),
            rs.getInt("mana_cost"),
            rs.getInt("cooldown_ticks"),
            rs.getDouble("max_radius"),
            rs.getBoolean("is_aoe"),
            rs.getBoolean("is_melee"),
            rs.getString("skill_type"),
            rs.getInt("cast_time_ticks"),
            rs.getBoolean("cast_cancellable"),
            rs.getBoolean("consumable"),
            rs.getString("elemental_type"),
            rs.getString("parent_skill_id"),
            rs.getInt("evolution_level"),
            rs.getInt("level"),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("created_at")),
            JdbcPlayerRepository.timestampToInstant(rs.getTimestamp("updated_at"))
        );
    }

    @Override
    public void deleteById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM kiemhiep_skill_definitions WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete skill definition: " + id, e);
        }
    }
}
