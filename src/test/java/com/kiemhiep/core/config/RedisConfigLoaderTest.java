package com.kiemhiep.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RedisConfigLoaderTest {

    @Test
    void load_createsDefaultWhenMissing(@TempDir Path tempDir) {
        Path configDir = tempDir.resolve("config/kiemhiep");
        RedisConfigLoader loader = new RedisConfigLoader(configDir);

        RedisConfig config = loader.load();

        assertEquals("localhost", config.host());
        assertEquals(6379, config.port());
        assertTrue(Files.exists(configDir.resolve("redis.json")));
    }

    @Test
    void load_readsExistingFile(@TempDir Path tempDir) throws Exception {
        Path configDir = tempDir.resolve("config/kiemhiep");
        Files.createDirectories(configDir);
        Files.writeString(configDir.resolve("redis.json"),
            "{\"host\":\"redis.example.com\",\"port\":6380,\"password\":\"secret\"}");

        RedisConfigLoader loader = new RedisConfigLoader(configDir);
        RedisConfig config = loader.load();

        assertEquals("redis.example.com", config.host());
        assertEquals(6380, config.port());
        assertEquals("secret", config.password());
    }
}
