package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Bear Summon - Summon an Ursine Guardian.
 * inspired by League of Legends: Bear form abilities.
 */
public class BearSummonSkill extends BaseSkill implements ISummon {

    public static final BearSummonSkill INSTANCE = new BearSummonSkill();

    private BearSummonSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "bear_summon");
        }
    }
}
