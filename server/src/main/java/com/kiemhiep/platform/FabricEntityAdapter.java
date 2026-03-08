package com.kiemhiep.platform;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.WorldAdapter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.UUID;

/**
 * Wrap Entity (Fabric) thành EntityAdapter (api).
 */
public class FabricEntityAdapter implements EntityAdapter {

    private final Entity entity;
    private final WorldAdapter worldAdapter;

    public FabricEntityAdapter(Entity entity, WorldAdapter worldAdapter) {
        this.entity = entity;
        this.worldAdapter = worldAdapter;
    }

    @Override
    public UUID getUniqueId() {
        return entity.getUUID();
    }

    @Override
    public Location getLocation() {
        return FabricLocation.from(
            worldAdapter.getWorldId(),
            entity.getX(), entity.getY(), entity.getZ()
        );
    }

    @Override
    public String getType() {
        return inferType(entity);
    }

    @Override
    public WorldAdapter getWorld() {
        return worldAdapter;
    }

    /**
     * Phân loại entity: "mob", "projectile", "npc", "other" (theo Rule 5 limits).
     */
    public static String inferType(Entity entity) {
        if (entity instanceof net.minecraft.server.level.ServerPlayer) {
            return "player"; // không đếm vào limit mob/npc/other
        }
        if (entity instanceof Projectile) {
            return "projectile";
        }
        if (entity instanceof LivingEntity) {
            EntityType<?> type = entity.getType();
            // NPC có thể là custom entity; đơn giản coi một số type là npc
            if (type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
                return "npc";
            }
            return "mob";
        }
        return "other";
    }
}
