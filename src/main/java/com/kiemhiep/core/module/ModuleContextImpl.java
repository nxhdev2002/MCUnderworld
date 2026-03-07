package com.kiemhiep.core.module;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.module.ModuleConfigLoader;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.api.platform.PlatformProvider;

public class ModuleContextImpl implements ModuleContext {

    private final String moduleId;
    private final ModuleRegistry registry;
    private final ModuleConfigLoader moduleConfigLoader;
    private final EventDispatcher eventDispatcher;
    private final PlatformProvider platformProvider;

    public ModuleContextImpl(String moduleId, ModuleRegistry registry, ModuleConfigLoader moduleConfigLoader,
                             EventDispatcher eventDispatcher, PlatformProvider platformProvider) {
        this.moduleId = moduleId;
        this.registry = registry;
        this.moduleConfigLoader = moduleConfigLoader;
        this.eventDispatcher = eventDispatcher;
        this.platformProvider = platformProvider;
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

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public PlatformProvider getPlatformProvider() {
        return platformProvider;
    }
}
