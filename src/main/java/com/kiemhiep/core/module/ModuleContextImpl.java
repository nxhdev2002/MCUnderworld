package com.kiemhiep.core.module;

import com.kiemhiep.api.module.ModuleConfigLoader;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;

public class ModuleContextImpl implements ModuleContext {

    private final String moduleId;
    private final ModuleRegistry registry;
    private final ModuleConfigLoader moduleConfigLoader;

    public ModuleContextImpl(String moduleId, ModuleRegistry registry, ModuleConfigLoader moduleConfigLoader) {
        this.moduleId = moduleId;
        this.registry = registry;
        this.moduleConfigLoader = moduleConfigLoader;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public ModuleRegistry getModuleRegistry() {
        return registry;
    }

    @Override
    public ModuleConfigLoader getModuleConfigLoader() {
        return moduleConfigLoader;
    }
}
