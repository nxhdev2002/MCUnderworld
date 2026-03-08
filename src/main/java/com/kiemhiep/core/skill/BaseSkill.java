package com.kiemhiep.core.skill;

import com.kiemhiep.api.skill.ISkill;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.*;

/**
 * Abstract base for skill classes. Subclass overrides onExecute and/or effect methods.
 * Engine or subclass may call applyThunder, applyStun, etc. when this implements those interfaces.
 */
public abstract class BaseSkill implements ISkill {

    @Override
    public final void execute(SkillContext ctx) {
        onExecute(ctx);
        // Dispatch to effect interfaces if this skill implements them
        if (this instanceof IThunder t) t.applyThunder(ctx);
        if (this instanceof IStunable s) s.applyStun(ctx);
        if (this instanceof ISilentable s) s.applySilence(ctx);
        if (this instanceof IHeal h) h.applyHeal(ctx);
        if (this instanceof IAreaDamage a) a.applyAreaDamage(ctx);
        if (this instanceof ISingleTargetDamage s) s.applySingleTargetDamage(ctx);
        if (this instanceof ISummon s) s.applySummon(ctx);
        if (this instanceof IShield s) s.applyShield(ctx);
    }

    /**
     * Override in subclass for main skill logic (before effect dispatch).
     */
    protected void onExecute(SkillContext ctx) {
        // Optional override
    }
}
