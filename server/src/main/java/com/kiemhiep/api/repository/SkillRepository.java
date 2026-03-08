package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Skill;

import java.util.List;
import java.util.Optional;

public interface SkillRepository {

    Optional<Skill> getById(long id);

    List<Skill> getByPlayerId(long playerId);

    Optional<Skill> getByPlayerIdAndSkillId(long playerId, String skillId);

    Skill save(Skill skill);

    void deleteById(long id);
}
