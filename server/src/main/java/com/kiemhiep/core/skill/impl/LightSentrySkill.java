package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Vikor: Light Sentry - Summon a sentry turret that shoots enemies.
 * inspired by League of Legends: Viktor's Death Ray and Sentry Charm.
 */
public class LightSentrySkill extends BaseSkill implements ISummon {

    public static final LightSentrySkill INSTANCE = new LightSentrySkill();

    private LightSentrySkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "sentry_light");
        }
    }
}
