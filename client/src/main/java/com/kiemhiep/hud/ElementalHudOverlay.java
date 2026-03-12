package com.kiemhiep.hud;

import com.kiemhiep.ClientSkillCooldowns;
import com.kiemhiep.ClientSkillDefinitions;
import com.kiemhiep.api.model.SkillDefinition;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * HUD overlay cho hiệu ứng kỹ năng nguyên tố.
 * Vẽ biểu tượng kỹ năng với màu nguyên tố, cooldown overlay, và hiệu ứng khi đang unused.
 */
public final class ElementalHudOverlay {

    private ElementalHudOverlay() {}

    public static void register() {
        HudRenderCallback.EVENT.register(ElementalHudOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.gameMode == null) return;

        // Get equipped skill items from hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                Identifier skillId = ClientSkillDefinitions.getSkillId(stack);
                if (skillId != null) {
                    SkillDefinition definition = ClientSkillDefinitions.getDefinition(skillId);
                    if (definition != null) {
                        renderSkillSlot(graphics, i, definition, stack);
                    }
                }
            }
        }
    }

    private static void renderSkillSlot(GuiGraphics graphics, int slotIndex, SkillDefinition definition, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        int screenX = mc.getWindow().getGuiScaledWidth() / 2 - 90 + slotIndex * 20 + 2;
        int screenY = mc.getWindow().getGuiScaledHeight() - 22 + 2;

        // Vẽ overlay cooldown nếu đang cooldown
        double cooldownProgress = ClientSkillCooldowns.getCooldownProgress(definition.skillId());
        if (cooldownProgress > 0) {
            int width = 16;
            int height = 16;
            int color = getCooldownColor(definition.elementalType());
            graphics.fill(screenX, screenY, screenX + width, screenY + height, color);
        }

        // Vẽ boundary color theo nguyên tố
        int boundaryColor = getBoundaryColor(definition.elementalType());
        graphics.fill(screenX - 1, screenY - 1, screenX + 17, screenY + 17, boundaryColor);
    }

    /**
     * Get boundary color based on elemental type.
     */
    public static int getBoundaryColor(String elementalType) {
        if (elementalType == null) return 0xFFFFFFFF;
        return switch (elementalType) {
            case "Hỏa", "fire" -> 0xFFFF4500; // Orange-Red
            case "Thủy", "ice", "water" -> 0xFF00BFFF; // Deep Sky Blue
            case "Lôi", "lightning" -> 0xFF00FFFF; // Cyan
            case "Thổ", "earth" -> 0xFF8B4513; // Saddle Brown
            case "Phong", "wind" -> 0xFF87CEEB; // Sky Blue
            case "Độc", "poison" -> 0xFF9ACD32; // Dark Yellow-Green
            default -> 0xFFFFFFFF; // White
        };
    }

    /**
     * Get cooldown overlay color based on elemental type.
     */
    public static int getCooldownColor(String elementalType) {
        if (elementalType == null) return 0x80FFFFFF;
        return switch (elementalType) {
            case "Hỏa", "fire" -> 0x80FF4500; // Semi-transparent Orange-Red
            case "Thủy", "ice", "water" -> 0x8000BFFF; // Semi-transparent Deep Sky Blue
            case "Lôi", "lightning" -> 0x8000FFFF; // Semi-transparent Cyan
            case "Thổ", "earth" -> 0x808B4513; // Semi-transparent Saddle Brown
            case "Phong", "wind" -> 0x8087CEEB; // Semi-transparent Sky Blue
            case "Độc", "poison" -> 0x809ACD32; // Semi-transparent Dark Yellow-Green
            default -> 0x80FFFFFF; // Semi-transparent White
        };
    }

    /**
     * Render elemental type icon next to skill in tooltip.
     */
    public static void renderElementalIcon(GuiGraphics graphics, int x, int y, String elementalType) {
        int color = getBoundaryColor(elementalType);
        // Vẽ icon hình tròn nhỏ với màu nguyên tố
        graphics.fill(x, y, x + 8, y + 8, color);
    }

    /**
     * Get display name for elemental type.
     */
    public static Component getElementalDisplayName(String elementalType) {
        if (elementalType == null) elementalType = "none";
        return switch (elementalType) {
            case "Hỏa", "fire" -> Component.translatable("elemental_type.kiemhiep.fire");
            case "Thủy", "ice", "water" -> Component.translatable("elemental_type.kiemhiep.ice");
            case "Lôi", "lightning" -> Component.translatable("elemental_type.kiemhiep.lightning");
            case "Thổ", "earth" -> Component.translatable("elemental_type.kiemhiep.earth");
            case "Phong", "wind" -> Component.translatable("elemental_type.kiemhiep.wind");
            case "Độc", "poison" -> Component.translatable("elemental_type.kiemhiep.poison");
            default -> Component.translatable("elemental_type.kiemhiep.none");
        };
    }
}
