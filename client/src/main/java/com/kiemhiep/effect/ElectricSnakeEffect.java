package com.kiemhiep.effect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom effect cho Electric Snake.
 * Electric loop effect với snake pattern.
 */
public class ElectricSnakeEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger("kiemhiep");

    private static float progress;
    private static long startTime;
    private static boolean active;
    private static double effectX;
    private static double effectY;
    private static double effectZ;

    private static final int DURATION_MS = 4000;

    public static void init() {
        LOGGER.debug("ElectricSnakeEffect initialized");
    }

    public static void activate(double x, double y, double z) {
        effectX = x;
        effectY = y;
        effectZ = z;
        progress = 0f;
        startTime = System.currentTimeMillis();
        active = true;
        LOGGER.debug("ElectricSnakeEffect activated at ({}, {}, {})", x, y, z);
    }

    public static void render() {
        if (!active) return;

        long elapsed = System.currentTimeMillis() - startTime;
        progress = Math.min(1.0f, (float) elapsed / DURATION_MS);

        if (progress >= 1.0f) {
            active = false;
            LOGGER.debug("ElectricSnakeEffect completed");
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static float getProgress() {
        return progress;
    }

    public static double getEffectX() {
        return effectX;
    }

    public static double getEffectY() {
        return effectY;
    }

    public static double getEffectZ() {
        return effectZ;
    }
}
