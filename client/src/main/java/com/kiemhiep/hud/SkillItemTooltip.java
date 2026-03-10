package com.kiemhiep.hud;

import com.kiemhiep.ClientSkillDefinitions;
import com.kiemhiep.api.model.SkillDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Renders skill item tooltips with full skill information.
 * Shows: skill type, mana cost, cooldown, range, effects, and type (AoE/Melee).
 * Injected via ItemTooltipMixin.
 */
public final class SkillItemTooltip {

    private static final String SKILL_PREFIX = "kiemhiep:skill_";

    private SkillItemTooltip() {}

    /**
     * Get tooltip lines for a skill item. Called from ItemTooltipMixin.
     */
    public static void addTooltip(ItemStack stack, @SuppressWarnings("unused") TooltipFlag tooltipFlag, List<Component> lines) {
        // Check if this is a skill item
        if (stack.isEmpty()) return;

        var itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return;

        String itemId = itemKey.getNamespace() + ":" + itemKey.getPath();
        if (!itemId.startsWith(SKILL_PREFIX)) return;

        // Get skill definition from cache
        SkillDefinition def = ClientSkillDefinitions.getByItemId(itemId);
        if (def == null) return;

        // Add skill type (header)
        if (def.skillType() != null && !def.skillType().isEmpty()) {
            lines.add(Component.literal("§6" + def.skillType()).withStyle(ChatFormatting.GOLD));
        }

        // Empty line for separation
        lines.add(Component.literal(""));

        // Effects (if any)
        if (def.effects() != null && !def.effects().isEmpty()) {
            String effectsStr = String.join(", ", def.effects());
            lines.add(Component.literal("§7Effects: §b" + effectsStr).withStyle(ChatFormatting.GRAY));
        }

        // Mana cost
        lines.add(Component.literal("§7Mana: §b" + def.manaCost()).withStyle(ChatFormatting.GRAY));

        // Cooldown
        String cooldownStr = def.getCooldownDisplay();
        lines.add(Component.literal("§7Cooldown: §b" + cooldownStr).withStyle(ChatFormatting.GRAY));

        // Range/radius
        String radiusStr = def.getRadiusDisplay();
        lines.add(Component.literal("§7Range: §b" + radiusStr).withStyle(ChatFormatting.GRAY));

        // Cast time
        String castTimeStr = def.getCastTimeDisplay();
        lines.add(Component.literal("§7Cast Time: §b" + castTimeStr).withStyle(ChatFormatting.GRAY));

        // Type (AoE / Melee / Single)
        String typeStr = getTypeString(def);
        lines.add(Component.literal("§7Type: §b" + typeStr).withStyle(ChatFormatting.GRAY));

        // Consumable indicator
        if (def.consumable()) {
            lines.add(Component.literal("§c§lConsumable").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * Get display string for skill type (AoE/Melee/Single).
     */
    private static String getTypeString(SkillDefinition def) {
        if (def.isAoe() && def.isMelee()) {
            return "AoE, Melee";
        } else if (def.isAoe()) {
            return "AoE";
        } else if (def.isMelee()) {
            return "Melee";
        } else {
            return "Single Target";
        }
    }

    /**
     * Check if this is a skill item (used by other components).
     */
    public static boolean isSkillItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        var itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemKey == null) return false;
        String itemId = itemKey.getNamespace() + ":" + itemKey.getPath();
        return itemId.startsWith(SKILL_PREFIX);
    }
}