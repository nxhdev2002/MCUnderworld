package com.kiemhiep.api.event;

import com.kiemhiep.api.model.CultivationBreakthroughResult;

import java.util.UUID;

/**
 * Event fired when a player attempts a cultivation breakthrough.
 */
public class CultivationBreakthroughEvent implements Event {
    private final UUID playerId;
    private final CultivationBreakthroughResult result;

    public CultivationBreakthroughEvent(UUID playerId, CultivationBreakthroughResult result) {
        this.playerId = playerId;
        this.result = result;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    public CultivationBreakthroughResult getResult() {
        return result;
    }

    public boolean isSuccess() {
        return result.isSuccess();
    }
}
