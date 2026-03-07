package com.kiemhiep.api.event;

import com.kiemhiep.api.model.Cultivation;

import java.util.UUID;

/**
 * Event fired when a player's cultivation sub-level increases.
 */
public class CultivationSubLevelUpEvent implements Event {
    private final UUID playerId;
    private final int oldSubLevel;
    private final int newSubLevel;
    private final int realmLevel;

    public CultivationSubLevelUpEvent(UUID playerId, int oldSubLevel, int newSubLevel, int realmLevel) {
        this.playerId = playerId;
        this.oldSubLevel = oldSubLevel;
        this.newSubLevel = newSubLevel;
        this.realmLevel = realmLevel;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    public int getOldSubLevel() {
        return oldSubLevel;
    }

    public int getNewSubLevel() {
        return newSubLevel;
    }

    public int getRealmLevel() {
        return realmLevel;
    }
}
