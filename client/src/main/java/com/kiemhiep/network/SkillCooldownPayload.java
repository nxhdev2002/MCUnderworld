package com.kiemhiep.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * S2C payload for skill cooldown notification. Sent from server to client when a skill is used.
 */
public record SkillCooldownPayload(String skillId, long cooldownEndTimeMillis)
    implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("kiemhiep", "skill_cooldown");

    public static final CustomPacketPayload.Type<SkillCooldownPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SkillCooldownPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        SkillCooldownPayload::skillId,
        ByteBufCodecs.LONG,
        SkillCooldownPayload::cooldownEndTimeMillis,
        SkillCooldownPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
