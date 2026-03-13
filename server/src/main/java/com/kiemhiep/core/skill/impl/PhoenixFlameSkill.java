package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Phoenix: Flame凤凰 - Summon a phoenix that restores health on death.
 * inspired by League of Legends: Phoenix's Solar Flare.
 */
public class PhoenixFlameSkill extends BaseSkill implements ISummon {

    public static final PhoenixFlameSkill INSTANCE = new PhoenixFlameSkill();

    private PhoenixFlameSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "phoenix");
        }
    }
}
