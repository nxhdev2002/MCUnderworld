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
        "kiemhiep:skill_fireball",
        "kiemhiep:skill_inferno_fist",
        "kiemhiep:skill_solar_flare",
        "kiemhiep:skill_dragon_breath",
        "kiemhiep:skill_blazing_sword",
        "kiemhiep:skill_pyroclastic_flow",
        "kiemhiep:skill_glacier_spike",
        "kiemhiep:skill_cryo_blast",
        "kiemhiep:skill_arctic_wind",
        "kiemhiep:skill_ice_prison",
        "kiemhiep:skill_thunder_fang",
        "kiemhiep:skill_electro_wave",
        "kiemhiep:skill_raging_thunder",
        "kiemhiep:skill_chain_thrust",
        "kiemhiep:skill_vajra_lightning",
        "kiemhiep:skill_stone_fist",
        "kiemhiep:skill_mud_wall",
        "kiemhiep:skill_seismic_pulse",
        "kiemhiep:skill_earth_golem",
        "kiemhiep:skill_quake_stomp",
        "kiemhiep:skill_gale_sword",
        "kiemhiep:skill_sand_storm",
        "kiemhiep:skill_vacuum_cut",
        "kiemhiep:skill_tornado_sweep",
        "kiemhiep:skill_sonic_slicer",
        "kiemhiep:skill_black_spark",
        "kiemhiep:skill_acid_rain",
        "kiemhiep:skill_miasma_blast",
        "kiemhiep:skill_transformation",
        "kiemhiep:skill_cursed_gas"
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
