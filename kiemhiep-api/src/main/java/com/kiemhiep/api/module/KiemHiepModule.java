package com.kiemhiep.api.module;

import java.util.List;

/**
 * Interface for KiemHiep modules.
 * All feature modules must implement this interface.
 */
public interface KiemHiepModule {

    /**
     * Get the module's unique ID.
     */
    String getId();

    /**
     * Get the module's display name.
     */
    String getName();

    /**
     * Get the module's version.
     */
    String getVersion();

    /**
     * Called when the module is loaded.
     * Use this for early initialization.
     */
    void onLoad();

    /**
     * Called when the module is enabled.
     *
     * @param injector The dependency injection injector
     */
    void onEnable(Object injector);

    /**
     * Called when the module is disabled.
     * Use this for cleanup.
     */
    void onDisable();

    /**
     * Get the list of module classes this module depends on.
     *
     * @return List of dependent module classes
     */
    List<Class<?>> getDependencies();

    /**
     * Check if the module is enabled.
     */
    boolean isEnabled();

    /**
     * Set the module's enabled state.
     */
    void setEnabled(boolean enabled);
}
