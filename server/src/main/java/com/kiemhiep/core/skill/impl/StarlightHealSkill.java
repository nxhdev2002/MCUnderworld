package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IHeal;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Soraka: Starlight Heal - Restore health to allies in area.
 * inspired by League of Legends: Soraka's Starlight.
 */
public class StarlightHealSkill extends BaseSkill implements IHeal {

    public static final StarlightHealSkill INSTANCE = new StarlightHealSkill();

    private StarlightHealSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applyHeal(SkillContext ctx) {
        // Heal logic: call CombatService to heal targets in radius
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "star_heal");
        }
    }
}
