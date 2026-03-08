package com.kiemhiep.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kiemhiep.Kiemhiep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Đọc config/kiemhiep/redis.json — host, port, password.
 */
public class RedisConfigLoader {

    private static final String REDIS_FILE = "redis.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configDir;
    private final Path redisPath;
    private RedisConfig config;

    public RedisConfigLoader(Path configDir) {
        this.configDir = configDir;
        this.redisPath = configDir.resolve(REDIS_FILE);
        this.config = RedisConfig.defaults();
    }

    public RedisConfig load() {
        try {
            if (!Files.exists(redisPath)) {
                writeDefault();
            }
            String json = Files.readString(redisPath);
            var parsed = GSON.fromJson(json, RedisConfigJson.class);
            if (parsed != null) {
                this.config = new RedisConfig(
                    parsed.host != null ? parsed.host : RedisConfig.defaults().host(),
                    parsed.port > 0 ? parsed.port : RedisConfig.DEFAULT_PORT,
                    parsed.password != null ? parsed.password : ""
                );
            }
            return config;
        } catch (IOException e) {
            Kiemhiep.LOGGER.warn("Could not load redis config from {}", redisPath, e);
            this.config = RedisConfig.defaults();
            return config;
        }
    }

    public RedisConfig getConfig() {
        return config != null ? config : RedisConfig.defaults();
    }

    private void writeDefault() throws IOException {
        Files.createDirectories(configDir);
        RedisConfig def = RedisConfig.defaults();
        RedisConfigJson dto = new RedisConfigJson(def.host(), def.port(), def.password());
        Files.writeString(redisPath, GSON.toJson(dto));
    }

    @SuppressWarnings("unused")
    private static class RedisConfigJson {
        String host;
        int port;
        String password;

        RedisConfigJson() {}

        RedisConfigJson(String host, int port, String password) {
            this.host = host;
            this.port = port;
            this.password = password;
        }
    }
}
