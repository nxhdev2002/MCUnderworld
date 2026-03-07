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
 * Đọc config/kiemhiep/modules.json — map moduleId -> enabled (boolean).
 */
public class ConfigLoader {

    private static final String CONFIG_DIR = "config/kiemhiep";
    private static final String MODULES_FILE = "modules.json";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Boolean>>() {}.getType();

    private final Path configDir;
    private final Path modulesPath;
    private Map<String, Boolean> moduleEnabled = new HashMap<>();

    public ConfigLoader() {
        this(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_DIR));
    }

    /** Constructor for testing: dùng thư mục config tùy chỉnh (vd. temp dir). */
    public ConfigLoader(Path configDir) {
        this.configDir = configDir;
        this.modulesPath = configDir.resolve(MODULES_FILE);
    }

    /** Đọc lại file modules.json và trả về map id -> enabled. */
    public Map<String, Boolean> loadModulesConfig() {
        try {
            if (!Files.exists(modulesPath)) {
                writeDefaultModulesConfig();
            }
            String json = Files.readString(modulesPath);
            Map<String, Boolean> map = GSON.fromJson(json, MAP_TYPE);
            this.moduleEnabled = map != null ? map : new HashMap<>();
            return new HashMap<>(moduleEnabled);
        } catch (IOException e) {
            Kiemhiep.LOGGER.warn("Could not load modules config from {}", modulesPath, e);
            this.moduleEnabled = getDefaultModulesConfig();
            return new HashMap<>(moduleEnabled);
        }
    }

    public boolean isModuleEnabled(String moduleId) {
        return moduleEnabled.getOrDefault(moduleId, true);
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getModulesPath() {
        return modulesPath;
    }

    private Map<String, Boolean> getDefaultModulesConfig() {
        Map<String, Boolean> def = new HashMap<>();
        def.put("cultivation", true);
        def.put("economy", true);
        def.put("sect", true);
        def.put("combat", true);
        def.put("skill", true);
        def.put("quest", true);
        def.put("npcs", true);
        def.put("dungeon", true);
        return def;
    }

    private void writeDefaultModulesConfig() throws IOException {
        Files.createDirectories(configDir);
        Map<String, Boolean> def = getDefaultModulesConfig();
        Files.writeString(modulesPath, GSON.toJson(def));
    }
}
