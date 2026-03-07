package com.kiemhiep.platform;

import com.kiemhiep.api.platform.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Tạo Location (api) từ BlockPos/Vec3 + worldId.
 */
public final class FabricLocation {

    private FabricLocation() {
    }

    public static Location from(String worldId, BlockPos pos) {
        return new Location(worldId, pos.getX(), pos.getY(), pos.getZ());
    }

    public static Location from(String worldId, Vec3 vec) {
        return new Location(worldId, vec.x, vec.y, vec.z);
    }

    public static Location from(String worldId, double x, double y, double z) {
        return new Location(worldId, x, y, z);
    }
}
