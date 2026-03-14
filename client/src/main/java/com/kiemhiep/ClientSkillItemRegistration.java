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

    /** Skill item IDs registered by server (from seed). Client must register the same set.
     *  Synced with db/migration V4, V5, V6, V7. */
    private static final String[] SKILL_ITEM_IDS = {
        "kiemhiep:skill_fireball",
        "kiemhiep:skill_fire_blast",
        "kiemhiep:skill_fire_storm",
        "kiemhiep:skill_fire_wall",
        "kiemhiep:skill_ice_shard",
        "kiemhiep:skill_frost_wave",
        "kiemhiep:skill_ice_crystal",
        "kiemhiep:skill_lightning_bolt",
        "kiemhiep:skill_chain_lightning",
        "kiemhiep:skill_thunder_storm",
        "kiemhiep:skill_earth_spike",
        "kiemhiep:skill_tremor",
        "kiemhiep:skill_earth_barrier",
        "kiemhiep:skill_wind_cut",
        "kiemhiep:skill_cyclone",
        "kiemhiep:skill_sonic_boom",
        "kiemhiep:skill_poison_dart",
        "kiemhiep:skill_poison_cloud",
        "kiemhiep:skill_venom_web",
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
        "kiemhiep:skill_lightning_stab",
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
        "kiemhiep:skill_cursed_gas",
        "kiemhiep:skill_zilean_bomb",
        "kiemhiep:skill_viktor_sentry",
        "kiemhiep:skill_soraka_star",
        "kiemhiep:skill_ward_blue",
        "kiemhiep:skill_ward_red",
        "kiemhiep:skill_owl_spirit",
        "kiemhiep:skill_wolf_beast",
        "kiemhiep:skill_phoenix_flame",
        "kiemhiep:skill_crab_summon",
        "kiemhiep:skill_bear_summon",
        "kiemhiep:skill_void_spawn",
        "kiemhiep:skill_ekko_zdrive",
        "kiemhiep:skill_dark_rift",
        "kiemhiep:skill_quantum_ray",
        "kiemhiep:skill_shield_summon",
        "kiemhiep:skill_jinx_chompers",
        "kiemhiep:skill_mf_rain",
        "kiemhiep:skill_lux_spike",
        "kiemhiep:skill_frozen_cage",
        "kiemhiep:skill_electric_snake"
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
