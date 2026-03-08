package com.kiemhiep.api.platform;

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
}
