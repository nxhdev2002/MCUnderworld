package com.kiemhiep;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.core.command.ModuleCommands;
import com.kiemhiep.core.config.ConfigLoader;
import com.kiemhiep.core.config.LimitsConfigLoader;
import com.kiemhiep.core.config.ModuleConfigLoaderImpl;
import com.kiemhiep.core.event.EventDispatcherImpl;
import com.kiemhiep.core.limit.EntityLimitEnforcer;
import com.kiemhiep.core.module.ModuleContextImpl;
import com.kiemhiep.core.module.ModuleLoader;
import com.kiemhiep.core.module.ModuleRegistryImpl;
import com.kiemhiep.core.monitor.TPSMonitor;
import com.kiemhiep.platform.FabricPlatformProvider;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Khởi tạo core: config, module registry/loader, event dispatcher, platform provider,
 * limits enforcer, TPS monitor; load + apply config; đăng ký lệnh và server lifecycle.
 */
public final class KiemhiepBootstrap {

    private static ConfigLoader configLoader;
    private static ModuleRegistry registry;
    private static ModuleLoader loader;
    private static EventDispatcher eventDispatcher;
    private static PlatformProvider platformProvider;
    private static LimitsConfigLoader limitsConfigLoader;
    private static EntityLimitEnforcer entityLimitEnforcer;
    private static TPSMonitor tpsMonitor;

    public static void onInitialize() {
        configLoader = new ConfigLoader();
        configLoader.loadModulesConfig();

        eventDispatcher = new EventDispatcherImpl();
        platformProvider = new FabricPlatformProvider();
        limitsConfigLoader = new LimitsConfigLoader(configLoader.getConfigDir());
        limitsConfigLoader.loadLimits();
        entityLimitEnforcer = new EntityLimitEnforcer(limitsConfigLoader);
        tpsMonitor = new TPSMonitor();

        registry = new ModuleRegistryImpl(configLoader);
        ModuleConfigLoaderImpl moduleConfigLoader = new ModuleConfigLoaderImpl(configLoader);
        ModuleLoader.ModuleContextFactory contextFactory = moduleId ->
            new ModuleContextImpl(moduleId, registry, moduleConfigLoader, eventDispatcher, platformProvider);
        loader = new ModuleLoader(registry, configLoader, contextFactory);

        loader.loadAll();
        loader.applyConfig();

        ModuleCommands.register(() -> registry, () -> loader);

        ServerLifecycleEvents.SERVER_STARTED.register(KiemhiepBootstrap::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(KiemhiepBootstrap::onServerTickEnd);

        Kiemhiep.LOGGER.info("KiemHiep core initialized.");
    }

    private static long lastTickTime = 0;

    private static void onServerStarted(MinecraftServer server) {
        ((FabricPlatformProvider) platformProvider).setServer(server);
    }

    private static void onServerTickEnd(MinecraftServer server) {
        long now = System.currentTimeMillis();
        if (lastTickTime > 0) {
            tpsMonitor.update(now - lastTickTime);
        }
        lastTickTime = now;
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

    public static EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public static PlatformProvider getPlatformProvider() {
        return platformProvider;
    }

    public static LimitsConfigLoader getLimitsConfigLoader() {
        return limitsConfigLoader;
    }

    public static EntityLimitEnforcer getEntityLimitEnforcer() {
        return entityLimitEnforcer;
    }

    public static TPSMonitor getTpsMonitor() {
        return tpsMonitor;
    }
}
