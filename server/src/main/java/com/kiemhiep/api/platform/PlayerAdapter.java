package com.kiemhiep.api.platform;

import com.kiemhiep.platform.FabricPlayerAdapter;
import java.util.UUID;

/**
 * Adapter cho player.
 * API-agnostic; implementation wrap ServerPlayerEntity (Fabric).
 */
public interface PlayerAdapter extends EntityAdapter {

    @Override
    UUID getUniqueId();

    /** Tên hiển thị của player. */
    String getName();

    @Override
    Location getLocation();

    @Override
    WorldAdapter getWorld();

    /** level của player. */
    default int getLevel() {
        if (this instanceof FabricPlayerAdapter fabric) {
            return fabric.getPlayer().experienceLevel;
        }
        return 1;
    }
}
