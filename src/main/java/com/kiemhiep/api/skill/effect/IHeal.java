package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: heal. Override for formula/range.
 */
public interface IHeal {

    default void applyHeal(SkillContext ctx) {
        // Default: no-op; implementation heals caster or targets
    }
}
