package com.kiemhiep.shader;

/**
 * Trạng thái hiệu ứng shader nguyên tố đang active (khi dùng skill fire/ice/lightning/earth/wind/poison).
 * SkillEffectReceiver gọi activate(); overlay đọc isActive() và thời gian còn lại để vẽ.
 */
public final class ElementalShaderState {

    private static final int DEFAULT_DURATION_MS = 2000;

    private static String activeEffectType;
    private static long activeUntil;
    private static long activeStart;
    private static int activeDurationMs;

    private ElementalShaderState() {}

    /**
     * Kích hoạt hiệu ứng shader nguyên tố trong một khoảng thời gian.
     */
    public static void activate(String effectType, int durationMs) {
        activeEffectType = effectType;
        activeStart = System.currentTimeMillis();
        activeDurationMs = durationMs;
        activeUntil = activeStart + durationMs;
    }

    /**
     * Kích hoạt với thời gian mặc định (2 giây).
     */
    public static void activate(String effectType) {
        activate(effectType, DEFAULT_DURATION_MS);
    }

    public static boolean isActive() {
        if (activeEffectType == null) return false;
        if (System.currentTimeMillis() >= activeUntil) {
            activeEffectType = null;
            return false;
        }
        return true;
    }

    /**
     * Loại hiệu ứng đang active (fire, ice, lightning, earth, wind, poison) hoặc null.
     */
    public static String getActiveEffectType() {
        if (!isActive()) return null;
        return activeEffectType;
    }

    /**
     * Tiến độ 0..1 (0 = vừa bắt đầu, 1 = sắp hết). Dùng để fade out overlay.
     */
    public static float getProgress() {
        if (activeEffectType == null || activeDurationMs <= 0) return 1f;
        long now = System.currentTimeMillis();
        if (now >= activeUntil) return 1f;
        long elapsed = now - activeStart;
        return Math.min(1f, (float) elapsed / activeDurationMs);
    }
}
