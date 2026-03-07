package com.kiemhiep.core.module;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;

import java.util.ArrayList;
import java.util.List;

/** Module giả để test: ghi lại onLoad/onEnable/onDisable. */
public class FakeModule implements KiemHiepModule {

    private final String id;
    private final List<String> dependencies;
    private boolean enabled;
    private int onLoadCount;
    private int onEnableCount;
    private int onDisableCount;

    public FakeModule(String id, List<String> dependencies) {
        this.id = id;
        this.dependencies = dependencies != null ? dependencies : List.of();
        this.enabled = false;
    }

    public FakeModule(String id) {
        this(id, List.of());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Fake-" + id;
    }

    @Override
    public List<String> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    @Override
    public void onLoad(ModuleContext ctx) {
        onLoadCount++;
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        onEnableCount++;
    }

    @Override
    public void onDisable() {
        onDisableCount++;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOnLoadCount() {
        return onLoadCount;
    }

    public int getOnEnableCount() {
        return onEnableCount;
    }

    public int getOnDisableCount() {
        return onDisableCount;
    }
}
