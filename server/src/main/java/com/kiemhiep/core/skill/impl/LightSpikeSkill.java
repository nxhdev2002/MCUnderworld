package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Lux: Final Spark Spike - Spark a final spike of light energy.
 * inspired by League of Legends: Lux's Final Spark.
 */
public class LightSpikeSkill extends BaseSkill implements ISummon {

    public static final LightSpikeSkill INSTANCE = new LightSpikeSkill();

    private LightSpikeSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "light_spike");
        }
    }
}
