package com.kiemhiep.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * S2C payload for player stats (level, mana). Client displays in HUD.
 */
public record PlayerStatsPayload(int level, int currentMana, int maxMana)
    implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("kiemhiep", "player_stats");

    public static final CustomPacketPayload.Type<PlayerStatsPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PlayerStatsPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        PlayerStatsPayload::level,
        ByteBufCodecs.VAR_INT,
        PlayerStatsPayload::currentMana,
        ByteBufCodecs.VAR_INT,
        PlayerStatsPayload::maxMana,
        PlayerStatsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
