package com.kiemhiep.hud;

import com.kiemhiep.ClientPlayerStats;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * HUD overlay showing Level and current Mana. Uses HudRenderCallback so text renders correctly.
 */
public final class LevelManaHud {

    private static final int MARGIN = 8;
    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 10;
    /** White with full opacity (ARGB). */
    private static final int TEXT_COLOR = 0xFF_FF_FF_FF;
    private static final int BG_COLOR = 0x80_00_00_00;

    private LevelManaHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register(LevelManaHud::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        var font = mc.font;
        int x = MARGIN;
        int y = MARGIN;

        int level = ClientPlayerStats.getLevel();
        int currentMana = ClientPlayerStats.getCurrentMana();
        int maxMana = ClientPlayerStats.getMaxMana();

        String line1 = "Level: " + level;
        String line2 = "Mana: " + currentMana + " / " + maxMana;
        int w = Math.max(font.width(line1), font.width(line2));
        int h = LINE_HEIGHT * 2;

        graphics.fill(x - PADDING, y - PADDING, x + w + PADDING, y + h + PADDING, BG_COLOR);
        graphics.drawString(font, line1, x, y, TEXT_COLOR, false);
        graphics.drawString(font, line2, x, y + LINE_HEIGHT, TEXT_COLOR, false);
    }
}
