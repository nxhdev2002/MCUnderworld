package com.kiemhiep.fabric;

import com.kiemhiep.fabric.config.ModConfig;
import com.kiemhiep.fabric.database.DatabaseManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main KiemHiep mod entrypoint for Fabric.
 */
public class KiemHiepMod implements ModInitializer {
    public static final String MOD_ID = "kiemhiep";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KiemHiepMod instance;
    private ModConfig config;
    private DatabaseManager databaseManager;

    @Override
    public void onInitialize() {
        instance = this;

        LOGGER.info("Initializing KiemHiep Mod for Fabric...");

        // Initialize configuration
        config = ModConfig.initialize();

        // Initialize database
        databaseManager = new DatabaseManager(config);
        databaseManager.initialize();

        // Initialize modules
        // TODO: Initialize feature modules (cultivation, combat, economy, etc.)

        LOGGER.info("KiemHiep Mod initialized successfully!");
    }

    public static KiemHiepMod getInstance() {
        return instance;
    }

    public ModConfig getConfig() {
        return config;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
