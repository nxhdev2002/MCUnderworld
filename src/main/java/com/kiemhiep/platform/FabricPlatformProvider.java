package com.kiemhiep.platform;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.platform.WorldAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;
import java.util.UUID;

/**
 * PlatformProvider dùng MinecraftServer (Fabric).
 * Server được set khi server start (setServer).
 */
public class FabricPlatformProvider implements PlatformProvider {

    private volatile MinecraftServer server;

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public Optional<PlayerAdapter> getPlayer(UUID uuid) {
        MinecraftServer s = server;
        if (s == null) {
            return Optional.empty();
        }
        ServerPlayer player = s.getPlayerList().getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }
        WorldAdapter world = new FabricWorldAdapter((net.minecraft.server.level.ServerLevel) player.level());
        return Optional.of(new FabricPlayerAdapter(player, world));
    }

    @Override
    public Optional<WorldAdapter> getWorld(String worldId) {
        MinecraftServer s = server;
        if (s == null) {
            return Optional.empty();
        }
        for (ServerLevel level : s.getAllLevels()) {
            if (level.dimension().toString().equals(worldId)) {
                return Optional.of(new FabricWorldAdapter(level));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<EntityAdapter> getEntity(UUID uuid) {
        MinecraftServer s = server;
        if (s == null) {
            return Optional.empty();
        }
        for (ServerLevel level : s.getAllLevels()) {
            net.minecraft.world.entity.Entity e = level.getEntity(uuid);
            if (e != null) {
                return Optional.of(new FabricEntityAdapter(e, new FabricWorldAdapter(level)));
            }
        }
        return Optional.empty();
    }
}
