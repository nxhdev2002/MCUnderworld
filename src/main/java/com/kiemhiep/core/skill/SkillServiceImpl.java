package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.api.repository.SkillRepository;
import com.kiemhiep.api.service.SkillService;
import com.kiemhiep.core.skill.SkillManager.UseResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SkillServiceImpl implements SkillService {

    private final SkillDefinitionRepository definitionRepository;
    private final SkillRepository playerSkillRepository;
    private final SkillManager skillManager;

    public SkillServiceImpl(SkillDefinitionRepository definitionRepository,
                            SkillRepository playerSkillRepository,
                            SkillManager skillManager) {
        this.definitionRepository = definitionRepository;
        this.playerSkillRepository = playerSkillRepository;
        this.skillManager = skillManager;
    }

    @Override
    public Optional<SkillDefinition> getSkillDefinition(String skillId) {
        return definitionRepository.getBySkillId(skillId);
    }

    @Override
    public Optional<SkillDefinition> getByItemId(String itemId) {
        return definitionRepository.getByItemId(itemId);
    }

    @Override
    public List<Skill> getPlayerSkills(long playerId) {
        return playerSkillRepository.getByPlayerId(playerId);
    }

    @Override
    public UseResult useSkill(UUID casterId, String itemId, long serverTick) {
        Optional<SkillDefinition> def = definitionRepository.getByItemId(itemId);
        if (def.isEmpty()) return UseResult.INVALID_SKILL;
        return skillManager.useSkill(casterId, def.get(), serverTick);
    }
}
