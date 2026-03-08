package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.core.skill.SkillManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillService {

    Optional<SkillDefinition> getSkillDefinition(String skillId);

    Optional<SkillDefinition> getByItemId(String itemId);

    /** All skill definitions on the system (for admin /skill all). */
    List<SkillDefinition> getAllSkillDefinitions();

    List<Skill> getPlayerSkills(long playerId);

    /** Clear in-memory definition cache (e.g. when reloading skill definitions). */
    void clearDefinitionCache();

    /**
     * Use skill from item. Resolves definition by itemId, validates and executes via SkillManager.
     *
     * @param casterId   player UUID
     * @param itemId     registry id of the item (e.g. "kiemhiep:skill_fireball")
     * @param serverTick current server tick
     * @return result; if SUCCESS and definition is consumable, caller should shrink item stack
     */
    SkillManager.UseResult useSkill(UUID casterId, String itemId, long serverTick);
}
