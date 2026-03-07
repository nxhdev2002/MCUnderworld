package com.kiemhiep.core.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kiemhiep.Kiemhiep;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Đọc config/kiemhiep/limits.json — map entity type -> limit (mob, projectile, npc, other).
 * Rule 5: Mob 1000, Projectile 1000, NPC 500, Other 500.
 */
public class LimitsConfigLoader {

    private static final String CONFIG_DIR = "config/kiemhiep";
    private static final String LIMITS_FILE = "limits.json";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Integer>>() {}.getType();

    private final Path configDir;
    private final Path limitsPath;
    private Map<String, Integer> limits = new HashMap<>();

    public LimitsConfigLoader() {
        this(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_DIR));
    }

    /** Constructor for testing. */
    public LimitsConfigLoader(Path configDir) {
        this.configDir = configDir;
        this.limitsPath = configDir.resolve(LIMITS_FILE);
    }

    /** Đọc lại limits.json và trả về map type -> limit. */
    public Map<String, Integer> loadLimits() {
        try {
            if (!Files.exists(limitsPath)) {
                writeDefaultLimits();
            }
            String json = Files.readString(limitsPath);
            Map<String, Integer> map = GSON.fromJson(json, MAP_TYPE);
            this.limits = map != null ? map : new HashMap<>();
            applyDefaults();
            return new HashMap<>(limits);
        } catch (IOException e) {
            Kiemhiep.LOGGER.warn("Could not load limits config from {}", limitsPath, e);
            this.limits = getDefaultLimits();
            return new HashMap<>(limits);
        }
    }

    public int getLimit(String entityType) {
        return limits.getOrDefault(entityType, Integer.MAX_VALUE);
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getLimitsPath() {
        return limitsPath;
    }

    private Map<String, Integer> getDefaultLimits() {
        Map<String, Integer> def = new HashMap<>();
        def.put("mob", 1000);
        def.put("projectile", 1000);
        def.put("npc", 500);
        def.put("other", 500);
        return def;
    }

    private void applyDefaults() {
        Map<String, Integer> def = getDefaultLimits();
        for (Map.Entry<String, Integer> e : def.entrySet()) {
            limits.putIfAbsent(e.getKey(), e.getValue());
        }
    }

    private void writeDefaultLimits() throws IOException {
        Files.createDirectories(configDir);
        Map<String, Integer> def = getDefaultLimits();
        Files.writeString(limitsPath, GSON.toJson(def));
    }
}
