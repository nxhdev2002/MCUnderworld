package com.kiemhiep.hud;

import com.kiemhiep.shader.ElementalShaderState;
import com.kiemhiep.shader.ShaderManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overlay hiệu ứng nguyên tố khi dùng skill (fire/ice/lightning/earth/wind/poison).
 * Gọi ShaderManager.getShaderByType(); nếu shader = null thì vẽ overlay màu fade (fallback).
 */
public final class ElementalShaderOverlay {

    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    /** Alpha cao (~70%) để dễ thấy overlay khi dùng skill. */
    private static final int FIRE_COLOR   = 0xB0FF6600; // cam/đỏ
    private static final int ICE_COLOR    = 0xB066CCFF; // xanh băng
    private static final int LIGHTNING_COLOR = 0xB0FFFF88; // vàng chớp
    private static final int EARTH_COLOR  = 0xB0996633; // nâu đất
    private static final int WIND_COLOR   = 0xB0AAAAAA; // xám gió
    private static final int POISON_COLOR = 0xB0880088; // tím độc

    /** Chỉ log một lần mỗi lần kích hoạt (tránh spam mỗi frame). */
    private static String lastLoggedEffectType;
    private static long lastLoggedTime;

    private ElementalShaderOverlay() {}

    public static void register() {
        HudRenderCallback.EVENT.register(ElementalShaderOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        if (!ElementalShaderState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        String effectType = ElementalShaderState.getActiveEffectType();
        if (effectType == null) return;

        // Gọi ShaderManager để (sau này) áp shader; hiện tại trả về null nên dùng overlay màu
        Object shader = ShaderManager.getShaderByType(effectType);
        if (shader != null) {
            // TODO: khi loadShader() được implement, áp dụng post-process shader tại đây
            // ((PostEffectProcessor) shader).render(...);
            return;
        }

        float progress = ElementalShaderState.getProgress();
        int color = getColorForType(effectType, progress);
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        graphics.fill(0, 0, w, h, color);
        // Log tối đa 1 lần mỗi lần kích hoạt (progress < 0.05 và chưa log effect này trong 1.5s)
        if (progress < 0.05f) {
            long now = System.currentTimeMillis();
            if (!effectType.equals(lastLoggedEffectType) || (now - lastLoggedTime) > 1500) {
                LOGGER.info("[ElementalShaderOverlay] active effectType={} size={}x{}", effectType, w, h);
                lastLoggedEffectType = effectType;
                lastLoggedTime = now;
            }
        } else {
            lastLoggedEffectType = null; // reset để lần activate sau log lại
        }
        LOGGER.debug("[ElementalShaderOverlay] drawing effectType={} progress={} size={}x{} color=0x{}", effectType, progress, w, h, Integer.toHexString(color));
    }

    private static int getColorForType(String effectType, float progress) {
        int base = switch (effectType) {
            case "fire" -> FIRE_COLOR;
            case "ice" -> ICE_COLOR;
            case "lightning", "thunder" -> LIGHTNING_COLOR; // server gửi "thunder" cho ThunderSkill
            case "earth" -> EARTH_COLOR;
            case "wind" -> WIND_COLOR;
            case "poison" -> POISON_COLOR;
            default -> 0x20000000;
        };
        // Flash mạnh lúc đầu (progress < 0.15), sau đó fade
        int baseAlpha = (base >> 24) & 0xFF;
        float alphaMul = progress < 0.15f ? 1f : (1f - (progress - 0.15f) / 0.85f * 0.8f);
        int alpha = (int) (baseAlpha * alphaMul);
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        return (alpha << 24) | (base & 0x00FFFFFF);
    }
}
