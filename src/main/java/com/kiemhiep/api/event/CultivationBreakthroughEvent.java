package com.kiemhiep.api.event;

/**
 * Fired when a player breaks through to the next realm.
 */
public record CultivationBreakthroughEvent(
    long playerId,
    int oldRealm,
    int newRealm
) {}
