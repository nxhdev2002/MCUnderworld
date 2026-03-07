package com.kiemhiep.core.module;

import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.core.config.ConfigLoader;
import com.kiemhiep.core.config.ModuleConfigLoaderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModuleLoaderTest {

    @TempDir
    Path tempDir;

    private ConfigLoader configLoader;
    private ModuleRegistryImpl registry;
    private ModuleLoader loader;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader(tempDir.resolve("config/kiemhiep"));
        configLoader.loadModulesConfig();
        registry = new ModuleRegistryImpl(configLoader);
        ModuleLoader.ModuleContextFactory factory = moduleId -> new ModuleContextImpl(moduleId, registry, new ModuleConfigLoaderImpl(configLoader), new StubEventDispatcher(), new StubPlatformProvider());
        loader = new ModuleLoader(registry, configLoader, factory);
    }

    @Test
    void loadAll_callsOnLoadOncePerModule() {
        FakeModule a = new FakeModule("a");
        FakeModule b = new FakeModule("b");
        registry.register(a);
        registry.register(b);

        loader.loadAll();
        assertEquals(1, a.getOnLoadCount());
        assertEquals(1, b.getOnLoadCount());

        loader.loadAll();
        assertEquals(1, a.getOnLoadCount());
        assertEquals(1, b.getOnLoadCount());
    }

    @Test
    void applyConfig_enablesModulesInDependencyOrder() {
        FakeModule base = new FakeModule("base");
        FakeModule dep = new FakeModule("dep", List.of("base"));
        registry.register(base);
        registry.register(dep);

        loader.loadAll();
        loader.applyConfig();

        assertTrue(registry.isEnabled("base"));
        assertTrue(registry.isEnabled("dep"));
        assertEquals(1, base.getOnEnableCount());
        assertEquals(1, dep.getOnEnableCount());
    }

    @Test
    void enableModule_callsOnEnableOnlyWhenDisabled() {
        FakeModule m = new FakeModule("m");
        registry.register(m);
        registry.setEnabled("m", false);
        loader.loadAll();

        loader.enableModule("m");
        assertEquals(1, m.getOnEnableCount());
        assertTrue(registry.isEnabled("m"));

        loader.enableModule("m");
        assertEquals(1, m.getOnEnableCount());
    }

    @Test
    void disableModule_callsOnDisableOnlyWhenEnabled() {
        FakeModule m = new FakeModule("m");
        registry.register(m);
        loader.loadAll();
        loader.enableModule("m");

        loader.disableModule("m");
        assertEquals(1, m.getOnDisableCount());
        assertFalse(registry.isEnabled("m"));

        loader.disableModule("m");
        assertEquals(1, m.getOnDisableCount());
    }

    @Test
    void enableModule_ignoresUnknownId() {
        loader.enableModule("nonexistent");
        assertTrue(registry.getAll().isEmpty());
    }
}
