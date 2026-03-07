package com.kiemhiep.api.event;

/**
 * Fired when a player's cultivation sub-level increases (within or across realms).
 */
public record CultivationSubLevelUpEvent(
    long playerId,
    int oldLevel,
    int newLevel
) {}
