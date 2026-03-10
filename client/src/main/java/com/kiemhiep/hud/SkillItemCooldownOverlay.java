package com.kiemhiep.hud;

import com.kiemhiep.ClientSkillCooldowns;
import com.kiemhiep.ClientSkillItemRegistration;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * HUD overlay showing cooldown on skill items in hotbar and inventory.
 * Renders a dark overlay with countdown text on items that are on cooldown.
 */
public final class SkillItemCooldownOverlay {

    private static final int MARGIN = 8;
    /** Color for cooldown overlay (black with varying alpha). */
    private static final int OVERLAY_COLOR = 0x80_00_00_00;
    /** Color for cooldown text (white). */
    private static final int TEXT_COLOR = 0xFF_FF_FF_FF;
    /** Color for countdown text when almost ready (green). */
    private static final int READY_COLOR = 0xFF_00_FF_00;

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int INVENTORY_SLOT_COUNT = 27;

    /** Slot size in pixels. */
    private static final int SLOT_SIZE = 18;
    /** Spacing between slots. */
    private static final int SLOT_SPACING = 18;

    private SkillItemCooldownOverlay() {}

    public static void register() {
        HudRenderCallback.EVENT.register(SkillItemCooldownOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Tick cooldowns to remove expired ones
        ClientSkillCooldowns.tick();

        // Render on hotbar (always visible)
        renderHotbarCooldowns(graphics, mc);

        // Render on open inventory screen
        if (mc.screen != null) {
            String screenName = mc.screen.getClass().getSimpleName();
            if (screenName.contains("Inventory") || screenName.contains("Creative") || screenName.contains("Chest")) {
                renderInventoryCooldowns(graphics, mc);
            }
        }
    }

    private static void renderHotbarCooldowns(GuiGraphics graphics, Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Hotbar position (vanilla Minecraft)
        int hotbarX = screenWidth / 2 - 91;
        int hotbarY = screenHeight - 22;

        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (isSkillItem(stack)) {
                String skillId = getSkillId(stack);
                if (ClientSkillCooldowns.isOnCooldown(skillId)) {
                    int slotX = hotbarX + i * SLOT_SPACING;
                    int slotY = hotbarY;
                    renderCooldownOverlay(graphics, mc, slotX, slotY, skillId);
                }
            }
        }
    }

    private static void renderInventoryCooldowns(GuiGraphics graphics, Minecraft mc) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Inventory screen position (approximate vanilla positions)
        int inventoryX = screenWidth / 2 - 176 / 2;
        int inventoryY = screenHeight / 2 - 166 / 2;

        // Main inventory slots (3 rows x 9 columns, starting at row 4)
        int inventorySlotStartX = inventoryX + 8;
        int inventorySlotStartY = inventoryY + 84;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col + 9; // Skip hotbar (0-8)
                ItemStack stack = mc.player.getInventory().getItem(slotIndex);
                if (isSkillItem(stack)) {
                    String skillId = getSkillId(stack);
                    if (ClientSkillCooldowns.isOnCooldown(skillId)) {
                        int slotX = inventorySlotStartX + col * SLOT_SPACING;
                        int slotY = inventorySlotStartY + row * SLOT_SPACING;
                        renderCooldownOverlay(graphics, mc, slotX, slotY, skillId);
                    }
                }
            }
        }
    }

    private static boolean isSkillItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return false;
        String idStr = itemId.getNamespace() + ":" + itemId.getPath();
        return idStr.startsWith("kiemhiep:skill_");
    }

    private static String getSkillId(ItemStack stack) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId.getNamespace() + ":" + itemId.getPath();
    }

    private static void renderCooldownOverlay(GuiGraphics graphics, Minecraft mc, int slotX, int slotY, String skillId) {
        long remainingMillis = ClientSkillCooldowns.getRemainingMillis(skillId);
        float remainingSeconds = remainingMillis / 1000.0f;

        // Draw semi-transparent overlay
        graphics.fill(slotX + 1, slotY + 1, slotX + SLOT_SIZE - 1, slotY + SLOT_SIZE - 1, OVERLAY_COLOR);

        // Draw cooldown text (countdown in seconds)
        String cooldownText = remainingSeconds < 1 ? String.format("%.1f", remainingSeconds) : String.valueOf((int) Math.ceil(remainingSeconds));
        int textWidth = mc.font.width(cooldownText);
        int textX = slotX + (SLOT_SIZE - textWidth) / 2;
        int textY = slotY + (SLOT_SIZE - mc.font.lineHeight) / 2 + 1;

        // Change color when almost ready (< 1 second)
        int textColor = remainingSeconds < 1 ? READY_COLOR : TEXT_COLOR;
        graphics.drawString(mc.font, cooldownText, textX, textY, textColor, true);
    }
}
