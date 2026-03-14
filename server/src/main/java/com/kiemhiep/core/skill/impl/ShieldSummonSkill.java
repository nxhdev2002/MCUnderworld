package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IShield;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Void Shield - Create a shield that absorbs damage.
 * inspired by League of Legends: Void shields.
 */
public class ShieldSummonSkill extends BaseSkill implements IShield {

    public static final ShieldSummonSkill INSTANCE = new ShieldSummonSkill();

    private ShieldSummonSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applyShield(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "shield_summon");
        }
    }
}
