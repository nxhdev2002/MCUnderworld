package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.LocationAdapter;
import com.kiemhiep.api.platform.World;
import com.kiemhiep.api.platform.EntityAdapter;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * Fabric implementation of LocationAdapter.
 */
public class FabricLocationAdapter implements LocationAdapter {
    private final ServerWorld world;
    private final double x, y, z;
    private final float yaw, pitch;

    public FabricLocationAdapter(ServerWorld world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public World getWorld() {
        return new World(world.getRegistryKey().getValue().toString());
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    @Override
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    @Override
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public String getBlockType() {
        var blockState = world.getBlockState(new BlockPos((int) x, (int) y, (int) z));
        return net.minecraft.registry.Registries.BLOCK.getId(blockState.getBlock()).toString();
    }

    @Override
    public void setBlockType(String blockType) {
        // TODO: Implement block setting
    }

    @Override
    public boolean isDifferentWorld(LocationAdapter other) {
        if (other instanceof FabricLocationAdapter) {
            return !world.getRegistryKey().equals(((FabricLocationAdapter) other).world.getRegistryKey());
        }
        return true;
    }

    @Override
    public double distance(LocationAdapter other) {
        if (isDifferentWorld(other)) {
            return Double.MAX_VALUE;
        }
        double dx = x - other.getX();
        double dy = y - other.getY();
        double dz = z - other.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public double distanceSquared(LocationAdapter other) {
        if (isDifferentWorld(other)) {
            return Double.MAX_VALUE;
        }
        double dx = x - other.getX();
        double dy = y - other.getY();
        double dz = z - other.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public void spawnParticle(String particleType, int count) {
        // TODO: Implement particle spawning
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        // TODO: Implement sound playing
    }

    @Override
    public Optional<EntityAdapter> getEntity(UUID entityId) {
        var entity = world.getEntity(entityId);
        if (entity != null) {
            return Optional.of(new FabricEntityAdapter(entity));
        }
        return Optional.empty();
    }
}
