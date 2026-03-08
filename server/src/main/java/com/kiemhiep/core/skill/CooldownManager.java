package com.kiemhiep.core.skill;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-only cooldown: (playerId, skillId) -> cooldownEndTime (millis).
 * Uses string composite key to avoid allocation in hot path.
 */
public final class CooldownManager {

    private static final String KEY_SEP = ":";

    private static String key(UUID playerId, String skillId) {
        return playerId.toString() + KEY_SEP + skillId;
    }

    private final ConcurrentHashMap<String, Long> cooldownEndByKey = new ConcurrentHashMap<>();

    public boolean isOnCooldown(UUID playerId, String skillId) {
        Long end = cooldownEndByKey.get(key(playerId, skillId));
        return end != null && end > System.currentTimeMillis();
    }

    public void setCooldown(UUID playerId, String skillId, long cooldownEndTimeMillis) {
        cooldownEndByKey.put(key(playerId, skillId), cooldownEndTimeMillis);
    }

    /** Called every 5-10 tick to remove expired entries (optional, to avoid map growth). */
    public void tick(long currentTimeMillis) {
        cooldownEndByKey.entrySet().removeIf(e -> e.getValue() <= currentTimeMillis);
    }

    public long getCooldownEndTimeMillis(UUID playerId, String skillId) {
        Long end = cooldownEndByKey.get(key(playerId, skillId));
        return end != null ? end : 0L;
    }

    public void clear() {
        cooldownEndByKey.clear();
    }
}
