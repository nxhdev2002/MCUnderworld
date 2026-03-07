package com.kiemhiep.api.platform;

/**
 * Vị trí bất biến trong một world (block hoặc precise coordinates).
 * API-agnostic; không phụ thuộc Fabric/Minecraft.
 */
public record Location(String worldId, double x, double y, double z) {

    public Location {
        if (worldId == null) {
            worldId = "";
        }
    }
}
