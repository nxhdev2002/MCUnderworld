package com.kiemhiep.core.skill;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-only cooldown: (playerId, skillId) -> cooldownEndTime (millis).
 * Updated every 5-10 tick; client only displays cooldown (Rule 6).
 */
public final class CooldownManager {

    private static final class Key {
        final UUID playerId;
        final String skillId;

        Key(UUID playerId, String skillId) {
            this.playerId = playerId;
            this.skillId = skillId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return playerId.equals(key.playerId) && skillId.equals(key.skillId);
        }

        @Override
        public int hashCode() {
            return 31 * playerId.hashCode() + skillId.hashCode();
        }
    }

    private final ConcurrentHashMap<Key, Long> cooldownEndByKey = new ConcurrentHashMap<>();

    public boolean isOnCooldown(UUID playerId, String skillId) {
        Long end = cooldownEndByKey.get(new Key(playerId, skillId));
        return end != null && end > System.currentTimeMillis();
    }

    public void setCooldown(UUID playerId, String skillId, long cooldownEndTimeMillis) {
        cooldownEndByKey.put(new Key(playerId, skillId), cooldownEndTimeMillis);
    }

    /** Called every 5-10 tick to remove expired entries (optional, to avoid map growth). */
    public void tick(long currentTimeMillis) {
        cooldownEndByKey.entrySet().removeIf(e -> e.getValue() <= currentTimeMillis);
    }

    public long getCooldownEndTimeMillis(UUID playerId, String skillId) {
        Long end = cooldownEndByKey.get(new Key(playerId, skillId));
        return end != null ? end : 0L;
    }

    public void clear() {
        cooldownEndByKey.clear();
    }
}
