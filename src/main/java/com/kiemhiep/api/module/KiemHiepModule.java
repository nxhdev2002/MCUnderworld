package com.kiemhiep.api.module;

import java.util.List;

/**
 * Interface cho mỗi feature module. Module đăng ký với ModuleRegistry;
 * enable/disable theo config và dependency.
 */
public interface KiemHiepModule {

    /** ID duy nhất, ví dụ "cultivation", "economy". */
    String getId();

    /** Tên hiển thị. */
    String getName();

    /** Các module phải enable trước module này. */
    List<String> getDependencies();

    /** Gọi một lần khi mod init: bind service, đăng ký repo. Không đăng ký Fabric event/command ở đây. */
    void onLoad(ModuleContext ctx);

    /** Gọi khi module được bật: đăng ký Fabric events, commands. */
    void onEnable(ModuleContext ctx);

    /** Gọi khi module bị tắt. */
    void onDisable();

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
