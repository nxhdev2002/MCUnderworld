package com.kiemhiep;

import com.kiemhiep.api.model.SkillDefinition;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side storage for skill cooldowns. Updated by SkillCooldownReceiver.
 * Maps skillId to cooldown end time in milliseconds.
 */
public final class ClientSkillCooldowns {

    private static final Map<String, Long> cooldownEndTimes = new ConcurrentHashMap<>();

    private ClientSkillCooldowns() {}

    /**
     * Set cooldown for a skill.
     *
     * @param skillId the skill identifier (e.g. "kiemhiep:skill_fireball")
     * @param cooldownEndTimeMillis the timestamp when cooldown ends (System.currentTimeMillis())
     */
    public static void setCooldown(String skillId, long cooldownEndTimeMillis) {
        cooldownEndTimes.put(skillId, cooldownEndTimeMillis);
    }

    /**
     * Check if a skill is on cooldown.
     *
     * @param skillId the skill identifier
     * @return true if the skill is on cooldown
     */
    public static boolean isOnCooldown(String skillId) {
        Long endTime = cooldownEndTimes.get(skillId);
        return endTime != null && endTime > System.currentTimeMillis();
    }

    /**
     * Get the remaining cooldown ratio (0.0 = ready, 1.0 = just started).
     *
     * @param skillId the skill identifier
     * @param cooldownDurationMillis the total cooldown duration in milliseconds
     * @return ratio between 0.0 and 1.0, or 0.0 if not on cooldown
     */
    public static float getRemainingRatio(String skillId, long cooldownDurationMillis) {
        Long endTime = cooldownEndTimes.get(skillId);
        if (endTime == null) return 0.0f;

        long now = System.currentTimeMillis();
        if (endTime <= now) return 0.0f;

        long remaining = endTime - now;
        return Math.min(1.0f, (float) remaining / cooldownDurationMillis);
    }

    /**
     * Get the remaining cooldown in milliseconds.
     *
     * @param skillId the skill identifier
     * @return remaining milliseconds, or 0 if not on cooldown
     */
    public static long getRemainingMillis(String skillId) {
        Long endTime = cooldownEndTimes.get(skillId);
        if (endTime == null) return 0L;

        long now = System.currentTimeMillis();
        return Math.max(0L, endTime - now);
    }

    /**
     * Get the cooldown progress ratio (0.0 = ready, 1.0 = ongoing).
     * Convenience method that reads cooldown duration from ClientSkillDefinitions.
     *
     * @param skillId the skill identifier
     * @return progress ratio between 0.0 and 1.0
     */
    public static float getCooldownProgress(String skillId) {
        Long endTime = cooldownEndTimes.get(skillId);
        if (endTime == null) return 0.0f;

        long now = System.currentTimeMillis();
        if (endTime <= now) return 0.0f;

        // Get default cooldown duration from definitions (5 seconds = 5000ms)
        SkillDefinition definition = ClientSkillDefinitions.getDefinition(skillId);
        long duration = 5000L; // Default 5 seconds
        if (definition != null) {
            // Convert ticks to millis (20 ticks = 1 second)
            duration = (long) definition.cooldownTicks() * 50L;
        }

        long remaining = endTime - now;
        return Math.min(1.0f, (float) remaining / duration);
    }

    /**
     * Tick to remove expired cooldowns. Should be called periodically.
     */
    public static void tick() {
        long now = System.currentTimeMillis();
        cooldownEndTimes.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    /**
     * Clear all cooldowns (for debugging or logout).
     */
    public static void clear() {
        cooldownEndTimes.clear();
    }
}
