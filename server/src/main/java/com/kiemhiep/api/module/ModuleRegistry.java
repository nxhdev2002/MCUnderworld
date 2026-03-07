package com.kiemhiep.api.module;

import java.util.List;
import java.util.Optional;

/**
 * Quản lý danh sách module và trạng thái enabled.
 */
public interface ModuleRegistry {

    /** Đăng ký module (gọi trước load). */
    void register(KiemHiepModule module);

    /** Tất cả module đã đăng ký. */
    List<KiemHiepModule> getAll();

    Optional<KiemHiepModule> get(String id);

    boolean isEnabled(String id);

    void setEnabled(String id, boolean enabled);

    /** Reload config và áp dụng enable/disable theo config. */
    void reloadFromConfig();
}
