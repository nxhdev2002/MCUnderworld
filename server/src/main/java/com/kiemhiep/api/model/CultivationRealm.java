package com.kiemhiep.api.model;

/**
 * Value type for cultivation realm and sub-level.
 * Level 1–90 encodes 10 realms × 9 sub-levels: realm = (level-1)/9+1, subLevel = (level-1)%9.
 */
public record CultivationRealm(int realm, int subLevel) {

    public static final int MAX_REALM = 10;
    public static final int SUB_LEVELS_PER_REALM = 9;
    public static final int MAX_LEVEL = MAX_REALM * SUB_LEVELS_PER_REALM; // 90

    public CultivationRealm {
        if (realm < 1 || realm > MAX_REALM) {
            throw new IllegalArgumentException("realm must be 1.." + MAX_REALM);
        }
        if (subLevel < 0 || subLevel >= SUB_LEVELS_PER_REALM) {
            throw new IllegalArgumentException("subLevel must be 0.." + (SUB_LEVELS_PER_REALM - 1));
        }
    }

    /** Convert level (1–90) to realm (1–10) and subLevel (0–8). */
    public static CultivationRealm fromLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException("level must be 1.." + MAX_LEVEL);
        }
        int realm = (level - 1) / SUB_LEVELS_PER_REALM + 1;
        int subLevel = (level - 1) % SUB_LEVELS_PER_REALM;
        return new CultivationRealm(realm, subLevel);
    }

    /** Convert realm and subLevel to level (1–90). */
    public static int toLevel(int realm, int subLevel) {
        if (realm < 1 || realm > MAX_REALM) {
            throw new IllegalArgumentException("realm must be 1.." + MAX_REALM);
        }
        if (subLevel < 0 || subLevel >= SUB_LEVELS_PER_REALM) {
            throw new IllegalArgumentException("subLevel must be 0.." + (SUB_LEVELS_PER_REALM - 1));
        }
        return (realm - 1) * SUB_LEVELS_PER_REALM + subLevel + 1;
    }

    /** Level (1–90) for this realm/subLevel. */
    public int toLevel() {
        return toLevel(realm, subLevel);
    }
}
