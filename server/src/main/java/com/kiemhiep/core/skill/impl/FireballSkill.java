package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.api.skill.effect.IThunder;
import com.kiemhiep.core.skill.BaseSkill;

/**
 * Example skill: fireball with thunder and area damage. Implements IThunder and IAreaDamage;
 * overrides applyThunder for custom (fire) effect and uses default-style area damage.
 */
public class FireballSkill extends BaseSkill implements IThunder, IAreaDamage {

    public static final FireballSkill INSTANCE = new FireballSkill();

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks or order of effects
    }

    @Override
    public void applyThunder(SkillContext ctx) {
        // Override: custom "fire" thunder (particle/sound) at origin
        // In real impl: send S2C packet or spawn particle via EffectManager
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        // Apply damage to ctx.getTargetsInRadius(); delegate to CombatService in Phase 08 if needed
    }
}
