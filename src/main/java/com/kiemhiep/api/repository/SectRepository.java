package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Sect;

import java.util.List;
import java.util.Optional;

public interface SectRepository {

    Optional<Sect> getById(long id);

    Sect save(Sect sect);

    void deleteById(long id);

    List<Sect> findAll();
}
