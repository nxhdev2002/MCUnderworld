package com.kiemhiep.core.skill;

import com.kiemhiep.Kiemhiep;
import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.api.repository.SkillRepository;
import com.kiemhiep.api.service.SkillService;
import com.kiemhiep.core.skill.SkillManager.UseResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkillServiceImpl implements SkillService {

    private final SkillDefinitionRepository definitionRepository;
    private final SkillRepository playerSkillRepository;
    private final SkillManager skillManager;
    /** In-memory cache for getByItemId to reduce DB reads in hot path. */
    private final ConcurrentHashMap<String, SkillDefinition> definitionByItemId = new ConcurrentHashMap<>();

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
        SkillDefinition cached = definitionByItemId.get(itemId);
        if (cached != null) return Optional.of(cached);
        Optional<SkillDefinition> fromDb = definitionRepository.getByItemId(itemId);
        fromDb.ifPresent(d -> definitionByItemId.put(itemId, d));
        return fromDb;
    }

    @Override
    public List<SkillDefinition> getAllSkillDefinitions() {
        return definitionRepository.findAll();
    }

    @Override
    public List<Skill> getPlayerSkills(long playerId) {
        return playerSkillRepository.getByPlayerId(playerId);
    }

    @Override
    public UseResult useSkill(UUID casterId, String itemId, long serverTick) {
        Optional<SkillDefinition> def = getByItemId(itemId);
        if (def.isEmpty()) {
            // Fallback: game sends "namespace:path" (e.g. kiemhiep:skill_ice_shard); DB may have skill_id = path
            int colon = itemId.indexOf(':');
            if (colon >= 0 && colon + 1 < itemId.length()) {
                String skillIdFromPath = itemId.substring(colon + 1);
                def = definitionRepository.getBySkillId(skillIdFromPath);
                if (def.isPresent()) {
                    definitionByItemId.put(itemId, def.get());
                    Kiemhiep.LOGGER.debug("[Skill] resolved itemId={} via skill_id={}", itemId, skillIdFromPath);
                }
            }
        }
        if (def.isEmpty()) {
            Kiemhiep.LOGGER.info("[Skill] no definition for itemId={} (skill item not in DB or wrong item)", itemId);
            return UseResult.INVALID_SKILL;
        }
        return skillManager.useSkill(casterId, def.get(), serverTick);
    }

    @Override
    public void clearDefinitionCache() {
        definitionByItemId.clear();
    }
}
