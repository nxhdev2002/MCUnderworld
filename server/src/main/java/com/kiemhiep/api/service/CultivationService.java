package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Cultivation;

import java.util.Optional;

/**
 * Service for cultivation (tu luyện): 10 realms, 9 sub-levels per realm, addExp, breakthrough.
 */
public interface CultivationService {

    Optional<Cultivation> get(long playerId);

    /**
     * Load cultivation for player, or create default (level 1, exp 0).
     */
    Cultivation getOrCreate(long playerId);

    /**
     * Add exp; level up sub-levels and/or breakthrough as needed. Fires events.
     */
    void addExp(long playerId, long amount);

    /**
     * Admin: set realm and sub-level directly. Exp is reset to 0.
     */
    void setSubLevel(long playerId, int realm, int subLevel);

    /**
     * Breakthrough to next realm. Only valid when at sub-level 8 (max sub-level in current realm).
     * Returns true if breakthrough succeeded.
     */
    boolean breakthrough(long playerId);

    /**
     * Exp required to advance from current sub-level within the given realm.
     */
    long getExpRequired(int realmLevel);

    /** Realm (1–10) for the given level (1–90). */
    int getRealm(int level);

    /** Sub-level (0–8) within realm for the given level (1–90). */
    int getSubLevel(int level);
}
