package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Quest;

import java.util.List;
import java.util.Optional;

public interface QuestRepository {

    Optional<Quest> getById(long id);

    List<Quest> getByPlayerId(long playerId);

    Optional<Quest> getByPlayerIdAndQuestId(long playerId, String questId);

    Quest save(Quest quest);

    void deleteById(long id);
}
