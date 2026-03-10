package com.kiemhiep.hud;

import com.kiemhiep.effect.SkySplitEffect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * HUD overlay cho hiệu ứng sky split khi dùng meteor skill.
 * Render vết nứt không gian hình chữ V với ánh sáng đỏ/cam ở giữa.
 */
public final class SkySplitOverlay {

    private SkySplitOverlay() {}

    public static void register() {
        HudRenderCallback.EVENT.register(SkySplitOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        if (!SkySplitEffect.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Update effect state
        SkySplitEffect.render();

        float progress = SkySplitEffect.getProgress();
        int screenWidth = mc.getWindow().getScreenWidth();
        int screenHeight = mc.getWindow().getScreenHeight();

        // Vẽ vết nứt màu đỏ/cam ở giữa screen (phần trên - sky)
        float crackAlpha = (int) (255 * 0.7f * (1.0f - progress * 0.3f));

        // Vẽ gradient cho vết nứt hình chữ V
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 3; // 1/3 từ trên xuống (vùng trời)
        int crackWidth = (int) (screenWidth * 0.3f);
        int crackHeight = (int) (screenHeight * 0.4f);

        // Vết nứt chính - hình chữ V
        // Bên trái của V
        for (int i = 0; i < crackHeight; i++) {
            float t = (float) i / crackHeight;
            int xLeft = centerX - (int) (crackWidth * t);
            int xRight = centerX + (int) (crackWidth * t);
            int y = centerY + i;

            // Gradient color từ cam sang đỏ
            int r1 = 255;
            int g1 = (int) (100 + 50 * t);
            int b1 = (int) (50 * (1 - t));
            int a1 = (int) (crackAlpha * (1 - t));

            // Vẽ 2 bên vết nứt
            if (a1 > 0) {
                int color = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                // Line bên trái
                graphics.fill(xLeft - 3, y, xLeft, y + 1, color);
                // Line bên phải
                graphics.fill(xRight, y, xRight + 3, y + 1, color);
            }
        }

        // Darken overlay cho 2 bên trời (effekt bầu trời tối)
        int darkenAlpha = (int) (255 * 0.5f * progress);
        if (darkenAlpha > 0) {
            // Top left darken
            graphics.fill(0, 0, centerX, screenHeight / 2, darkenAlpha << 24);
            // Top right darken
            graphics.fill(centerX, 0, screenWidth, screenHeight / 2, darkenAlpha << 24);
        }

        // Glow effect ở tâm vết nứt
        int glowSize = 20;
        int glowAlpha = (int) (200 * (1 - progress));
        if (glowAlpha > 0) {
            // Gradient glow từ trắng sang cam
            for (int i = 0; i < glowSize; i++) {
                int alpha = glowAlpha * (glowSize - i) / glowSize;
                int r = 255;
                int g = 200 * (glowSize - i) / glowSize;
                int b = 100 * (glowSize - i) / glowSize;
                int color = (alpha << 24) | (r << 16) | (g << 8) | b;
                int size = glowSize - i;
                graphics.fill(centerX - size, centerY - size, centerX + size, centerY + size, color);
            }
        }
    }
}
