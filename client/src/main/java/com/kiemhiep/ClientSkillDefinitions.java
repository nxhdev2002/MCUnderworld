package com.kiemhiep;

import com.kiemhiep.api.model.SkillDefinition;

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
            BY_SKILL_ID.put(def.skillId(), def);
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
        return BY_SKILL_ID.get(skillId);
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