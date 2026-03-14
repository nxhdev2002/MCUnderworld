package com.kiemhiep.api.model;

import java.util.List;

/**
 * Client-side skill definition data for displaying tooltips.
 * Maps item ID to skill metadata.
 */
public record SkillDefinition(
    String skillId,
    String itemId,
    String name,
    int manaCost,
    int cooldownTicks,
    double maxRadius,
    boolean isAoe,
    boolean isMelee,
    String skillType,
    int castTimeTicks,
    boolean consumable,
    String elementalType,
    List<String> effects
) {
    public static final double DEFAULT_MAX_RADIUS = 8.0;
    public static final int DEFAULT_COOLDOWN_TICKS = 40;
    public static final String DEFAULT_ELEMENTAL_TYPE = "none";

    public SkillDefinition {
        if (elementalType == null || elementalType.isEmpty()) {
            elementalType = DEFAULT_ELEMENTAL_TYPE;
        }
    }

    /**
     * Get display string for cooldown in seconds.
     */
    public String getCooldownDisplay() {
        float seconds = cooldownTicks / 20.0f;
        if (seconds == (int) seconds) {
            return String.valueOf((int) seconds) + "s";
        }
        return String.format("%.1fs", seconds);
    }

    /**
     * Get display string for radius.
     */
    public String getRadiusDisplay() {
        if (maxRadius <= 0) {
            return "N/A";
        }
        return String.valueOf(maxRadius);
    }

    /**
     * Get display string for cast time in seconds.
     */
    public String getCastTimeDisplay() {
        if (castTimeTicks <= 0) {
            return "Instant";
        }
        float seconds = castTimeTicks / 20.0f;
        if (seconds == (int) seconds) {
            return String.valueOf((int) seconds) + "s";
        }
        return String.format("%.1fs", seconds);
    }
}