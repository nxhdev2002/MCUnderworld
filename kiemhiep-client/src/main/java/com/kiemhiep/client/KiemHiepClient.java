package com.kiemhiep.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main KiemHiep client entrypoint.
 */
@Environment(EnvType.CLIENT)
public class KiemHiepClient implements ClientModInitializer {
    public static final String MOD_ID = "kiemhiep";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing KiemHiep Client...");

        // Initialize client-side handlers
        // TODO: Register packet handlers
        // TODO: Register screens
        // TODO: Register HUD overlays

        LOGGER.info("KiemHiep Client initialized successfully!");
    }
}
