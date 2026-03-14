package com.kiemhiep;

import com.kiemhiep.network.PlayerStatsPayload;

/**
 * Client-side holder for level and mana received from server. Updated by PlayerStatsReceiver.
 */
public final class ClientPlayerStats {

    private static volatile int level = 1;
    private static volatile int currentMana = 100;
    private static volatile int maxMana = 100;

    private ClientPlayerStats() {}

    public static void set(PlayerStatsPayload payload) {
        level = payload.level();
        currentMana = payload.currentMana();
        maxMana = payload.maxMana();
    }

    public static int getLevel() {
        return level;
    }

    public static int getCurrentMana() {
        return currentMana;
    }

    public static int getMaxMana() {
        return maxMana;
    }
}
