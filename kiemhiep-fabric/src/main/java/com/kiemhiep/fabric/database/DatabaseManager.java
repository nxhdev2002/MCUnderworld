package com.kiemhiep.fabric.database;

import com.kiemhiep.data.database.DatabaseConfig;
import com.kiemhiep.data.database.DatabaseMigrator;
import com.kiemhiep.data.redis.RedisConfig;
import com.kiemhiep.fabric.KiemHiepMod;
import com.kiemhiep.fabric.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database manager for KiemHiep mod.
 * Handles PostgreSQL and Redis connections.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private final ModConfig config;
    private DatabaseConfig databaseConfig;
    private RedisConfig redisConfig;
    private DatabaseMigrator migrator;

    public DatabaseManager(ModConfig config) {
        this.config = config;
    }

    /**
     * Initialize database connections.
     */
    public void initialize() {
        logger.info("Initializing database connections...");

        // Initialize PostgreSQL
        databaseConfig = new DatabaseConfig(
                config.getDbHost(),
                config.getDbPort(),
                config.getDbDatabase(),
                config.getDbUsername(),
                config.getDbPassword(),
                config.getDbMaxPoolSize(),
                config.getDbMinIdle(),
                30000
        );
        databaseConfig.initialize();

        // Initialize Flyway migrations
        migrator = new DatabaseMigrator(
                databaseConfig.getDataSource(),
                "filesystem:./config/kiemhiep/migrations"
        );

        // Run migrations
        if (!migrator.migrate()) {
            logger.error("Database migration failed!");
            // Continue anyway - migrations may have already been applied
        }

        // Initialize Redis
        redisConfig = new RedisConfig(
                config.getRedisHost(),
                config.getRedisPort(),
                config.getRedisPassword(),
                config.getRedisDatabase(),
                5000
        );

        try {
            redisConfig.initialize();
            logger.info("Redis connection initialized");
        } catch (Exception e) {
            logger.warn("Redis connection failed, continuing without Redis cache", e);
        }

        logger.info("Database connections initialized successfully");
    }

    /**
     * Get the PostgreSQL data source.
     */
    public DataSource getDataSource() {
        if (databaseConfig == null) {
            throw new IllegalStateException("Database not initialized");
        }
        return databaseConfig.getDataSource();
    }

    /**
     * Get a database connection.
     */
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Get the Redis configuration.
     */
    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    /**
     * Check if Redis is available.
     */
    public boolean isRedisAvailable() {
        return redisConfig != null && redisConfig.isConnected();
    }

    /**
     * Shutdown database connections.
     */
    public void shutdown() {
        logger.info("Shutting down database connections...");

        if (databaseConfig != null) {
            databaseConfig.shutdown();
        }

        if (redisConfig != null) {
            redisConfig.shutdown();
        }

        logger.info("Database connections shut down");
    }
}
