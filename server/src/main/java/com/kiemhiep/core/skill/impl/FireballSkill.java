package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.api.skill.effect.IMeteor;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Fireball skill: triệu hồi thiên thạch từ trên trời rơi xuống mục tiêu (origin), kèm area damage.
 */
public class FireballSkill extends BaseSkill implements IMeteor, IAreaDamage {

    public static final FireballSkill INSTANCE = new FireballSkill();

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks or order of effects
    }

    @Override
    public void applyMeteor(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "meteor");
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        // Apply damage to ctx.getTargetsInRadius(); delegate to CombatService in Phase 08 if needed
    }
}
