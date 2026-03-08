package com.kiemhiep.api.module;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Load config file cho từng module (vd. config/kiemhiep/modules/cultivation.json).
 */
public interface ModuleConfigLoader {

    /**
     * Đường dẫn thư mục config (vd. config/kiemhiep).
     */
    Path getConfigDir();

    /**
     * Đọc nội dung file config của module dạng text.
     * Path: config/kiemhiep/modules/{moduleId}.json
     */
    Optional<String> loadModuleConfig(String moduleId);
}
