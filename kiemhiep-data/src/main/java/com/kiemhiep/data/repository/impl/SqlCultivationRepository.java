package com.kiemhiep.data.repository.impl;

import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.data.repository.CultivationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * SQL implementation of CultivationRepository.
 */
public class SqlCultivationRepository implements CultivationRepository {
    private static final Logger logger = LoggerFactory.getLogger(SqlCultivationRepository.class);

    private final DataSource dataSource;
    private final ExecutorService executor;

    public SqlCultivationRepository(DataSource dataSource, ExecutorService executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Cultivation> getCultivation(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT realm_level, sub_level, exp FROM cultivation WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int realmLevel = rs.getInt("realm_level");
                    int subLevel = rs.getInt("sub_level");
                    int exp = rs.getInt("exp");
                    return new Cultivation(playerId, realmLevel, subLevel, exp);
                } else {
                    // Return default cultivation (Mortal)
                    return new Cultivation(playerId, 0, 0, 0);
                }

            } catch (Exception e) {
                logger.error("Failed to get cultivation for player {}", playerId, e);
                throw new RuntimeException("Failed to get cultivation", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> saveCultivation(Cultivation cultivation) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO cultivation (player_id, realm_level, sub_level, exp) " +
                         "VALUES (?, ?, ?, ?) " +
                         "ON CONFLICT (player_id) DO UPDATE SET " +
                         "realm_level = EXCLUDED.realm_level, " +
                         "sub_level = EXCLUDED.sub_level, " +
                         "exp = EXCLUDED.exp";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, cultivation.getPlayerId().toString());
                stmt.setInt(2, cultivation.getRealmLevel());
                stmt.setInt(3, cultivation.getSubLevel());
                stmt.setInt(4, cultivation.getExp());

                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to save cultivation for player {}", cultivation.getPlayerId(), e);
                throw new RuntimeException("Failed to save cultivation", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateRealm(UUID playerId, int realmLevel) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE cultivation SET realm_level = ? WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, realmLevel);
                stmt.setString(2, playerId.toString());
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to update realm for player {}", playerId, e);
                throw new RuntimeException("Failed to update realm", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateSubLevel(UUID playerId, int subLevel) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE cultivation SET sub_level = ? WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, subLevel);
                stmt.setString(2, playerId.toString());
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to update sub-level for player {}", playerId, e);
                throw new RuntimeException("Failed to update sub-level", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> updateExp(UUID playerId, int exp) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE cultivation SET exp = ? WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, exp);
                stmt.setString(2, playerId.toString());
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to update exp for player {}", playerId, e);
                throw new RuntimeException("Failed to update exp", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> deleteCultivation(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM cultivation WHERE player_id = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                stmt.executeUpdate();

            } catch (Exception e) {
                logger.error("Failed to delete cultivation for player {}", playerId, e);
                throw new RuntimeException("Failed to delete cultivation", e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> hasCultivation(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT 1 FROM cultivation WHERE player_id = ? LIMIT 1";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                return rs.next();

            } catch (Exception e) {
                logger.error("Failed to check cultivation for player {}", playerId, e);
                throw new RuntimeException("Failed to check cultivation", e);
            }
        }, executor);
    }
}
