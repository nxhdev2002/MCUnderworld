package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Sect;

import java.util.List;
import java.util.Optional;

public interface SectRepository {

    Optional<Sect> getById(long id);

    Sect save(Sect sect);

    void deleteById(long id);

    /**
     * Returns all sects. For admin or small datasets only; do not use in hot path.
     * Large tables will load entirely into memory.
     */
    List<Sect> findAll();
}
