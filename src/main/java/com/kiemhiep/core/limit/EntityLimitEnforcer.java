package com.kiemhiep.core.limit;

import com.kiemhiep.core.config.LimitsConfigLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforce entity limits (Rule 5): mob, projectile, npc, other.
 * Module nên gọi tryRecordSpawn(type) một lần trước khi spawn (atomic); hoặc canSpawn + recordSpawn nếu không cần đảm bảo đa luồng.
 */
public class EntityLimitEnforcer {

    private static final ThreadLocal<Boolean> TRY_RECORD_INCREMENTED = ThreadLocal.withInitial(() -> false);

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

    /**
     * Atomic: tăng count nếu còn slot. Gọi một lần thay vì canSpawn + recordSpawn để tránh TOCTOU.
     *
     * @param entityType "mob", "projectile", "npc", "other"
     * @return true nếu còn slot và đã tăng count, false nếu đã đạt limit (không tăng)
     */
    public boolean tryRecordSpawn(String entityType) {
        if (entityType == null) {
            return false;
        }
        TRY_RECORD_INCREMENTED.set(false);
        try {
            currentCountByType.compute(entityType, (k, cur) -> {
                int c = cur != null ? cur : 0;
                int limit = limitsLoader.getLimit(entityType);
                if (c >= limit) {
                    return c;
                }
                TRY_RECORD_INCREMENTED.set(true);
                return c + 1;
            });
            return TRY_RECORD_INCREMENTED.get();
        } finally {
            TRY_RECORD_INCREMENTED.remove();
        }
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
