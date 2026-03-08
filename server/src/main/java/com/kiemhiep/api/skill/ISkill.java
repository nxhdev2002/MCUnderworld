package com.kiemhiep.api.skill;

/**
 * Main skill interface. Runtime skill instance is obtained from SkillRegistry by behavior_id.
 */
public interface ISkill {

    /**
     * Execute skill logic. Engine calls this after validation (mana, cooldown, cast).
     * Implementation may call effect interfaces (applyThunder, applyAreaDamage, etc.) as needed.
     */
    void execute(SkillContext ctx);
}
