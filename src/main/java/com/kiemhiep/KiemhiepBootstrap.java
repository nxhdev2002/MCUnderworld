package com.kiemhiep;

import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.core.command.ModuleCommands;
import com.kiemhiep.core.config.ConfigLoader;
import com.kiemhiep.core.config.ModuleConfigLoaderImpl;
import com.kiemhiep.core.module.ModuleContextImpl;
import com.kiemhiep.core.module.ModuleLoader;
import com.kiemhiep.core.module.ModuleRegistryImpl;

/**
 * Khởi tạo core: config, module registry/loader, đăng ký module (skeleton),
 * load + apply config, đăng ký lệnh.
 */
public final class KiemhiepBootstrap {

    private static ConfigLoader configLoader;
    private static ModuleRegistry registry;
    private static ModuleLoader loader;

    public static void onInitialize() {
        configLoader = new ConfigLoader();
        configLoader.loadModulesConfig();

        registry = new ModuleRegistryImpl(configLoader);
        ModuleConfigLoaderImpl moduleConfigLoader = new ModuleConfigLoaderImpl(configLoader);
        ModuleLoader.ModuleContextFactory contextFactory = moduleId ->
            new ModuleContextImpl(moduleId, registry, moduleConfigLoader);
        loader = new ModuleLoader(registry, configLoader, contextFactory);

        // Phase 04+ sẽ đăng ký module ở đây: registry.register(new CultivationModule()); ...
        // Hiện tại không có module nào.

        loader.loadAll();
        loader.applyConfig();

        ModuleCommands.register(() -> registry, () -> loader);

        Kiemhiep.LOGGER.info("KiemHiep core initialized.");
    }

    public static ModuleRegistry getRegistry() {
        return registry;
    }

    public static ModuleLoader getLoader() {
        return loader;
    }

    public static ConfigLoader getConfigLoader() {
        return configLoader;
    }
}
