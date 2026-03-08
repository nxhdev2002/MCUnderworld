package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: stun. Skill implementing this can apply stun to targets.
 * Override applyStun for custom duration/effect.
 */
public interface IStunable {

    /**
     * Apply stun effect to valid targets from context. Override for custom behavior.
     */
    default void applyStun(SkillContext ctx) {
        // Default: no-op; implementation applies status to targets
    }
}
