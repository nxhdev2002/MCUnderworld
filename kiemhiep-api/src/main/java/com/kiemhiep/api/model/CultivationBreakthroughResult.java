package com.kiemhiep.api.model;

/**
 * Result of a cultivation breakthrough attempt.
 */
public class CultivationBreakthroughResult {
    private final boolean success;
    private final CultivationRealm oldRealm;
    private final CultivationRealm newRealm;

    public CultivationBreakthroughResult(boolean success, CultivationRealm oldRealm, CultivationRealm newRealm) {
        this.success = success;
        this.oldRealm = oldRealm;
        this.newRealm = newRealm;
    }

    public boolean isSuccess() {
        return success;
    }

    public CultivationRealm getOldRealm() {
        return oldRealm;
    }

    public CultivationRealm getNewRealm() {
        return newRealm;
    }

    @Override
    public String toString() {
        if (success) {
            return "Breakthrough successful: " + oldRealm.getVietnameseName() + " -> " + newRealm.getVietnameseName();
        } else {
            return "Breakthrough failed at " + oldRealm.getVietnameseName();
        }
    }
}
