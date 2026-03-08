package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.WorldAdapter;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.api.skill.ISkill;
import com.kiemhiep.api.skill.ManaProvider;
import com.kiemhiep.api.skill.SkillContext;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Server-only: validates and executes skill from item. Resolves definition by item_id,
 * gets skill instance from SkillRegistry, runs cooldown/cast then skill.execute(ctx).
 */
public final class SkillManager {

    public enum UseResult {
        SUCCESS,
        ON_COOLDOWN,
        ALREADY_CASTING,
        INVALID_SKILL,
        INSUFFICIENT_MANA,
        CAST_STARTED
    }

    private final SkillDefinitionRepository definitionRepository;
    private final CooldownManager cooldownManager;
    private final CastStateManager castStateManager;
    private final EffectManager effectManager;
    private final PlatformProvider platformProvider;
    private final Optional<ManaProvider> manaProvider;

    public SkillManager(SkillDefinitionRepository definitionRepository,
                        CooldownManager cooldownManager,
                        CastStateManager castStateManager,
                        EffectManager effectManager,
                        PlatformProvider platformProvider,
                        Optional<ManaProvider> manaProvider) {
        this.definitionRepository = definitionRepository;
        this.cooldownManager = cooldownManager;
        this.castStateManager = castStateManager;
        this.effectManager = effectManager;
        this.platformProvider = platformProvider;
        this.manaProvider = Objects.requireNonNull(manaProvider, "manaProvider");
    }

    /**
     * Execute skill for caster using the given definition (already resolved by item_id).
     * If cast_time_ticks > 0, starts cast and returns CAST_STARTED; otherwise runs immediately.
     *
     * @param serverTick current server tick (for cast end and cooldown)
     * @return SUCCESS if executed (consumable: caller should shrink stack), CAST_STARTED if cast began
     */
    public UseResult useSkill(UUID casterId, SkillDefinition definition, long serverTick) {
        if (definition == null) return UseResult.INVALID_SKILL;

        Optional<ISkill> skillOpt = SkillRegistry.get(definition.behaviorId());
        if (skillOpt.isEmpty()) return UseResult.INVALID_SKILL;

        if (cooldownManager.isOnCooldown(casterId, definition.skillId())) {
            return UseResult.ON_COOLDOWN;
        }
        if (castStateManager.isCasting(casterId)) {
            return UseResult.ALREADY_CASTING;
        }

        int manaCost = definition.manaCost();
        if (manaCost > 0 && manaProvider.isPresent()) {
            if (manaProvider.get().getCurrentMana(casterId) < manaCost) {
                return UseResult.INSUFFICIENT_MANA;
            }
            if (!manaProvider.get().consumeMana(casterId, manaCost)) {
                return UseResult.INSUFFICIENT_MANA;
            }
        }

        if (definition.castTimeTicks() > 0) {
            long castEndTick = serverTick + definition.castTimeTicks();
            castStateManager.startCast(casterId, definition.skillId(), castEndTick, definition);
            return UseResult.CAST_STARTED;
        }

        return doExecute(casterId, definition, skillOpt.get(), serverTick);
    }

    /** Called when cast completes (from CastStateManager tick). */
    public void onCastComplete(UUID casterId, CastStateManager.CastEntry entry) {
        Optional<ISkill> skillOpt = SkillRegistry.get(entry.definition.behaviorId());
        if (skillOpt.isEmpty()) return;
        doExecute(casterId, entry.definition, skillOpt.get(), entry.castEndTick);
    }

    private UseResult doExecute(UUID casterId, SkillDefinition definition, ISkill skill, long serverTick) {
        PlayerAdapter caster = platformProvider.getPlayer(casterId).orElse(null);
        Location origin = caster != null ? caster.getLocation() : new Location("", 0, 0, 0);
        WorldAdapter world = caster != null ? caster.getWorld() : null;
        List<EntityAdapter> targets = Collections.<EntityAdapter>emptyList();
        if (world != null && definition.maxRadius() > 0) {
            targets = world.getEntitiesInRadius(origin, definition.maxRadius());
        }
        SkillContext ctx = new SkillContextImpl(casterId, caster, definition, origin, targets, serverTick);
        skill.execute(ctx);

        long cooldownEndMillis = System.currentTimeMillis() + definition.cooldownTicks() * 50L;
        cooldownManager.setCooldown(casterId, definition.skillId(), cooldownEndMillis);
        return UseResult.SUCCESS;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CastStateManager getCastStateManager() {
        return castStateManager;
    }

    public long getCooldownEndTimeMillis(UUID playerId, String skillId) {
        return cooldownManager.getCooldownEndTimeMillis(playerId, skillId);
    }
}
