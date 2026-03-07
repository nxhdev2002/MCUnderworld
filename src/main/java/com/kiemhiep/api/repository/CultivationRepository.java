package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Cultivation;

import java.util.List;
import java.util.Optional;

public interface CultivationRepository {

    Optional<Cultivation> getById(long id);

    Optional<Cultivation> getByPlayerId(long playerId);

    Cultivation save(Cultivation cultivation);

    void deleteById(long id);

    List<Cultivation> findAll();
}
