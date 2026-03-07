package com.kiemhiep.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LimitsConfigLoaderTest {

    @Test
    void loadLimits_createsDefaultWhenMissing(@TempDir Path tempDir) {
        Path configDir = tempDir.resolve("config/kiemhiep");
        LimitsConfigLoader loader = new LimitsConfigLoader(configDir);

        Map<String, Integer> result = loader.loadLimits();

        assertEquals(1000, result.get("mob"));
        assertEquals(1000, result.get("projectile"));
        assertEquals(500, result.get("npc"));
        assertEquals(500, result.get("other"));
        assertTrue(Files.exists(configDir.resolve("limits.json")));
    }

    @Test
    void getLimit_returnsDefaultForType(@TempDir Path tempDir) {
        LimitsConfigLoader loader = new LimitsConfigLoader(tempDir.resolve("config/kiemhiep"));
        loader.loadLimits();

        assertEquals(1000, loader.getLimit("mob"));
        assertEquals(500, loader.getLimit("npc"));
    }

    @Test
    void loadLimits_readsExistingFile(@TempDir Path tempDir) throws Exception {
        Path configDir = tempDir.resolve("config/kiemhiep");
        Files.createDirectories(configDir);
        Files.writeString(configDir.resolve("limits.json"),
            "{\"mob\":2000,\"projectile\":500,\"npc\":100,\"other\":200}");

        LimitsConfigLoader loader = new LimitsConfigLoader(configDir);
        Map<String, Integer> result = loader.loadLimits();

        assertEquals(2000, result.get("mob"));
        assertEquals(500, result.get("projectile"));
        assertEquals(100, result.get("npc"));
        assertEquals(200, result.get("other"));
        assertEquals(2000, loader.getLimit("mob"));
    }

    @Test
    void getLimitsPath_returnsInjectedPath(@TempDir Path tempDir) {
        Path expected = tempDir.resolve("custom");
        LimitsConfigLoader loader = new LimitsConfigLoader(expected);

        assertEquals(expected, loader.getConfigDir());
        assertEquals(expected.resolve("limits.json"), loader.getLimitsPath());
    }
}
