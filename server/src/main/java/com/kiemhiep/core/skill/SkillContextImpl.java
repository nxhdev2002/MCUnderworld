package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.IEffectRunner;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.platform.FabricEntityAdapter;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.UUID;

public record SkillContextImpl(
    UUID casterId,
    PlayerAdapter caster,
    SkillDefinition definition,
    Location origin,
    List<EntityAdapter> targetsInRadius,
    long serverTick,
    IEffectRunner effectRunner
) implements SkillContext {

    @Override
    public UUID getCasterId() {
        return casterId;
    }

    @Override
    public PlayerAdapter getCaster() {
        return caster;
    }

    @Override
    public SkillDefinition getDefinition() {
        return definition;
    }

    @Override
    public Location getOrigin() {
        return origin;
    }

    @Override
    public List<EntityAdapter> getTargetsInRadius() {
        return targetsInRadius;
    }

    @Override
    public long getServerTick() {
        return serverTick;
    }

    @Override
    public IEffectRunner getEffectRunner() {
        return effectRunner;
    }

    // Add implementations for new methods

    @Override
    public Object getCasterEntity() {
        // Return the raw entity from the adapter if available
        if (caster instanceof FabricPlayerAdapter fabricAdapter) {
            return fabricAdapter.getPlayer();
        }
        return null;
    }

    @Override
    public Object getTargetEntity() {
        // Check first target for single target damage
        if (!targetsInRadius.isEmpty()) {
            EntityAdapter adapter = targetsInRadius.get(0);
            if (adapter instanceof FabricEntityAdapter fabricAdapter) {
                return fabricAdapter.getEntity();
            }
        }
        return null;
    }

    @Override
    public Location getSkillLocation() {
        // Use origin if no specific location is stored in skill data
        return getOrigin();
    }

    @Override
    public PlayerAdapter getSkillCaster() {
        return getCaster();
    }

    @Override
    public int getSkillLevel() {
        return definition != null ? definition.level() : 1;
    }

    @Override
    public DamageSource getDamageSource(String type) {
        // Create damage source based on type
        Object casterEntity = getCasterEntity();
        if (casterEntity instanceof LivingEntity livingCaster) {
            ServerLevel level = (ServerLevel) livingCaster.level();
            if (level == null) return level.damageSources().generic();

            DamageSource source;
            switch (type) {
                case "lightning" -> source = level.damageSources().lightningBolt();
                case "magic" -> source = level.damageSources().magic();
                case "indirectMagic" -> source = level.damageSources().magic();
                case "player" -> {
                    if (livingCaster instanceof ServerPlayer player) {
                        source = level.damageSources().playerAttack(player);
                    } else {
                        source = level.damageSources().generic();
                    }
                }
                default -> source = level.damageSources().generic();
            }
            return source;
        }
        // Fallback - get level from caster's world adapter
        if (caster != null) {
            ServerLevel level = getServerLevelFromCaster();
            if (level != null) {
                return level.damageSources().generic();
            }
        }
        return null;
    }

    private ServerLevel getServerLevelFromCaster() {
        if (caster instanceof FabricPlayerAdapter fabricAdapter) {
            ServerPlayer player = fabricAdapter.getPlayer();
            return player != null ? player.level() : null;
        }
        return null;
    }

    @Override
    public List<Object> getEntitiesWithinArea(Object areaBox) {
        // Get entities within the specified area
        // This would access the world from the caster or origin
        return List.of();
    }
}
