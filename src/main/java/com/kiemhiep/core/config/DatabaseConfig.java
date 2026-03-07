package com.kiemhiep.core.config;

/**
 * DB connection config read from config/kiemhiep/database.json.
 */
public record DatabaseConfig(
    String url,
    String username,
    String password,
    int poolSize
) {
    public static final int DEFAULT_POOL_SIZE = 10;

    public DatabaseConfig {
        if (url == null) url = "";
        if (username == null) username = "";
        if (password == null) password = "";
    }

    public static DatabaseConfig defaults() {
        return new DatabaseConfig(
            "jdbc:postgresql://localhost:5432/kiemhiep",
            "kiemhiep",
            "",
            DEFAULT_POOL_SIZE
        );
    }
}
