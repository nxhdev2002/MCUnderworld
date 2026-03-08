package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: area damage. Skill implementing this damages entities in radius.
 * Override for formula/radius. Actual damage calculation may delegate to Combat (Phase 08).
 */
public interface IAreaDamage {

    default void applyAreaDamage(SkillContext ctx) {
        // Default: no-op; implementation or CombatService applies damage to ctx.getTargetsInRadius()
    }
}
