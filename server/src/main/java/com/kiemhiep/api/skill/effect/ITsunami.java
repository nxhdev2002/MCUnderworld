package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: tsunami (sóng thần). Skill implementing this can play tsunami effect at target.
 * Override applyTsunami to call ctx.getEffectRunner().playEffect(ctx, "tsunami") when non-null.
 */
public interface ITsunami {

    /**
     * Apply tsunami effect at ctx.getOrigin(). Override to trigger visual/particles via effect runner.
     */
    default void applyTsunami(SkillContext ctx) {
        // Default: no-op; override to call getEffectRunner().playEffect(ctx, "tsunami")
    }
}
