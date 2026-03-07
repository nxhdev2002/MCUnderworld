package com.kiemhiep.core.module;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.core.config.ConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleRegistryImpl implements ModuleRegistry {

    private final List<KiemHiepModule> modules = new ArrayList<>();
    private final ConcurrentHashMap<String, Boolean> enabled = new ConcurrentHashMap<>();
    private final ConfigLoader configLoader;

    public ModuleRegistryImpl(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public void register(KiemHiepModule module) {
        modules.add(module);
        enabled.put(module.getId(), configLoader.isModuleEnabled(module.getId()));
    }

    @Override
    public List<KiemHiepModule> getAll() {
        return List.copyOf(modules);
    }

    @Override
    public Optional<KiemHiepModule> get(String id) {
        return modules.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    @Override
    public boolean isEnabled(String id) {
        return Boolean.TRUE.equals(enabled.get(id));
    }

    @Override
    public void setEnabled(String id, boolean value) {
        enabled.put(id, value);
        get(id).ifPresent(m -> m.setEnabled(value));
    }

    @Override
    public void reloadFromConfig() {
        configLoader.loadModulesConfig();
        for (KiemHiepModule m : modules) {
            boolean on = configLoader.isModuleEnabled(m.getId());
            enabled.put(m.getId(), on);
            m.setEnabled(on);
        }
    }
}
