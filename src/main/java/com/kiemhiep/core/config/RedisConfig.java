package com.kiemhiep.core.config;

/**
 * Redis connection config from config/kiemhiep/redis.json.
 */
public record RedisConfig(
    String host,
    int port,
    String password
) {
    public static final int DEFAULT_PORT = 6379;

    public RedisConfig {
        if (host == null) host = "localhost";
        if (password == null) password = "";
    }

    public int port() {
        return port > 0 ? port : DEFAULT_PORT;
    }

    public static RedisConfig defaults() {
        return new RedisConfig("localhost", DEFAULT_PORT, "");
    }
}
