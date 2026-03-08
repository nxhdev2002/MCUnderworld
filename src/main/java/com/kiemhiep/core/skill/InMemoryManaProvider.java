package com.kiemhiep.core.skill;

import com.kiemhiep.api.skill.ManaProvider;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory mana pool per player. Use until cultivation/persistence provides mana.
 * Default 100 mana per player; no max cap in this impl.
 */
public final class InMemoryManaProvider implements ManaProvider {

    private static final int DEFAULT_MANA = 100;

    private final ConcurrentHashMap<UUID, Integer> manaByPlayer = new ConcurrentHashMap<>();

    @Override
    public int getCurrentMana(UUID playerId) {
        return manaByPlayer.getOrDefault(playerId, DEFAULT_MANA);
    }

    @Override
    public boolean consumeMana(UUID playerId, int amount) {
        if (amount <= 0) return true;
        final int[] consumed = { 0 }; // 1 = did consume, -1 = insufficient
        manaByPlayer.compute(playerId, (k, v) -> {
            int cur = v != null ? v : DEFAULT_MANA;
            if (cur < amount) {
                consumed[0] = -1;
                return cur;
            }
            consumed[0] = 1;
            return cur - amount;
        });
        return consumed[0] == 1;
    }
}
