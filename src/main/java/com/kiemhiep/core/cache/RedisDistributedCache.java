package com.kiemhiep.core.cache;

import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.core.config.RedisConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Redis implementation of DistributedCache. Key format: kiemhiep:&lt;domain&gt;:&lt;id&gt;
 */
public class RedisDistributedCache implements DistributedCache {

    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> commands;

    public RedisDistributedCache(RedisConfig config) {
        RedisURI uri = RedisURI.builder()
            .withHost(config.host())
            .withPort(config.port())
            .withPassword(config.password() != null && !config.password().isEmpty() ? config.password().toCharArray() : null)
            .build();
        this.client = RedisClient.create(uri);
        this.connection = client.connect();
        this.commands = connection.sync();
    }

    @Override
    public String get(String key) {
        return commands.get(key);
    }

    @Override
    public void set(String key, String value, long ttlSeconds) {
        if (ttlSeconds > 0) {
            commands.set(key, value, SetArgs.Builder.ex(ttlSeconds));
        } else {
            commands.set(key, value);
        }
    }

    @Override
    public void delete(String key) {
        commands.del(key);
    }

    public void close() {
        if (connection != null) {
            connection.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }
}
