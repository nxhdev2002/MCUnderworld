package com.kiemhiep.network;

import com.kiemhiep.ClientSkillCooldowns;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers skill cooldown payload and updates client-side cooldown storage.
 */
public final class SkillCooldownReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    private SkillCooldownReceiver() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SkillCooldownPayload.TYPE, SkillCooldownPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SkillCooldownPayload.TYPE, (payload, context) -> {
            String skillId = payload.skillId();
            long cooldownEndTimeMillis = payload.cooldownEndTimeMillis();
            LOGGER.debug("Skill cooldown received: skillId={} cooldownEnd={}", skillId, cooldownEndTimeMillis);
            ClientSkillCooldowns.setCooldown(skillId, cooldownEndTimeMillis);
        });
    }
}
