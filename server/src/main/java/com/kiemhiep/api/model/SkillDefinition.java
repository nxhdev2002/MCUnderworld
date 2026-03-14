package com.kiemhiep.api.model;

import java.time.Instant;

/**
 * Catalog definition for a skill. Stored in kiemhiep_skill_definitions.
 * behavior_id maps to SkillRegistry for the runtime class.
 */
public record SkillDefinition(
    long id,
    String skillId,
    String behaviorId,
    String itemId,
    String name,
    int manaCost,
    int cooldownTicks,
    double maxRadius,
    boolean isAoe,
    boolean isMelee,
    String skillType,
    int castTimeTicks,
    boolean castCancellable,
    boolean consumable,
    String elementalType,
    String parentSkillId,
    int evolutionLevel,
    int level,
    Instant createdAt,
    Instant updatedAt
) {
    public static final double DEFAULT_MAX_RADIUS = 8.0;
    public static final int DEFAULT_COOLDOWN_TICKS = 40;
    public static final String DEFAULT_ELEMENTAL_TYPE = "NONE";
    public static final int DEFAULT_LEVEL = 1;

    public SkillDefinition {
        if (elementalType == null || elementalType.isEmpty()) {
            elementalType = DEFAULT_ELEMENTAL_TYPE;
        }
        if (level <= 0) {
            level = DEFAULT_LEVEL;
        }
    }
}
