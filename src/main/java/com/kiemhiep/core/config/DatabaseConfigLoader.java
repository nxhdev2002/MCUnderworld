package com.kiemhiep.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kiemhiep.Kiemhiep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Đọc config/kiemhiep/database.json — url, username, password, poolSize.
 */
public class DatabaseConfigLoader {

    private static final String DATABASE_FILE = "database.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configDir;
    private final Path databasePath;
    private DatabaseConfig config;

    public DatabaseConfigLoader(Path configDir) {
        this.configDir = configDir;
        this.databasePath = configDir.resolve(DATABASE_FILE);
        this.config = DatabaseConfig.defaults();
    }

    public DatabaseConfig load() {
        try {
            if (!Files.exists(databasePath)) {
                writeDefault();
            }
            String json = Files.readString(databasePath);
            var parsed = GSON.fromJson(json, DatabaseConfigJson.class);
            if (parsed != null) {
                int pool = parsed.poolSize > 0 ? parsed.poolSize : DatabaseConfig.DEFAULT_POOL_SIZE;
                this.config = new DatabaseConfig(
                    parsed.url != null ? parsed.url : DatabaseConfig.defaults().url(),
                    parsed.username != null ? parsed.username : DatabaseConfig.defaults().username(),
                    parsed.password != null ? parsed.password : "",
                    pool
                );
            }
            return config;
        } catch (IOException e) {
            Kiemhiep.LOGGER.warn("Could not load database config from {}", databasePath, e);
            this.config = DatabaseConfig.defaults();
            return config;
        }
    }

    public DatabaseConfig getConfig() {
        return config != null ? config : DatabaseConfig.defaults();
    }

    public Path getConfigDir() {
        return configDir;
    }

    private void writeDefault() throws IOException {
        Files.createDirectories(configDir);
        DatabaseConfig def = DatabaseConfig.defaults();
        DatabaseConfigJson dto = new DatabaseConfigJson(def.url(), def.username(), def.password(), def.poolSize());
        Files.writeString(databasePath, GSON.toJson(dto));
    }

    @SuppressWarnings("unused")
    private static class DatabaseConfigJson {
        String url;
        String username;
        String password;
        int poolSize;

        DatabaseConfigJson() {}

        DatabaseConfigJson(String url, String username, String password, int poolSize) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.poolSize = poolSize;
        }
    }
}
