package com.kiemhiep.network;

import com.kiemhiep.ClientSkillDefinitions;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers skill definitions payload and updates client-side skill definition cache.
 */
public final class SkillDefinitionsReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    private SkillDefinitionsReceiver() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SkillDefinitionsPayload.TYPE, SkillDefinitionsPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SkillDefinitionsPayload.TYPE, (payload, context) -> {
            LOGGER.debug("Skill definitions received: count={}", payload.skills().size());
            ClientSkillDefinitions.setDefinitions(payload.skills());
            LOGGER.info("Loaded {} skill definitions from server", payload.skills().size());
        });
    }
}