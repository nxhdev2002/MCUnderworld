package com.kiemhiep.entity;

import com.kiemhiep.KiemhiepConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Đăng ký entity types của mod (MeteorEntity, ...).
 * Gọi từ cả server (Kiemhiep.onInitialize) và client (KiemhiepClient.onInitializeClient)
 * để registry có entity type ở cả hai môi trường.
 */
public final class EntityRegistration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KiemhiepConstants.MOD_ID);

    private EntityRegistration() {}

    public static void register() {
        Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(KiemhiepConstants.MOD_ID, "meteor"),
            MeteorEntity.TYPE
        );
        LOGGER.debug("Registered entity type: meteor");
    }
}
