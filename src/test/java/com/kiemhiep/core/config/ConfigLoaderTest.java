package com.kiemhiep.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @Test
    void loadModulesConfig_createsDefaultWhenMissing(@TempDir Path tempDir) throws Exception {
        Path configDir = tempDir.resolve("config/kiemhiep");
        ConfigLoader loader = new ConfigLoader(configDir);

        Map<String, Boolean> result = loader.loadModulesConfig();

        assertTrue(result.getOrDefault("cultivation", false));
        assertTrue(result.getOrDefault("economy", false));
        assertTrue(Files.exists(configDir.resolve("modules.json")));
    }

    @Test
    void isModuleEnabled_returnsTrueByDefault(@TempDir Path tempDir) {
        ConfigLoader loader = new ConfigLoader(tempDir.resolve("config/kiemhiep"));
        loader.loadModulesConfig();

        assertTrue(loader.isModuleEnabled("cultivation"));
        assertTrue(loader.isModuleEnabled("unknown_module"));
    }

    @Test
    void loadModulesConfig_readsExistingFile(@TempDir Path tempDir) throws Exception {
        Path configDir = tempDir.resolve("config/kiemhiep");
        Files.createDirectories(configDir);
        Files.writeString(configDir.resolve("modules.json"),
            "{\"cultivation\":false,\"economy\":true}");

        ConfigLoader loader = new ConfigLoader(configDir);
        Map<String, Boolean> result = loader.loadModulesConfig();

        assertFalse(result.get("cultivation"));
        assertTrue(result.get("economy"));
        assertFalse(loader.isModuleEnabled("cultivation"));
        assertTrue(loader.isModuleEnabled("economy"));
    }

    @Test
    void getConfigDir_returnsInjectedPath(@TempDir Path tempDir) {
        Path expected = tempDir.resolve("custom");
        ConfigLoader loader = new ConfigLoader(expected);

        assertEquals(expected, loader.getConfigDir());
        assertEquals(expected.resolve("modules.json"), loader.getModulesPath());
    }
}
