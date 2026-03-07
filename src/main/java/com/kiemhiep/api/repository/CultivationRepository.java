package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Cultivation;

import java.util.List;
import java.util.Optional;

public interface CultivationRepository {

    Optional<Cultivation> getById(long id);

    Optional<Cultivation> getByPlayerId(long playerId);

    Cultivation save(Cultivation cultivation);

    void deleteById(long id);

    /**
     * Returns all cultivation records. For admin or small datasets only; do not use in hot path.
     * Large tables will load entirely into memory.
     */
    List<Cultivation> findAll();
}
