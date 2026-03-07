package com.kiemhiep.api.model;

import java.util.Objects;

/**
 * Represents a cultivation realm/level in the cultivation system.
 * Each realm has a level, name, and sub-levels (0-8).
 */
public class CultivationRealm {
    private final int level;
    private final String name;
    private final String vietnameseName;
    private final int maxSubLevel;

    public CultivationRealm(int level, String name, String vietnameseName, int maxSubLevel) {
        this.level = level;
        this.name = name;
        this.vietnameseName = vietnameseName;
        this.maxSubLevel = maxSubLevel;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public int getMaxSubLevel() {
        return maxSubLevel;
    }

    /**
     * Check if this realm can breakthrough to the next realm.
     */
    public boolean canBreakthrough() {
        return level < 9; // Can breakthrough if not at max realm
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CultivationRealm that = (CultivationRealm) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return "CultivationRealm{" +
                "level=" + level +
                ", name='" + name + '\'' +
                ", vietnameseName='" + vietnameseName + '\'' +
                '}';
    }
}
