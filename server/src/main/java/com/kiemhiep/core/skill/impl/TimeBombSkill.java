package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Zilean: Time Bomb - Summon a time bomb that explodes after delay.
 * inspired by League of Legends: Zilean's Timed Bomb.
 */
public class TimeBombSkill extends BaseSkill implements ISummon {

    public static final TimeBombSkill INSTANCE = new TimeBombSkill();

    private TimeBombSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "time_bomb");
        }
    }
}
