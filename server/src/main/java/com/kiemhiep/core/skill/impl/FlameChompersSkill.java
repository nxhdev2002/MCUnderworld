package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Jinx: Flame Chompers - Summon flame chompers that chase enemies.
 * inspired by League of Legends: Jinx's Flame Chompers!
 */
public class FlameChompersSkill extends BaseSkill implements ISummon {

    public static final FlameChompersSkill INSTANCE = new FlameChompersSkill();

    private FlameChompersSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "flame_chomp");
        }
    }
}
