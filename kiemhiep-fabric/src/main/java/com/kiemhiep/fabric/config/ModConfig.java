package com.kiemhiep.fabric.config;

import com.kiemhiep.fabric.KiemHiepMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Mod configuration manager.
 * Loads configuration from kiemhiep.properties file.
 */
public class ModConfig {
    private static final Logger logger = LoggerFactory.getLogger(ModConfig.class);
    private static final String CONFIG_FILE = "kiemhiep.properties";

    // Database settings
    private String dbHost;
    private int dbPort;
    private String dbDatabase;
    private String dbUsername;
    private String dbPassword;
    private int dbMaxPoolSize;
    private int dbMinIdle;

    // Redis settings
    private String redisHost;
    private int redisPort;
    private String redisPassword;
    private int redisDatabase;

    // Game settings
    private boolean cultivationEnabled;
    private boolean combatEnabled;
    private boolean economyEnabled;
    private boolean sectEnabled;
    private boolean questEnabled;
    private boolean skillEnabled;
    private boolean dungeonEnabled;
    private boolean npcEnabled;

    // Performance settings
    private int asyncThreadPoolSize;
    private int cacheExpiryMinutes;

    private Properties properties;

    /**
     * Initialize and load configuration.
     */
    public static ModConfig initialize() {
        ModConfig config = new ModConfig();
        config.load();
        return config;
    }

    /**
     * Load configuration from file.
     */
    private void load() {
        properties = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
                logger.info("Configuration loaded from {}", CONFIG_FILE);
            } catch (IOException e) {
                logger.error("Failed to load configuration file", e);
            }
        } else {
            logger.warn("Configuration file not found, using defaults");
            // Create default config file
            createDefaultConfig(configFile);
        }

        // Load values
        loadValues();
    }

    /**
     * Load values from properties.
     */
    private void loadValues() {
        // Database settings
        dbHost = getProperty("database.host", "localhost");
        dbPort = getIntProperty("database.port", 5432);
        dbDatabase = getProperty("database.name", "kiemhiep");
        dbUsername = getProperty("database.username", "postgres");
        dbPassword = getProperty("database.password", "");
        dbMaxPoolSize = getIntProperty("database.maxPoolSize", 20);
        dbMinIdle = getIntProperty("database.minIdle", 5);

        // Redis settings
        redisHost = getProperty("redis.host", "localhost");
        redisPort = getIntProperty("redis.port", 6379);
        redisPassword = getProperty("redis.password", "");
        redisDatabase = getIntProperty("redis.database", 0);

        // Game settings
        cultivationEnabled = getBooleanProperty("modules.cultivation.enabled", true);
        combatEnabled = getBooleanProperty("modules.combat.enabled", true);
        economyEnabled = getBooleanProperty("modules.economy.enabled", true);
        sectEnabled = getBooleanProperty("modules.sect.enabled", true);
        questEnabled = getBooleanProperty("modules.quest.enabled", true);
        skillEnabled = getBooleanProperty("modules.skill.enabled", true);
        dungeonEnabled = getBooleanProperty("modules.dungeon.enabled", true);
        npcEnabled = getBooleanProperty("modules.npc.enabled", true);

        // Performance settings
        asyncThreadPoolSize = getIntProperty("performance.asyncThreadPoolSize", 10);
        cacheExpiryMinutes = getIntProperty("performance.cacheExpiryMinutes", 30);
    }

    /**
     * Create default configuration file.
     */
    private void createDefaultConfig(File configFile) {
        Properties defaultProps = new Properties();

        // Database settings
        defaultProps.setProperty("database.host", "localhost");
        defaultProps.setProperty("database.port", "5432");
        defaultProps.setProperty("database.name", "kiemhiep");
        defaultProps.setProperty("database.username", "postgres");
        defaultProps.setProperty("database.password", "");
        defaultProps.setProperty("database.maxPoolSize", "20");
        defaultProps.setProperty("database.minIdle", "5");

        // Redis settings
        defaultProps.setProperty("redis.host", "localhost");
        defaultProps.setProperty("redis.port", "6379");
        defaultProps.setProperty("redis.password", "");
        defaultProps.setProperty("redis.database", "0");

        // Game settings
        defaultProps.setProperty("modules.cultivation.enabled", "true");
        defaultProps.setProperty("modules.combat.enabled", "true");
        defaultProps.setProperty("modules.economy.enabled", "true");
        defaultProps.setProperty("modules.sect.enabled", "true");
        defaultProps.setProperty("modules.quest.enabled", "true");
        defaultProps.setProperty("modules.skill.enabled", "true");
        defaultProps.setProperty("modules.dungeon.enabled", "true");
        defaultProps.setProperty("modules.npc.enabled", "true");

        // Performance settings
        defaultProps.setProperty("performance.asyncThreadPoolSize", "10");
        defaultProps.setProperty("performance.cacheExpiryMinutes", "30");

        try (OutputStream output = new FileOutputStream(configFile)) {
            defaultProps.store(output, "KiemHiep Mod Configuration");
            logger.info("Created default configuration file: {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Failed to create default configuration file", e);
        }
    }

    private String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for {}: {}", key, value);
            return defaultValue;
        }
    }

    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }

    // Getters

    public String getDbHost() { return dbHost; }
    public int getDbPort() { return dbPort; }
    public String getDbDatabase() { return dbDatabase; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }
    public int getDbMaxPoolSize() { return dbMaxPoolSize; }
    public int getDbMinIdle() { return dbMinIdle; }

    public String getRedisHost() { return redisHost; }
    public int getRedisPort() { return redisPort; }
    public String getRedisPassword() { return redisPassword; }
    public int getRedisDatabase() { return redisDatabase; }

    public boolean isCultivationEnabled() { return cultivationEnabled; }
    public boolean isCombatEnabled() { return combatEnabled; }
    public boolean isEconomyEnabled() { return economyEnabled; }
    public boolean isSectEnabled() { return sectEnabled; }
    public boolean isQuestEnabled() { return questEnabled; }
    public boolean isSkillEnabled() { return skillEnabled; }
    public boolean isDungeonEnabled() { return dungeonEnabled; }
    public boolean isNpcEnabled() { return npcEnabled; }

    public int getAsyncThreadPoolSize() { return asyncThreadPoolSize; }
    public int getCacheExpiryMinutes() { return cacheExpiryMinutes; }
}
