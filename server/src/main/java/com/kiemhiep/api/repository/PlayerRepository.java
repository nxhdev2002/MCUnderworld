package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.Player;

import java.util.Optional;

public interface PlayerRepository {

    Optional<Player> getById(long id);

    Optional<Player> getByUuid(String uuid);

    Player save(Player player);

    void deleteById(long id);
}
