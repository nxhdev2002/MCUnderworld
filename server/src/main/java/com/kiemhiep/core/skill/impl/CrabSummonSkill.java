package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Crab Summon - Summon a rock crab to fight for you.
 * inspired by League of Legends: Crab Grandma.
 */
public class CrabSummonSkill extends BaseSkill implements ISummon {

    public static final CrabSummonSkill INSTANCE = new CrabSummonSkill();

    private CrabSummonSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "summon_crab");
        }
    }
}
