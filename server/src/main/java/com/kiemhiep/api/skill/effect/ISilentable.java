package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: silence (disable target skills). Override for custom behavior.
 */
public interface ISilentable {

    default void applySilence(SkillContext ctx) {
        // Default: no-op
    }
}
