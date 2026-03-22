package com.kiemhiep.api.event;

import java.time.Instant;

/**
 * Event fired when a player leaves a sect.
 */
public record SectLeaveEvent(
    long playerId,
    long sectId,
    Instant leftAt
) {}
