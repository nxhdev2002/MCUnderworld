package com.kiemhiep.effect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quản lý hiệu ứng bầu trời tách đôi (sky split) khi dùng meteor skill.
 * Hiệu ứng tạo vết nứt không gian hình chữ V với ánh sáng đỏ/cam ở giữa.
 */
public class SkySplitEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    private static float splitCenterX;
    private static float splitCenterY;
    private static float progress;
    private static long startTime;
    private static boolean active;

    private static final int DURATION_MS = 4000;
    private static final float SPLIT_WIDTH = 0.15f;
    private static final float SPLIT_LENGTH = 1.5f;

    public static void init() {
        LOGGER.debug("SkySplitEffect initialized");
    }

    /**
     * Kích hoạt hiệu ứng sky split.
     * @param x Vị trí X của meteor (world coordinates)
     * @param y Vị trí Y của meteor
     * @param z Vị trí Z của meteor
     */
    public static void activate(double x, double y, double z) {
        // Chuyển world coordinates sang screen coordinates
        splitCenterX = 0.5f; // Center of screen
        splitCenterY = 0.3f; // Upper portion of screen (sky)

        progress = 0f;
        startTime = System.currentTimeMillis();
        active = true;

        LOGGER.debug("SkySplitEffect activated at ({}, {}, {})", x, y, z);
    }

    /**
     * Update progress và render effect.
     * Called mỗi frame khi active.
     */
    public static void render() {
        if (!active) return;

        long elapsed = System.currentTimeMillis() - startTime;
        progress = Math.min(1.0f, (float) elapsed / DURATION_MS);

        if (progress >= 1.0f) {
            active = false;
            LOGGER.debug("SkySplitEffect completed");
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static float getProgress() {
        return progress;
    }

    public static float getSplitCenterX() {
        return splitCenterX;
    }

    public static float getSplitCenterY() {
        return splitCenterY;
    }

    public static float getSplitWidth() {
        return SPLIT_WIDTH;
    }

    public static float getSplitLength() {
        return SPLIT_LENGTH;
    }

    public static float getTime() {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }
}
