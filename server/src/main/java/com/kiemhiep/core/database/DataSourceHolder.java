package com.kiemhiep.core.database;

import com.kiemhiep.core.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Holds HikariCP DataSource and runs Flyway migrations.
 * Created when database config is present and enabled.
 */
public final class DataSourceHolder {

    private final HikariDataSource dataSource;

    public DataSourceHolder(DatabaseConfig config) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.url());
        hikari.setUsername(config.username());
        hikari.setPassword(config.password());
        hikari.setMaximumPoolSize(config.poolSize());
        this.dataSource = new HikariDataSource(hikari);
        runFlyway(dataSource);
    }

    private static void runFlyway(DataSource ds) {
        Flyway flyway = Flyway.configure()
            .dataSource(ds)
            .locations("classpath:db/migration")
            .load();
        flyway.migrate();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Create holder only when config has a non-empty URL (DB enabled).
     */
    public static Optional<DataSourceHolder> createIfEnabled(DatabaseConfig config) {
        if (config == null || config.url() == null || config.url().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new DataSourceHolder(config));
    }
}
