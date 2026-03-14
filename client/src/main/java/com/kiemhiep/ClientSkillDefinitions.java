package com.kiemhiep;

import com.kiemhiep.api.model.SkillDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-side storage for skill definitions received from server.
 * Used for displaying tooltips on skill items.
 */
public final class ClientSkillDefinitions {

    private static final Map<String, SkillDefinition> BY_ITEM_ID = new HashMap<>();
    private static final Map<String, SkillDefinition> BY_SKILL_ID = new HashMap<>();

    private ClientSkillDefinitions() {}

    /**
     * Store all skill definitions from the server payload.
     */
    public static void setDefinitions(List<com.kiemhiep.network.SkillDefinitionsPayload.SkillDefinitionData> dataList) {
        BY_ITEM_ID.clear();
        BY_SKILL_ID.clear();

        for (var data : dataList) {
            SkillDefinition def = data.toSkillDefinition();
            BY_ITEM_ID.put(def.itemId(), def);
            // Use lowercase key so Identifier lookups (which require [a-z0-9/._-]) always match
            BY_SKILL_ID.put(def.skillId() != null ? def.skillId().toLowerCase() : def.skillId(), def);
        }
    }

    /**
     * Get skill definition by item ID (e.g., "kiemhiep:skill_fireball").
     */
    public static SkillDefinition getByItemId(String itemId) {
        return BY_ITEM_ID.get(itemId);
    }

    /**
     * Get skill definition by skill ID (e.g., "skill_fireball").
     */
    public static SkillDefinition getBySkillId(String skillId) {
        return skillId != null ? BY_SKILL_ID.get(skillId.toLowerCase()) : null;
    }

    /**
     * Get skill ID from a skill item stack.
     */
    public static Identifier getSkillId(ItemStack stack) {
        if (stack.isEmpty()) return null;
        var itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return null;
        String itemId = itemKey.getNamespace() + ":" + itemKey.getPath();
        SkillDefinition def = BY_ITEM_ID.get(itemId);
        if (def == null) return null;
        String sid = def.skillId();
        if (sid == null) return null;
        // Resource locations must use only [a-z0-9/._-]; normalize to avoid InvalidIdentifierException
        int colon = sid.indexOf(':');
        String namespace = colon >= 0 ? sid.substring(0, colon).toLowerCase() : "kiemhiep";
        String path = (colon >= 0 ? sid.substring(colon + 1) : sid).toLowerCase();
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    /**
     * Get skill definition from a skill item stack.
     * BY_SKILL_ID is keyed by lowercase skill_id (often path-only from server).
     */
    public static SkillDefinition getDefinition(Identifier skillId) {
        if (skillId == null) return null;
        String key = skillId.toString().toLowerCase();
        SkillDefinition def = BY_SKILL_ID.get(key);
        if (def == null && "kiemhiep".equals(skillId.getNamespace())) {
            def = BY_SKILL_ID.get(skillId.getPath());
        }
        return def;
    }

    /**
     * Get skill definition by skill ID string (e.g. "skill_fireball" or "kiemhiep:skill_fireball").
     */
    public static SkillDefinition getDefinition(String skillId) {
        return skillId != null ? BY_SKILL_ID.get(skillId.toLowerCase()) : null;
    }

    /**
     * Check if any skill definitions have been loaded.
     */
    public static boolean isLoaded() {
        return !BY_ITEM_ID.isEmpty();
    }

    /**
     * Clear all cached definitions.
     */
    public static void clear() {
        BY_ITEM_ID.clear();
        BY_SKILL_ID.clear();
    }
}