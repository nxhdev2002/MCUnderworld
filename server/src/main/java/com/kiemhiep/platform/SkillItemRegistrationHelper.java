package com.kiemhiep.platform;

import com.kiemhiep.Kiemhiep;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Optional;

/**
 * Registers skill items by item_id and resolves item_id to Item.
 * Uses Identifier / ResourceKey (Fabric 1.21) directly.
 */
public final class SkillItemRegistrationHelper {

    private SkillItemRegistrationHelper() {}

    /**
     * Resolve "namespace:path" item_id to a registered Item.
     *
     * @param itemId "namespace:path" (e.g. "kiemhiep:skill_fireball")
     * @return the Item if registered and not air, otherwise empty
     */
    public static Optional<Item> getItemById(String itemId) {
        if (itemId == null || !itemId.contains(":")) return Optional.empty();
        try {
            String[] parts = itemId.split(":", 2);
            if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) return Optional.empty();
            Identifier id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
            Optional<Item> opt = BuiltInRegistries.ITEM.getOptional(id);
            if (opt == null || opt.isEmpty()) return Optional.empty();
            Item item = opt.get();
            if (item == net.minecraft.world.item.Items.AIR) return Optional.empty();
            return Optional.of(item);
        } catch (Exception e) {
            Kiemhiep.LOGGER.debug("Failed to resolve skill item: {}", itemId, e);
            return Optional.empty();
        }
    }

    /**
     * Register a single item in the game registry if not already present.
     *
     * @param itemId "namespace:path" (e.g. "kiemhiep:skill_fireball")
     * @return true if registered or already present, false on error
     */
    public static boolean registerItem(String itemId) {
        if (itemId == null || !itemId.contains(":")) {
            Kiemhiep.LOGGER.warn("Invalid item ID format: {}", itemId);
            return false;
        }

        try {
            // Parse namespace và path từ itemId
            String[] parts = itemId.split(":", 2);
            String namespace = parts[0];
            String path = parts[1];

            // Tạo Identifier cho Fabric 1.21
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);

            // Kiểm tra xem item đã được register chưa
            if (BuiltInRegistries.ITEM.containsKey(id)) {
                Kiemhiep.LOGGER.info("Item {} already registered", itemId);
                return true;
            }

            // Tạo ResourceKey cho item (Fabric 1.21.2+)
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

            // Tạo item mới với properties
            Item item = new Item(new Item.Properties().setId(itemKey));

            // Register item vào registry
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);

            Kiemhiep.LOGGER.info("Successfully registered skill item: {}", itemId);
            return true;

        } catch (Exception e) {
            Kiemhiep.LOGGER.warn("Failed to register skill item: {}", itemId, e);
            return false;
        }
    }
}
