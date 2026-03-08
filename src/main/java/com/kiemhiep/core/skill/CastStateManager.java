package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.SkillDefinition;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks active casts: (playerId -> CastEntry). Tick every 5-10 to complete or cancel.
 */
public final class CastStateManager {

    public static final class CastEntry {
        public final String skillId;
        public final long castEndTick;
        public final SkillDefinition definition;

        public CastEntry(String skillId, long castEndTick, SkillDefinition definition) {
            this.skillId = skillId;
            this.castEndTick = castEndTick;
            this.definition = definition;
        }
    }

    private final ConcurrentHashMap<UUID, CastEntry> castByPlayer = new ConcurrentHashMap<>();

    public boolean isCasting(UUID playerId) {
        return castByPlayer.containsKey(playerId);
    }

    public void startCast(UUID playerId, String skillId, long castEndTick, SkillDefinition definition) {
        castByPlayer.put(playerId, new CastEntry(skillId, castEndTick, definition));
    }

    public CastEntry getCast(UUID playerId) {
        return castByPlayer.get(playerId);
    }

    public CastEntry removeCast(UUID playerId) {
        return castByPlayer.remove(playerId);
    }

    public void tick(long currentServerTick, CastCompletionHandler onComplete) {
        castByPlayer.entrySet().removeIf(e -> {
            if (e.getValue().castEndTick <= currentServerTick) {
                onComplete.onCastComplete(e.getKey(), e.getValue());
                return true;
            }
            return false;
        });
    }

    @FunctionalInterface
    public interface CastCompletionHandler {
        void onCastComplete(UUID playerId, CastEntry entry);
    }

    public void clear() {
        castByPlayer.clear();
    }
}
