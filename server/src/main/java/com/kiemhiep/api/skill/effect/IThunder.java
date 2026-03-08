package com.kiemhiep.api.skill.effect;

import com.kiemhiep.api.skill.SkillContext;

/**
 * Effect interface: thunder/lightning. Skill implementing this gets default thunder at target;
 * can override applyThunder for custom particle/radius/delay.
 */
public interface IThunder {

    /**
     * Apply thunder effect (e.g. spawn lightning at target). Default: spawn at ctx.getOrigin().
     * Override in skill class to customize.
     */
    default void applyThunder(SkillContext ctx) {
        // Default: no-op; implementation can spawn particle or send S2C for client lightning
        // Subclass overrides to add behavior
    }
}
