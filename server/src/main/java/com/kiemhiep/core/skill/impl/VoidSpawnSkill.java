package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Tryndamere: Void Spawn - Summon void monsters.
 * inspired by League of Legends: Tryndamere's Bloodlust.
 */
public class VoidSpawnSkill extends BaseSkill implements ISummon {

    public static final VoidSpawnSkill INSTANCE = new VoidSpawnSkill();

    private VoidSpawnSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "void_spawn");
        }
    }
}
