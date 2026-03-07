package com.kiemhiep.data.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Redis configuration and connection manager.
 */
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private final String host;
    private final int port;
    private final String password;
    private final int database;
    private final int timeout;

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    public RedisConfig(String host, int port) {
        this(host, port, null, 0, 5000);
    }

    public RedisConfig(String host, int port, String password, int database, int timeout) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.database = database;
        this.timeout = timeout;
    }

    /**
     * Initialize Redis connection.
     */
    public void initialize() {
        if (redisClient != null) {
            logger.warn("RedisClient already initialized");
            return;
        }

        try {
            RedisURI.Builder uriBuilder = RedisURI.builder()
                    .withHost(host)
                    .withPort(port)
                    .withTimeout(java.time.Duration.ofMillis(timeout));

            if (password != null && !password.isEmpty()) {
                uriBuilder.withPassword(password);
            }

            if (database > 0) {
                uriBuilder.withDatabase(database);
            }

            redisClient = RedisClient.create(uriBuilder.build());

            // Create connection with string codec
            connection = redisClient.connect(StringCodec.UTF8);

            logger.info("Redis connection initialized successfully");
            logger.info("Redis URI: redis://{}:{}/{}", host, port, database);

            // Test connection
            try {
                String ping = connection.sync().ping();
                logger.info("Redis PING response: {}", ping);
            } catch (Exception e) {
                logger.warn("Redis PING failed, but connection may still work", e);
            }

        } catch (Exception e) {
            logger.error("Failed to initialize Redis connection", e);
            throw new RuntimeException("Failed to initialize Redis", e);
        }
    }

    /**
     * Get synchronous Redis commands.
     */
    public RedisCommands<String, String> getSyncCommands() {
        if (connection == null) {
            throw new IllegalStateException("Redis connection not initialized");
        }
        return connection.sync();
    }

    /**
     * Get asynchronous Redis commands.
     */
    public RedisAsyncCommands<String, String> getAsyncCommands() {
        if (connection == null) {
            throw new IllegalStateException("Redis connection not initialized");
        }
        return connection.async();
    }

    /**
     * Check if Redis is connected.
     */
    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    /**
     * Close Redis connection.
     */
    public void shutdown() {
        if (connection != null) {
            connection.close();
            logger.info("Redis connection closed");
        }
        if (redisClient != null) {
            redisClient.shutdown();
            logger.info("RedisClient shut down");
        }
    }

    /**
     * Test Redis connection.
     */
    public boolean testConnection() {
        try {
            String pong = getSyncCommands().ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            logger.warn("Redis connection test failed", e);
            return false;
        }
    }
}
