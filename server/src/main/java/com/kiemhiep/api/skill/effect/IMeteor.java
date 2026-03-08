package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: meteor (thiên thạch). Skill implementing this can play meteor effect at target.
 * Override applyMeteor to call ctx.getEffectRunner().playEffect(ctx, "meteor") when non-null.
 */
public interface IMeteor {

    /**
     * Apply meteor effect at ctx.getOrigin(). Override to trigger visual/particles via effect runner.
     */
    default void applyMeteor(SkillContext ctx) {
        // Default: no-op; override to call getEffectRunner().playEffect(ctx, "meteor")
    }
}
