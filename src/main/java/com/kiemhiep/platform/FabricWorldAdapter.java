package com.kiemhiep.platform;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.WorldAdapter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrap ServerLevel (Fabric) thành WorldAdapter (api).
 * Chỉ query trong box/radius — không gọi getEntities() toàn world (Rule 4).
 */
public class FabricWorldAdapter implements WorldAdapter {

    private final ServerLevel level;

    public FabricWorldAdapter(ServerLevel level) {
        this.level = level;
    }

    @Override
    public String getWorldId() {
        // Dimension is ResourceKey<Level>; use toString() for stable world id (e.g. "minecraft:overworld")
        return level.dimension().toString();
    }

    @Override
    public List<EntityAdapter> getEntitiesInBox(Location origin, double radius) {
        double minX = origin.x() - radius;
        double minY = origin.y() - radius;
        double minZ = origin.z() - radius;
        double maxX = origin.x() + radius;
        double maxY = origin.y() + radius;
        double maxZ = origin.z() + radius;
        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        return getEntitiesInAabb(box);
    }

    @Override
    public List<EntityAdapter> getEntitiesInRadius(Location origin, double radius) {
        return getEntitiesInBox(origin, radius);
    }

    private List<EntityAdapter> getEntitiesInAabb(AABB box) {
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, box);
        List<EntityAdapter> result = new ArrayList<>(entities.size());
        for (Entity e : entities) {
            result.add(new FabricEntityAdapter(e, this));
        }
        return result;
    }
}
