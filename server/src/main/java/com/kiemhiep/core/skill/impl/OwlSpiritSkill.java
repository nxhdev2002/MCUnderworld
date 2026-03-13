package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Ahri: Fox Spirit - Summon a fox spirit companion.
 * inspired by League of Legends: Ahri's Spirit Rush.
 */
public class OwlSpiritSkill extends BaseSkill implements ISummon {

    public static final OwlSpiritSkill INSTANCE = new OwlSpiritSkill();

    private OwlSpiritSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "spirit_owl");
        }
    }
}
