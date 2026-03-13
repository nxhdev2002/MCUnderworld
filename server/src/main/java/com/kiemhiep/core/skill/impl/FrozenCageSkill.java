package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Frozen Cage - Create a cage of ice that traps enemies.
 * inspired by League of Legends: Frost Oracle.
 */
public class FrozenCageSkill extends BaseSkill implements ISummon {

    public static final FrozenCageSkill INSTANCE = new FrozenCageSkill();

    private FrozenCageSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "frozen_cage");
        }
    }
}
