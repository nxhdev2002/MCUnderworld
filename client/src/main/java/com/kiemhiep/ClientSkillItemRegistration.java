package com.kiemhiep;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;

/**
 * Registers skill items on the client so the item registry matches the server.
 * Prevents "Registry entry (kiemhiep:skill_*) is missing from local registry" when joining.
 * This list must match the server's skill definitions (db/migration V4__seed_skill_definitions.sql).
 */
public final class ClientSkillItemRegistration {

    /** Skill item IDs registered by server (from seed). Client must register the same set. */
    private static final String[] SKILL_ITEM_IDS = {
        "kiemhiep:skill_fireball"
    };

    private ClientSkillItemRegistration() {}

    public static void registerAll() {
        for (String itemId : SKILL_ITEM_IDS) {
            registerItem(itemId);
        }
    }

    private static void registerItem(String itemId) {
        if (itemId == null || !itemId.contains(":")) return;
        try {
            String[] parts = itemId.split(":", 2);
            String namespace = parts[0];
            String path = parts[1];
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
            if (BuiltInRegistries.ITEM.containsKey(id)) return;
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
            Item item = new Item(new Item.Properties().setId(itemKey));
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        } catch (Exception ignored) {
            // Log only in dev if needed
        }
    }
}
