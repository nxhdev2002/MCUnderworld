package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Brand: Wolf Beast - Summon a beast wolf companion.
 * inspired by League of Legends: Brand's Pyre.
 */
public class BeastWolfSkill extends BaseSkill implements ISummon {

    public static final BeastWolfSkill INSTANCE = new BeastWolfSkill();

    private BeastWolfSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "beast_wolf");
        }
    }
}
