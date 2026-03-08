package com.kiemhiep.platform;

import com.kiemhiep.Kiemhiep;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Registers skill items by item_id and resolves item_id to Item using reflection.
 * Resolves the ID type (ResourceLocation/Identifier) from the registry at runtime so it works
 * with any mapping (Mojang, Yarn, or Intermediary).
 */
public final class SkillItemRegistrationHelper {

    /** Resolved ID class and parse/create strategy; null until first use or if resolution failed. */
    private static volatile IdStrategy idStrategy;
    /** True after we determined the ID type could not be resolved. */
    private static volatile boolean resolutionFailed;

    private SkillItemRegistrationHelper() {}

    /**
     * Resolve ID class from an existing registry entry: getKey(item).location() gives us
     * the runtime type for namespace:path IDs without relying on class name strings.
     */
    private static IdStrategy resolveIdStrategy() {
        if (resolutionFailed) return null;
        IdStrategy s = idStrategy;
        if (s != null) return s;
        synchronized (SkillItemRegistrationHelper.class) {
            if (resolutionFailed) return null;
            s = idStrategy;
            if (s != null) return s;
            try {
                Object registry = BuiltInRegistries.ITEM;
                Class<?> registryClass = registry.getClass();
                // getResourceKey(Item) or getKey(Item) -> ResourceKey<Item>
                Method getKeyMethod = null;
                for (String name : new String[] { "getResourceKey", "getKey" }) {
                    try {
                        getKeyMethod = registryClass.getMethod(name, Item.class);
                        break;
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                if (getKeyMethod == null) throw new NoSuchMethodException("getResourceKey or getKey");
                Object key = getKeyMethod.invoke(registry, net.minecraft.world.item.Items.STONE);
                if (key == null) throw new IllegalStateException("key is null");
                // key.location() or key.getValue() -> ResourceLocation / Identifier
                Method locationMethod = null;
                for (String name : new String[] { "location", "getValue" }) {
                    try {
                        locationMethod = key.getClass().getMethod(name);
                        break;
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                if (locationMethod == null) throw new NoSuchMethodException("location or getValue");
                Object sampleId = locationMethod.invoke(key);
                if (sampleId == null) throw new IllegalStateException("location is null");
                Class<?> idClass = sampleId.getClass();
                // Create from string: parse(String) or of(String, String)
                Method parseOrOf = null;
                try {
                    parseOrOf = idClass.getMethod("parse", String.class);
                } catch (NoSuchMethodException ignored) {
                }
                if (parseOrOf == null) {
                    try {
                        parseOrOf = idClass.getMethod("of", String.class, String.class);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                if (parseOrOf == null) throw new NoSuchMethodException("parse or of");
                final Method createMethod = parseOrOf;
                final boolean takesTwoArgs = createMethod.getParameterCount() == 2;
                IdCreator creator = (cls, itemId) -> {
                    if (takesTwoArgs) {
                        int colon = itemId.indexOf(':');
                        if (colon <= 0 || colon == itemId.length() - 1) return null;
                        return createMethod.invoke(null, itemId.substring(0, colon), itemId.substring(colon + 1));
                    }
                    return createMethod.invoke(null, itemId);
                };
                idStrategy = new IdStrategy(idClass, creator);
                return idStrategy;
            } catch (Exception e) {
                Kiemhiep.LOGGER.warn("Could not resolve registry ID type for skill items: {}", e.getMessage());
                resolutionFailed = true;
                return null;
            }
        }
    }

    private static final class IdStrategy {
        final Class<?> idClass;
        final IdCreator creator;

        IdStrategy(Class<?> idClass, IdCreator creator) {
            this.idClass = idClass;
            this.creator = creator;
        }
    }

    private interface IdCreator {
        Object create(Class<?> idClass, String itemId) throws Exception;
    }

    /**
     * Resolve "namespace:path" item_id to a registered Item.
     *
     * @param itemId "namespace:path" (e.g. "kiemhiep:skill_fireball")
     * @return the Item if registered and not air, otherwise empty
     */
    public static Optional<Item> getItemById(String itemId) {
        if (itemId == null || !itemId.contains(":")) return Optional.empty();
        IdStrategy strategy = resolveIdStrategy();
        if (strategy == null) return Optional.empty();
        try {
            Object id = strategy.creator.create(strategy.idClass, itemId);
            if (id == null) return Optional.empty();
            Method getOptional = BuiltInRegistries.ITEM.getClass().getMethod("getOptional", strategy.idClass);
            @SuppressWarnings("unchecked")
            Optional<Item> opt = (Optional<Item>) getOptional.invoke(BuiltInRegistries.ITEM, id);
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
