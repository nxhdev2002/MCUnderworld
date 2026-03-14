package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Miss Fortune: Make It Rain - Rain arrows from the sky.
 * inspired by League of Legends: Miss Fortune's Make It Rain.
 */
public class RainArrowsSkill extends BaseSkill implements ISummon {

    public static final RainArrowsSkill INSTANCE = new RainArrowsSkill();

    private RainArrowsSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "rain_arrows");
        }
    }
}
