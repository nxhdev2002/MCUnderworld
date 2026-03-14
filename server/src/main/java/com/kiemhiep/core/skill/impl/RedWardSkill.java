package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Red Ward - Summon a red ward for warding and vision control.
 * inspired by League of Legends: wards.
 */
public class RedWardSkill extends BaseSkill implements ISummon {

    public static final RedWardSkill INSTANCE = new RedWardSkill();

    private RedWardSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "ward_red");
        }
    }
}
