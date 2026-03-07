package com.kiemhiep.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigLoaderTest {

    @Test
    void load_createsDefaultWhenMissing(@TempDir Path tempDir) {
        Path configDir = tempDir.resolve("config/kiemhiep");
        DatabaseConfigLoader loader = new DatabaseConfigLoader(configDir);

        DatabaseConfig config = loader.load();

        assertNotNull(config.url());
        assertTrue(config.url().contains("postgresql"));
        assertEquals(10, config.poolSize());
        assertTrue(Files.exists(configDir.resolve("database.json")));
    }

    @Test
    void load_readsExistingFile(@TempDir Path tempDir) throws Exception {
        Path configDir = tempDir.resolve("config/kiemhiep");
        Files.createDirectories(configDir);
        Files.writeString(configDir.resolve("database.json"),
            "{\"url\":\"jdbc:postgresql://host/db\",\"username\":\"u\",\"password\":\"p\",\"poolSize\":5}");

        DatabaseConfigLoader loader = new DatabaseConfigLoader(configDir);
        DatabaseConfig config = loader.load();

        assertEquals("jdbc:postgresql://host/db", config.url());
        assertEquals("u", config.username());
        assertEquals("p", config.password());
        assertEquals(5, config.poolSize());
    }

    @Test
    void getConfig_returnsLastLoaded(@TempDir Path tempDir) {
        DatabaseConfigLoader loader = new DatabaseConfigLoader(tempDir.resolve("config/kiemhiep"));
        loader.load();

        assertNotNull(loader.getConfig());
        assertEquals("kiemhiep", loader.getConfig().username());
    }
}
