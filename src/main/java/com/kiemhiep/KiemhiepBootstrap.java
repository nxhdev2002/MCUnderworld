package com.kiemhiep;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.core.command.ModuleCommands;
import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.cache.NoOpDistributedCache;
import com.kiemhiep.core.cache.RedisDistributedCache;
import com.kiemhiep.core.config.ConfigLoader;
import com.kiemhiep.core.config.DatabaseConfigLoader;
import com.kiemhiep.core.config.LimitsConfigLoader;
import com.kiemhiep.core.config.ModuleConfigLoaderImpl;
import com.kiemhiep.core.config.RedisConfigLoader;
import com.kiemhiep.core.database.DataSourceHolder;
import com.kiemhiep.core.database.JdbcServerMetricsRepository;
import com.kiemhiep.core.event.EventDispatcherImpl;
import com.kiemhiep.core.monitor.ServerMetricsRecorder;
import com.kiemhiep.core.sync.NoOpMessageBus;
import com.kiemhiep.core.sync.RedisMessageBus;
import com.kiemhiep.core.limit.EntityLimitEnforcer;
import com.kiemhiep.core.module.ModuleContextImpl;
import com.kiemhiep.core.module.ModuleLoader;
import com.kiemhiep.core.module.ModuleRegistryImpl;
import com.kiemhiep.core.monitor.TPSMonitor;
import com.kiemhiep.platform.FabricPlatformProvider;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Khởi tạo core: config, module registry/loader, event dispatcher, platform provider,
 * limits enforcer, TPS monitor; load + apply config; đăng ký lệnh và server lifecycle.
 */
public final class KiemhiepBootstrap {

    private KiemhiepBootstrap() {}

    private static ConfigLoader configLoader;
    private static DatabaseConfigLoader databaseConfigLoader;
    private static DataSourceHolder dataSourceHolder;
    private static DistributedCache distributedCache;
    private static MessageBus messageBus;
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

        databaseConfigLoader = new DatabaseConfigLoader(configLoader.getConfigDir());
        databaseConfigLoader.load();
        dataSourceHolder = DataSourceHolder.createIfEnabled(databaseConfigLoader.getConfig()).orElse(null);

        RedisConfigLoader redisConfigLoader = new RedisConfigLoader(configLoader.getConfigDir());
        redisConfigLoader.load();
        if (redisConfigLoader.getConfig().host() != null && !redisConfigLoader.getConfig().host().isBlank()) {
            try {
                distributedCache = new RedisDistributedCache(redisConfigLoader.getConfig());
                messageBus = new RedisMessageBus(redisConfigLoader.getConfig());
            } catch (Exception e) {
                Kiemhiep.LOGGER.warn("Redis init failed, using no-op cache and message bus", e);
                distributedCache = new NoOpDistributedCache();
                messageBus = new NoOpMessageBus();
            }
        } else {
            distributedCache = new NoOpDistributedCache();
            messageBus = new NoOpMessageBus();
        }

        eventDispatcher = new EventDispatcherImpl();
        platformProvider = new FabricPlatformProvider();
        limitsConfigLoader = new LimitsConfigLoader(configLoader.getConfigDir());
        limitsConfigLoader.loadLimits();
        entityLimitEnforcer = new EntityLimitEnforcer(limitsConfigLoader);
        tpsMonitor = new TPSMonitor();
        if (dataSourceHolder != null) {
            try {
                var serverMetricsRepo = new JdbcServerMetricsRepository(dataSourceHolder.getDataSource());
                String serverId = java.net.InetAddress.getLocalHost().getHostName();
                ServerMetricsRecorder recorder = new ServerMetricsRecorder(serverMetricsRepo, serverId, 10_000);
                tpsMonitor.setMetricsCallback(arr -> recorder.onMetrics(arr[0], arr[1]));
            } catch (Exception e) {
                Kiemhiep.LOGGER.warn("Could not init server metrics recorder", e);
            }
        }

        registry = new ModuleRegistryImpl(configLoader);
        ModuleConfigLoaderImpl moduleConfigLoader = new ModuleConfigLoaderImpl(configLoader);
        ModuleLoader.ModuleContextFactory contextFactory = moduleId ->
            new ModuleContextImpl(moduleId, registry, moduleConfigLoader, eventDispatcher, platformProvider);
        loader = new ModuleLoader(registry, configLoader, contextFactory);

        loader.loadAll();
        loader.applyConfig();

        ModuleCommands.register(() -> registry, () -> loader);

        ServerLifecycleEvents.SERVER_STARTED.register(KiemhiepBootstrap::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(KiemhiepBootstrap::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(KiemhiepBootstrap::onServerTickEnd);

        Kiemhiep.LOGGER.info("KiemHiep core initialized.");
    }

    private static void onServerStopping(MinecraftServer server) {
        if (distributedCache instanceof RedisDistributedCache redis) {
            redis.close();
        }
        if (messageBus instanceof RedisMessageBus redis) {
            redis.close();
        }
        if (dataSourceHolder != null) {
            dataSourceHolder.close();
        }
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

    public static Optional<DataSource> getDataSource() {
        return dataSourceHolder != null
            ? Optional.of(dataSourceHolder.getDataSource())
            : Optional.empty();
    }

    public static DatabaseConfigLoader getDatabaseConfigLoader() {
        return databaseConfigLoader;
    }

    public static DistributedCache getDistributedCache() {
        return distributedCache;
    }

    public static MessageBus getMessageBus() {
        return messageBus;
    }
}
