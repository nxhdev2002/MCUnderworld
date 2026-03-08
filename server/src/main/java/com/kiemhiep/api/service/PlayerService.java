package com.kiemhiep.api.service;

import com.kiemhiep.api.model.Player;

import java.util.Optional;

/**
 * Service for player: get by UUID, get-or-create on join (insert if missing, optional name update).
 */
public interface PlayerService {

    Optional<Player> get(String uuid);

    /**
     * Load player by UUID, or create with (uuid, name). If player exists and name differs, updates name.
     */
    Player getOrCreate(String uuid, String name);
}
