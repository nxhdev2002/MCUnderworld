package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric implementation of PlatformProvider.
 */
public class FabricPlatformProvider implements PlatformProvider {
    private final MinecraftServer server;
    private final FabricEventBus eventBus;
    private final FabricScheduler scheduler;
    private final FabricEffectPlayer effectPlayer;

    public FabricPlatformProvider(MinecraftServer server) {
        this.server = server;
        this.eventBus = new FabricEventBus();
        this.scheduler = new FabricScheduler(server);
        this.effectPlayer = new FabricEffectPlayer(server);
    }

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public CompletableFuture<Optional<PlayerAdapter>> getPlayerAdapter(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) {
                return Optional.empty();
            }
            return Optional.of(new FabricPlayerAdapter(player));
        });
    }

    @Override
    public CompletableFuture<List<PlayerAdapter>> getOnlinePlayers() {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerAdapter> players = new ArrayList<>();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                players.add(new FabricPlayerAdapter(player));
            }
            return players;
        });
    }

    @Override
    public boolean isPlayerOnline(UUID uuid) {
        return server.getPlayerManager().getPlayer(uuid) != null;
    }

    @Override
    public void broadcast(String message) {
        server.getPlayerManager().broadcast(message, false);
    }

    @Override
    public CompletableFuture<Optional<WorldAdapter>> getWorldAdapter(String worldName) {
        return CompletableFuture.supplyAsync(() -> {
            ServerWorld world = server.getWorld(Identifier.of(worldName));
            if (world == null) {
                // Try to find by key
                for (ServerWorld w : server.getWorlds()) {
                    if (w.getRegistryKey().getValue().getPath().equals(worldName)) {
                        return Optional.of(new FabricWorldAdapter(w));
                    }
                }
                return Optional.empty();
            }
            return Optional.of(new FabricWorldAdapter(world));
        });
    }

    @Override
    public CompletableFuture<Optional<WorldAdapter>> getMainWorldAdapter() {
        return CompletableFuture.supplyAsync(() -> {
            ServerWorld world = server.getOverworld();
            return Optional.of(new FabricWorldAdapter(world));
        });
    }

    @Override
    public CompletableFuture<Optional<EntityAdapter>> getEntityAdapter(UUID entityUuid) {
        return CompletableFuture.supplyAsync(() -> {
            for (ServerWorld world : server.getWorlds()) {
                var entity = world.getEntity(entityUuid);
                if (entity != null) {
                    return Optional.of(new FabricEntityAdapter(entity));
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void runTask(Runnable task) {
        scheduler.runTask(task);
    }

    @Override
    public void runTaskLater(long delayTicks, Runnable task) {
        scheduler.runTaskLater(delayTicks, task);
    }

    @Override
    public void runTaskAsync(Runnable task) {
        scheduler.runTaskAsync(task);
    }

    @Override
    public void cancelTask(Object taskId) {
        scheduler.cancelTask(taskId);
    }

    @Override
    public boolean hasPermission(UUID playerUuid, String permission) {
        // Fabric doesn't have built-in permissions, return true for OP players
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
        return player != null && player.hasPermissionLevel(2);
    }

    @Override
    public void setPermission(UUID playerUuid, String permission, boolean value) {
        // Not supported in vanilla Fabric - would need a permissions mod
        // This is a no-op implementation
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public EffectPlayer getEffectPlayer() {
        return effectPlayer;
    }

    public FabricEventBus getEventBus() {
        return eventBus;
    }
}
