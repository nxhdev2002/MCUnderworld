package com.kiemhiep.core.module;

import com.kiemhiep.Kiemhiep;
import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.core.config.ConfigLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gọi onLoad cho tất cả module (một lần), sau đó enable/disable theo config và dependency.
 */
public class ModuleLoader {

    private final ModuleRegistry registry;
    private final ConfigLoader configLoader;
    private final ModuleContextFactory contextFactory;
    private boolean loadCalled;

    public ModuleLoader(ModuleRegistry registry, ConfigLoader configLoader, ModuleContextFactory contextFactory) {
        this.registry = registry;
        this.configLoader = configLoader;
        this.contextFactory = contextFactory;
    }

    /** Gọi onLoad cho tất cả module (chỉ gọi một lần). */
    public void loadAll() {
        if (loadCalled) {
            return;
        }
        loadCalled = true;
        for (KiemHiepModule module : registry.getAll()) {
            try {
                ModuleContext ctx = contextFactory.create(module.getId());
                module.onLoad(ctx);
                Kiemhiep.LOGGER.info("Module loaded: {}", module.getId());
            } catch (Exception e) {
                Kiemhiep.LOGGER.error("Failed to load module {}", module.getId(), e);
            }
        }
    }

    /** Bật một module (chỉ gọi onEnable, không đọc lại config). */
    public void enableModule(String id) {
        registry.get(id).filter(m -> !registry.isEnabled(id)).ifPresent(module -> {
            try {
                ModuleContext ctx = contextFactory.create(module.getId());
                module.onEnable(ctx);
                module.setEnabled(true);
                registry.setEnabled(id, true);
                Kiemhiep.LOGGER.info("Module enabled: {}", id);
            } catch (Exception e) {
                Kiemhiep.LOGGER.error("Failed to enable module {}", id, e);
            }
        });
    }

    /** Tắt một module (chỉ gọi onDisable). */
    public void disableModule(String id) {
        registry.get(id).filter(m -> registry.isEnabled(m.getId())).ifPresent(module -> {
            try {
                module.onDisable();
                module.setEnabled(false);
                registry.setEnabled(id, false);
                Kiemhiep.LOGGER.info("Module disabled: {}", id);
            } catch (Exception e) {
                Kiemhiep.LOGGER.error("Failed to disable module {}", id, e);
            }
        });
    }

    /** Reload config rồi enable/disable từng module theo thứ tự dependency. */
    public void applyConfig() {
        registry.reloadFromConfig();
        List<KiemHiepModule> order = topologicalOrder(registry.getAll());
        for (KiemHiepModule module : order) {
            boolean shouldEnable = registry.isEnabled(module.getId());
            try {
                if (shouldEnable) {
                    ModuleContext ctx = contextFactory.create(module.getId());
                    module.onEnable(ctx);
                    module.setEnabled(true);
                    Kiemhiep.LOGGER.info("Module enabled: {}", module.getId());
                } else {
                    module.onDisable();
                    module.setEnabled(false);
                }
            } catch (Exception e) {
                Kiemhiep.LOGGER.error("Failed to apply state for module {}", module.getId(), e);
            }
        }
    }

    /** Enable: thứ tự dependency (dependency trước). Disable: ngược lại. */
    private List<KiemHiepModule> topologicalOrder(List<KiemHiepModule> modules) {
        Set<String> ids = new LinkedHashSet<>();
        for (KiemHiepModule m : modules) {
            addWithDeps(m, registry, ids);
        }
        List<KiemHiepModule> result = new ArrayList<>();
        for (String id : ids) {
            registry.get(id).ifPresent(result::add);
        }
        return result;
    }

    private static void addWithDeps(KiemHiepModule module, ModuleRegistry registry, Set<String> out) {
        for (String depId : module.getDependencies()) {
            registry.get(depId).ifPresent(dep -> addWithDeps(dep, registry, out));
        }
        out.add(module.getId());
    }

    @FunctionalInterface
    public interface ModuleContextFactory {
        ModuleContext create(String moduleId);
    }
}
