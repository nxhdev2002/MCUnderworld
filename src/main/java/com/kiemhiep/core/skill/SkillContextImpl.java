package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.IEffectRunner;
import com.kiemhiep.api.skill.SkillContext;

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
}
