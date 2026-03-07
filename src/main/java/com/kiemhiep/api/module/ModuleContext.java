package com.kiemhiep.api.module;

/**
 * Context truyền vào onLoad và onEnable của module.
 * Phase 02 sẽ bổ sung EventDispatcher, PlatformProvider, Injector.
 */
public interface ModuleContext {

    /** ID của module đang được load/enable. */
    String getModuleId();

    /** Registry để module kiểm tra trạng thái module khác hoặc reload. */
    ModuleRegistry getModuleRegistry();

    /** Config loader để đọc file config của module (vd. config/kiemhiep/modules/cultivation.json). */
    ModuleConfigLoader getModuleConfigLoader();
}
