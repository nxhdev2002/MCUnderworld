package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Electric Snake - Summon an electric snake that zaps enemies.
 * inspired by League of Legends: Electric snake attacks.
 */
public class ElectricSnakeSkill extends BaseSkill implements ISummon {

    public static final ElectricSnakeSkill INSTANCE = new ElectricSnakeSkill();

    private ElectricSnakeSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "electric_snake");
        }
    }
}
