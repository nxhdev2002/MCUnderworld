package com.kiemhiep.api.platform;

import java.util.Optional;
import java.util.UUID;

/**
 * Cung cấp adapter cho player, world, entity từ backend (vd. Minecraft server).
 * Module dùng interface này; implementation (Fabric) wrap MinecraftServer.
 */
public interface PlatformProvider {

    /** Lấy player theo UUID. */
    Optional<PlayerAdapter> getPlayer(UUID uuid);

    /**
     * Lấy world theo ID.
     *
     * @param worldId dimension registry key string, ví dụ "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"
     * @return adapter của world nếu tìm thấy
     */
    Optional<WorldAdapter> getWorld(String worldId);

    /** Lấy entity theo UUID (nếu có). */
    Optional<EntityAdapter> getEntity(UUID uuid);
}
