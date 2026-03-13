package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISummon;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Quantum Ray - Fire a quantum ray that passes through minions.
 * inspired by League of Legends: Quantum phenomena.
 */
public class QuantumRaySkill extends BaseSkill implements ISummon {

    public static final QuantumRaySkill INSTANCE = new QuantumRaySkill();

    private QuantumRaySkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applySummon(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "quantum_ray");
        }
    }
}
