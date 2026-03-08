package com.kiemhiep.api.skill;

import java.util.UUID;

/**
 * Provides current mana and consumption for a player. Used by SkillManager to validate and deduct mana.
 */
public interface ManaProvider {

    /** Current mana for the player (≥ 0). */
    int getCurrentMana(UUID playerId);

    /**
     * Consume mana if available. No-op if amount ≤ 0.
     *
     * @return true if amount was available and has been deducted, false if insufficient (no change)
     */
    boolean consumeMana(UUID playerId, int amount);
}
