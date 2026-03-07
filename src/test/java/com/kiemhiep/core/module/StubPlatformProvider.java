package com.kiemhiep.core.module;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.platform.WorldAdapter;

import java.util.Optional;
import java.util.UUID;

/** Stub PlatformProvider for unit tests. */
public class StubPlatformProvider implements PlatformProvider {

    @Override
    public Optional<PlayerAdapter> getPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<WorldAdapter> getWorld(String worldId) {
        return Optional.empty();
    }

    @Override
    public Optional<EntityAdapter> getEntity(UUID uuid) {
        return Optional.empty();
    }
}
