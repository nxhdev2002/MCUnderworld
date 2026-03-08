package com.kiemhiep.api.platform;

import java.util.UUID;

/**
 * Adapter cho entity (mob, projectile, NPC, other).
 * API-agnostic; implementation (vd. Fabric) wrap Entity thực tế.
 */
public interface EntityAdapter {

    /** UUID của entity. */
    UUID getUniqueId();

    /** Vị trí hiện tại. */
    Location getLocation();

    /**
     * Loại entity để áp limit: "mob", "projectile", "npc", "other".
     */
    String getType();

    /** World chứa entity. */
    WorldAdapter getWorld();
}
