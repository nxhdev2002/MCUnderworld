package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: summon (spawn entity or fake entity/particle). Prefer particle/fake (Rule 3).
 */
public interface ISummon {

    default void applySummon(SkillContext ctx) {
        // Default: no-op
    }
}
