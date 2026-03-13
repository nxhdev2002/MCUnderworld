package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Ekko: Z-Drive - Create a time rift that rewinds time.
 * inspired by League of Legends: Ekko's Z-Drive Resonance.
 */
public class ZDriveSkill extends BaseSkill implements ISummon {

    public static final ZDriveSkill INSTANCE = new ZDriveSkill();

    private ZDriveSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "time_breaker");
        }
    }
}
