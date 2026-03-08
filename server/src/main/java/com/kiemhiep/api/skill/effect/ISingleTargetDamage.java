package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: single-target damage. Override for formula.
 */
public interface ISingleTargetDamage {

    default void applySingleTargetDamage(SkillContext ctx) {
        // Default: no-op
    }
}
