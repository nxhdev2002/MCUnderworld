package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: shield/barrier (absorb damage). Override for amount/duration.
 */
public interface IShield {

    default void applyShield(SkillContext ctx) {
        // Default: no-op
    }
}
