package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Dark Rift - Open a rift to the void dimension.
 * inspired by League of Legends: Void rift abilities.
 */
public class DarkRiftSkill extends BaseSkill implements ISummon {

    public static final DarkRiftSkill INSTANCE = new DarkRiftSkill();

    private DarkRiftSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "dark_rift");
        }
    }
}
