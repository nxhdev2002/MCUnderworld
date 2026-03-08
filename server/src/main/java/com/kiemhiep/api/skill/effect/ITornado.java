package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: tornado (lốc xoáy). Skill implementing this can play tornado effect at target.
 * Override applyTornado to call ctx.getEffectRunner().playEffect(ctx, "tornado") when non-null.
 */
public interface ITornado {

    /**
     * Apply tornado effect at ctx.getOrigin(). Override to trigger visual/particles via effect runner.
     */
    default void applyTornado(SkillContext ctx) {
        // Default: no-op; override to call getEffectRunner().playEffect(ctx, "tornado")
    }
}
