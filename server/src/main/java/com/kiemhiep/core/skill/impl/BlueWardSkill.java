package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Blue Ward - Summon a blue ward for vision.
 * inspired by League of Legends: wards.
 */
public class BlueWardSkill extends BaseSkill implements ISummon {

    public static final BlueWardSkill INSTANCE = new BlueWardSkill();

    private BlueWardSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "ward_blue");
        }
    }
}
