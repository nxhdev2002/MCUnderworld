package com.kiemhiep.api.event;

import java.util.UUID;

/**
 * Base event interface for all KiemHiep events.
 */
public interface Event {
    /**
     * Get the player UUID associated with this event, if any.
     */
    default UUID getPlayerId() {
        return null;
    }
}
