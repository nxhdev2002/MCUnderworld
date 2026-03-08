package com.kiemhiep.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * S2C payload for skill effect (tornado, tsunami, meteor, etc.). Client spawns particles at location.
 */
public record SkillEffectPayload(String skillId, String effectType, String worldId, double x, double y, double z)
    implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("kiemhiep", "skill_effect");

    public static final CustomPacketPayload.Type<SkillEffectPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SkillEffectPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        SkillEffectPayload::skillId,
        ByteBufCodecs.STRING_UTF8,
        SkillEffectPayload::effectType,
        ByteBufCodecs.STRING_UTF8,
        SkillEffectPayload::worldId,
        ByteBufCodecs.DOUBLE,
        SkillEffectPayload::x,
        ByteBufCodecs.DOUBLE,
        SkillEffectPayload::y,
        ByteBufCodecs.DOUBLE,
        SkillEffectPayload::z,
        SkillEffectPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
