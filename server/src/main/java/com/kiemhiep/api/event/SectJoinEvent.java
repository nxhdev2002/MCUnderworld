package com.kiemhiep.api.event;

import java.time.Instant;

/**
 * Event fired when a player joins a sect.
 */
public record SectJoinEvent(
    long playerId,
    long sectId,
    Instant joinedAt
) {}
