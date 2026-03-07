package com.kiemhiep.api.module;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.platform.PlatformProvider;

/**
 * Context truyền vào onLoad và onEnable của module.
 */
public interface ModuleContext {

    /** ID của module đang được load/enable. */
    String getModuleId();

    /** Registry để module kiểm tra trạng thái module khác hoặc reload. */
    ModuleRegistry getModuleRegistry();

    /** Config loader để đọc file config của module (vd. config/kiemhiep/modules/cultivation.json). */
    ModuleConfigLoader getModuleConfigLoader();

    /** Event dispatcher để fire/register events. */
    EventDispatcher getEventDispatcher();

    /** Platform provider để lấy player, world, entity (api-agnostic). */
    PlatformProvider getPlatformProvider();
}
