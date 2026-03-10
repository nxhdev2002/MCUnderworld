package com.kiemhiep.platform.network;

import com.kiemhiep.Kiemhiep;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.core.skill.SkillRegistry;
import com.kiemhiep.api.skill.ISkill;
import com.kiemhiep.api.skill.effect.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * S2C payload for sending all skill definitions to the client.
 * Sent on login to populate the client-side skill definition cache.
 */
public record SkillDefinitionsPayload(List<SkillDefinitionData> skills)
    implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("kiemhiep", "skill_definitions");

    public static final CustomPacketPayload.Type<SkillDefinitionsPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SkillDefinitionsPayload> STREAM_CODEC = StreamCodec.composite(
        SkillDefinitionData.STREAM_CODEC.apply(ByteBufCodecs.list()),
        SkillDefinitionsPayload::skills,
        SkillDefinitionsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Lightweight data record for network transfer (without Instant fields).
     */
    public record SkillDefinitionData(
        String skillId,
        String itemId,
        String name,
        int manaCost,
        int cooldownTicks,
        double maxRadius,
        boolean isAoe,
        boolean isMelee,
        String skillType,
        int castTimeTicks,
        boolean consumable,
        List<String> effects
    ) {
        public static final StreamCodec<FriendlyByteBuf, SkillDefinitionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SkillDefinitionData::skillId,
            ByteBufCodecs.STRING_UTF8,
            SkillDefinitionData::itemId,
            ByteBufCodecs.STRING_UTF8,
            SkillDefinitionData::name,
            ByteBufCodecs.INT,
            SkillDefinitionData::manaCost,
            ByteBufCodecs.INT,
            SkillDefinitionData::cooldownTicks,
            ByteBufCodecs.DOUBLE,
            SkillDefinitionData::maxRadius,
            ByteBufCodecs.BOOL,
            SkillDefinitionData::isAoe,
            ByteBufCodecs.BOOL,
            SkillDefinitionData::isMelee,
            ByteBufCodecs.STRING_UTF8,
            SkillDefinitionData::skillType,
            ByteBufCodecs.INT,
            SkillDefinitionData::castTimeTicks,
            ByteBufCodecs.BOOL,
            SkillDefinitionData::consumable,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            SkillDefinitionData::effects,
            SkillDefinitionData::new
        );
    }

    /**
     * Create payload from database definitions and registry.
     * Repository is passed from SkillModule which has access to it.
     */
    public static SkillDefinitionsPayload create(SkillDefinitionRepository repo) {
        if (repo == null) {
            Kiemhiep.LOGGER.warn("SkillDefinitionRepository is null, cannot create skill definitions payload");
            return new SkillDefinitionsPayload(List.of());
        }

        List<com.kiemhiep.api.model.SkillDefinition> dbDefinitions = repo.findAll();
        List<SkillDefinitionData> skills = new ArrayList<>();

        for (com.kiemhiep.api.model.SkillDefinition def : dbDefinitions) {
            List<String> effects = getEffectsForSkill(def.behaviorId());
            skills.add(new SkillDefinitionData(
                def.skillId(),
                def.itemId(),
                def.name(),
                def.manaCost(),
                def.cooldownTicks(),
                def.maxRadius(),
                def.isAoe(),
                def.isMelee(),
                def.skillType(),
                def.castTimeTicks(),
                def.consumable(),
                effects
            ));
        }

        return new SkillDefinitionsPayload(skills);
    }

    /**
     * Get effects list based on skill class interfaces.
     */
    private static List<String> getEffectsForSkill(String behaviorId) {
        List<String> effects = new ArrayList<>();

        if (behaviorId == null) {
            return effects;
        }

        // Get skill instance from registry
        ISkill skill = SkillRegistry.get(behaviorId).orElse(null);
        if (skill == null) {
            return effects;
        }

        // Check implemented interfaces
        if (skill instanceof IMeteor) {
            effects.add("Meteor");
        }
        if (skill instanceof IThunder) {
            effects.add("Thunder");
        }
        if (skill instanceof ITornado) {
            effects.add("Tornado");
        }
        if (skill instanceof ITsunami) {
            effects.add("Tsunami");
        }
        if (skill instanceof IAreaDamage) {
            effects.add("Area Damage");
        }
        if (skill instanceof ISingleTargetDamage) {
            effects.add("Single Target");
        }
        if (skill instanceof IShield) {
            effects.add("Shield");
        }
        if (skill instanceof IHeal) {
            effects.add("Heal");
        }
        if (skill instanceof IStunable) {
            effects.add("Stun");
        }
        if (skill instanceof ISilentable) {
            effects.add("Silence");
        }
        if (skill instanceof ISummon) {
            effects.add("Summon");
        }

        return effects;
    }

    /**
     * Holder for repository reference to be set by SkillModule.
     */
    private static volatile SkillDefinitionRepository repositoryHolder;

    /**
     * Set the repository reference for use in create().
     */
    public static void setRepository(SkillDefinitionRepository repo) {
        repositoryHolder = repo;
    }

    /**
     * Create payload using the stored repository reference.
     */
    public static SkillDefinitionsPayload create() {
        return create(repositoryHolder);
    }
}