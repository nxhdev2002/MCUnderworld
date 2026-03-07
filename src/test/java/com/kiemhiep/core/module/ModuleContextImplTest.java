package com.kiemhiep.core.module;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.module.ModuleConfigLoader;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.core.config.ConfigLoader;
import com.kiemhiep.core.config.ModuleConfigLoaderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ModuleContextImplTest {

    @TempDir
    Path tempDir;

    private ModuleContext ctx;

    @BeforeEach
    void setUp() {
        ConfigLoader configLoader = new ConfigLoader(tempDir.resolve("config/kiemhiep"));
        configLoader.loadModulesConfig();
        com.kiemhiep.api.module.ModuleRegistry registry = new ModuleRegistryImpl(configLoader);
        ModuleConfigLoader moduleConfigLoader = new ModuleConfigLoaderImpl(configLoader);
        EventDispatcher eventDispatcher = new StubEventDispatcher();
        PlatformProvider platformProvider = new StubPlatformProvider();
        ctx = new ModuleContextImpl("test-module", registry, moduleConfigLoader, eventDispatcher, platformProvider);
    }

    @Test
    void getEventDispatcher_returnsNonNull() {
        assertNotNull(ctx.getEventDispatcher());
    }

    @Test
    void getPlatformProvider_returnsNonNull() {
        assertNotNull(ctx.getPlatformProvider());
    }

    @Test
    void getModuleId_returnsInjectedId() {
        assertEquals("test-module", ctx.getModuleId());
    }

    @Test
    void getModuleRegistry_returnsInjectedRegistry() {
        assertNotNull(ctx.getModuleRegistry());
    }

    @Test
    void getModuleConfigLoader_returnsInjectedLoader() {
        assertNotNull(ctx.getModuleConfigLoader());
    }
}
