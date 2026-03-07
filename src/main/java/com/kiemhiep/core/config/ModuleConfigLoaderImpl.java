package com.kiemhiep.core.config;

import com.kiemhiep.api.module.ModuleConfigLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ModuleConfigLoaderImpl implements ModuleConfigLoader {

    private final ConfigLoader configLoader;

    public ModuleConfigLoaderImpl(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public Path getConfigDir() {
        return configLoader.getConfigDir();
    }

    @Override
    public Optional<String> loadModuleConfig(String moduleId) {
        Path file = configLoader.getConfigDir().resolve("modules").resolve(moduleId + ".json");
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(file));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
