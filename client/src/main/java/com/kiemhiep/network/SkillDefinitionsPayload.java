package com.kiemhiep.network;

import com.kiemhiep.api.model.SkillDefinition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * S2C payload for skill definitions. Sent from server on player join.
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

        /**
         * Convert to client-side SkillDefinition.
         */
        public SkillDefinition toSkillDefinition() {
            return new SkillDefinition(
                skillId,
                itemId,
                name,
                manaCost,
                cooldownTicks,
                maxRadius,
                isAoe,
                isMelee,
                skillType,
                castTimeTicks,
                consumable,
                effects != null ? new ArrayList<>(effects) : new ArrayList<>()
            );
        }
    }
}