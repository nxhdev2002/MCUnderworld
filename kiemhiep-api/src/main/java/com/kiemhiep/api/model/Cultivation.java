package com.kiemhiep.api.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player's cultivation state.
 * Contains realm, sub-level, and experience points.
 */
public class Cultivation {
    private final UUID playerId;
    private final int realmLevel;
    private final int subLevel;
    private final int exp;

    public Cultivation(UUID playerId, int realmLevel, int subLevel, int exp) {
        this.playerId = playerId;
        this.realmLevel = realmLevel;
        this.subLevel = subLevel;
        this.exp = exp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getRealmLevel() {
        return realmLevel;
    }

    public int getSubLevel() {
        return subLevel;
    }

    public int getExp() {
        return exp;
    }

    /**
     * Create a new Cultivation with updated experience.
     */
    public Cultivation withExp(int newExp) {
        return new Cultivation(playerId, realmLevel, subLevel, Math.max(0, newExp));
    }

    /**
     * Create a new Cultivation with updated sub-level.
     */
    public Cultivation withSubLevel(int newSubLevel) {
        return new Cultivation(playerId, realmLevel, newSubLevel, exp);
    }

    /**
     * Create a new Cultivation with breakthrough to new realm.
     */
    public Cultivation breakthrough(int newRealmLevel) {
        return new Cultivation(playerId, newRealmLevel, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cultivation that = (Cultivation) o;
        return realmLevel == that.realmLevel &&
                subLevel == that.subLevel &&
                exp == that.exp &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, realmLevel, subLevel, exp);
    }

    @Override
    public String toString() {
        return "Cultivation{" +
                "playerId=" + playerId +
                ", realmLevel=" + realmLevel +
                ", subLevel=" + subLevel +
                ", exp=" + exp +
                '}';
    }
}
