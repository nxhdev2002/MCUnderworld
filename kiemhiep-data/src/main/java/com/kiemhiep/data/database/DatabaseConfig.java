package com.kiemhiep.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * PostgreSQL database configuration and connection pool setup.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int maxPoolSize;
    private final int minIdle;
    private final long connectionTimeout;

    private HikariDataSource dataSource;

    public DatabaseConfig(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, 20, 5, 30000);
    }

    public DatabaseConfig(String host, int port, String database, String username, String password,
                          int maxPoolSize, int minIdle, long connectionTimeout) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Initialize the database connection pool.
     */
    public void initialize() {
        if (dataSource != null) {
            logger.warn("DataSource already initialized");
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
        config.setUsername(username);
        config.setPassword(password);

        // Connection pool settings
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);

        // PostgreSQL-specific settings
        Properties props = new Properties();
        props.setProperty("cachePrepStmts", "true");
        props.setProperty("prepStmtCacheSize", "250");
        props.setProperty("prepStmtCacheSqlLimit", "2048");
        props.setProperty("useServerPrepStmts", "true");
        props.setProperty("reWriteBatchedInserts", "true");

        config.setDataSourceProperties(props);

        // Pool name for monitoring
        config.setPoolName("KiemHiepPool");

        // Enable leak detection (for debugging)
        config.setLeakDetectionThreshold(60000); // 1 minute

        dataSource = new HikariDataSource(config);

        logger.info("Database connection pool initialized successfully");
        logger.info("JDBC URL: jdbc:postgresql://{}:{}/{}", host, port, database);
        logger.info("Max pool size: {}, Min idle: {}, Connection timeout: {}ms",
                    maxPoolSize, minIdle, connectionTimeout);
    }

    /**
     * Get the data source.
     */
    public DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not initialized. Call initialize() first.");
        }
        return dataSource;
    }

    /**
     * Close the connection pool.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    /**
     * Check if the data source is initialized.
     */
    public boolean isInitialized() {
        return dataSource != null && !dataSource.isClosed();
    }
}
