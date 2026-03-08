package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.SkillDefinition;

import java.util.List;
import java.util.Optional;

public interface SkillDefinitionRepository {

    Optional<SkillDefinition> getById(long id);

    Optional<SkillDefinition> getBySkillId(String skillId);

    Optional<SkillDefinition> getByItemId(String itemId);

    List<SkillDefinition> findAll();

    SkillDefinition save(SkillDefinition definition);

    void deleteById(long id);
}
