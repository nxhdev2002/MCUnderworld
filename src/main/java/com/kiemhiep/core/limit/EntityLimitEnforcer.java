package com.kiemhiep.core.limit;

import com.kiemhiep.core.config.LimitsConfigLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforce entity limits (Rule 5): mob, projectile, npc, other.
 * Module gọi canSpawn(type) trước khi spawn, recordSpawn/recordRemoval khi spawn/remove.
 */
public class EntityLimitEnforcer {

    private final LimitsConfigLoader limitsLoader;
    private final Map<String, Integer> currentCountByType = new ConcurrentHashMap<>();

    public EntityLimitEnforcer(LimitsConfigLoader limitsLoader) {
        this.limitsLoader = limitsLoader;
    }

    /** Reload limits từ config. */
    public void reloadLimits() {
        limitsLoader.loadLimits();
    }

    /**
     * Kiểm tra có thể spawn thêm entity loại type không.
     *
     * @param entityType "mob", "projectile", "npc", "other"
     * @return true nếu count hiện tại < limit
     */
    public boolean canSpawn(String entityType) {
        int limit = limitsLoader.getLimit(entityType);
        int current = currentCountByType.getOrDefault(entityType, 0);
        return current < limit;
    }

    /** Ghi nhận đã spawn một entity (tăng count). */
    public void recordSpawn(String entityType) {
        if (entityType == null) {
            return;
        }
        currentCountByType.merge(entityType, 1, Integer::sum);
    }

    /** Ghi nhận đã remove một entity (giảm count). */
    public void recordRemoval(String entityType) {
        if (entityType == null) {
            return;
        }
        currentCountByType.compute(entityType, (k, cur) -> {
            int next = (cur != null ? cur : 0) - 1;
            return Math.max(0, next);
        });
    }

    /** Số entity hiện tại theo type (để debug/metric). */
    public int getCurrentCount(String entityType) {
        return currentCountByType.getOrDefault(entityType, 0);
    }
}
