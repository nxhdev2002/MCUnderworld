package com.kiemhiep.platform;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.platform.WorldAdapter;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Wrap ServerPlayer (Fabric) thành PlayerAdapter (api).
 */
public class FabricPlayerAdapter implements PlayerAdapter {

    private final ServerPlayer player;
    private final WorldAdapter worldAdapter;

    public FabricPlayerAdapter(ServerPlayer player, WorldAdapter worldAdapter) {
        this.player = player;
        this.worldAdapter = worldAdapter;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUUID();
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    @Override
    public Location getLocation() {
        return FabricLocation.from(
            worldAdapter.getWorldId(),
            player.getX(), player.getY(), player.getZ()
        );
    }

    @Override
    public WorldAdapter getWorld() {
        return worldAdapter;
    }

    @Override
    public String getType() {
        return "player";
    }

    /** For platform use (e.g. sending packets). */
    public ServerPlayer getServerPlayer() {
        return player;
    }
}
