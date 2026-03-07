package com.kiemhiep.core.module;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.core.config.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModuleRegistryImplTest {

    @TempDir
    Path tempDir;

    private ConfigLoader configLoader;
    private ModuleRegistryImpl registry;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader(tempDir.resolve("config/kiemhiep"));
        configLoader.loadModulesConfig();
        registry = new ModuleRegistryImpl(configLoader);
    }

    @Test
    void register_andGetAll() {
        FakeModule a = new FakeModule("a");
        FakeModule b = new FakeModule("b");
        registry.register(a);
        registry.register(b);

        List<KiemHiepModule> all = registry.getAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(m -> m.getId().equals("a")));
        assertTrue(all.stream().anyMatch(m -> m.getId().equals("b")));
    }

    @Test
    void get_returnsOptional() {
        registry.register(new FakeModule("x"));
        assertTrue(registry.get("x").isPresent());
        assertEquals("x", registry.get("x").orElseThrow().getId());
        assertTrue(registry.get("nonexistent").isEmpty());
    }

    @Test
    void isEnabled_usesConfigByDefault() {
        registry.register(new FakeModule("cultivation"));
        assertTrue(registry.isEnabled("cultivation"));
    }

    @Test
    void setEnabled_updatesState() {
        FakeModule m = new FakeModule("m");
        registry.register(m);
        assertFalse(m.isEnabled());

        registry.setEnabled("m", true);
        assertTrue(registry.isEnabled("m"));
        assertTrue(m.isEnabled());

        registry.setEnabled("m", false);
        assertFalse(registry.isEnabled("m"));
        assertFalse(m.isEnabled());
    }

    @Test
    void reloadFromConfig_updatesFromConfig() throws Exception {
        FakeModule m = new FakeModule("cultivation");
        registry.register(m);
        registry.setEnabled("cultivation", false);

        java.nio.file.Files.createDirectories(tempDir.resolve("config/kiemhiep"));
        java.nio.file.Files.writeString(tempDir.resolve("config/kiemhiep/modules.json"),
            "{\"cultivation\":false}");

        configLoader.loadModulesConfig();
        registry.reloadFromConfig();

        assertFalse(registry.isEnabled("cultivation"));
        assertFalse(m.isEnabled());
    }
}
