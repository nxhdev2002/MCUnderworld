package com.kiemhiep.data.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database migrator using Flyway.
 * Handles schema migrations and versioning.
 */
public class DatabaseMigrator {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrator.class);

    private final DataSource dataSource;
    private final String locations;
    private final boolean enabled;
    private Flyway flyway;

    public DatabaseMigrator(DataSource dataSource, String locations) {
        this(dataSource, locations, true);
    }

    public DatabaseMigrator(DataSource dataSource, String locations, boolean enabled) {
        this.dataSource = dataSource;
        this.locations = locations;
        this.enabled = enabled;
    }

    /**
     * Initialize and run migrations.
     *
     * @return true if migration was successful, false otherwise
     */
    public boolean migrate() {
        if (!enabled) {
            logger.info("Database migration is disabled");
            return true;
        }

        try {
            // Validate database connection
            try (Connection conn = dataSource.getConnection()) {
                if (conn == null || !conn.isValid(5)) {
                    logger.error("Cannot connect to database for migration");
                    return false;
                }
            }

            // Configure Flyway
            FluentConfiguration config = Flyway.configure()
                    .dataSource(dataSource)
                    .locations(locations)
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .validateMigrationNaming(true);

            flyway = config.load();

            // Get current state before migration
            Configuration flywayConfig = flyway.getConfiguration();
            logger.info("Flyway configured with locations: {}", locations);

            // Run migration
            var result = flyway.migrate();

            if (result.migrationsExecuted > 0) {
                logger.info("Successfully executed {} migration(s)", result.migrationsExecuted);
            } else {
                logger.info("Database is up to date, no migrations needed");
            }

            return true;

        } catch (SQLException e) {
            logger.error("Database connection error during migration", e);
            return false;
        } catch (Exception e) {
            logger.error("Migration failed", e);
            return false;
        }
    }

    /**
     * Get the current schema version.
     *
     * @return The current version, or null if not available
     */
    public String getCurrentVersion() {
        if (flyway == null) {
            return null;
        }
        try {
            var info = flyway.info();
            var current = info.current();
            return current != null ? current.getVersion().getVersion() : null;
        } catch (Exception e) {
            logger.warn("Could not get current version", e);
            return null;
        }
    }

    /**
     * Validate migration state.
     *
     * @return true if validation passes
     */
    public boolean validate() {
        if (flyway == null) {
            logger.warn("Cannot validate - Flyway not initialized");
            return false;
        }
        try {
            flyway.validate();
            return true;
        } catch (Exception e) {
            logger.error("Migration validation failed", e);
            return false;
        }
    }
}
