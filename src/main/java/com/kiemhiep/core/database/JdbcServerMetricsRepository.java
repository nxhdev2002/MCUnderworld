package com.kiemhiep.core.database;

import com.kiemhiep.api.model.ServerMetrics;
import com.kiemhiep.api.repository.ServerMetricsRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

public class JdbcServerMetricsRepository implements ServerMetricsRepository {

    private final DataSource dataSource;

    public JdbcServerMetricsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ServerMetrics save(ServerMetrics metrics) {
        String sql = "INSERT INTO kiemhiep_server_metrics (server_id, ts, tps, tick_time_ms) VALUES (?, ?, ?, ?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, metrics.serverId());
            ps.setObject(2, metrics.ts());
            ps.setDouble(3, metrics.tps());
            ps.setLong(4, metrics.tickTimeMs());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return new ServerMetrics(id, metrics.serverId(), metrics.ts(), metrics.tps(), metrics.tickTimeMs());
                }
            }
            return metrics;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save server metrics", e);
        }
    }
}
