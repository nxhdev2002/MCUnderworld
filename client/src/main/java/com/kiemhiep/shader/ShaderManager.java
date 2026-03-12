package com.kiemhiep.shader;

import java.io.Closeable;

/**
 * Manager cho các shader hiệu ứng nguyên tố.
 * Shader loading stubbed for 1.21 compatibility; override when ShaderProgram/API is available.
 */
public final class ShaderManager {

    private ShaderManager() {}

    private static Object fireShader;
    private static Object iceShader;
    private static Object lightningShader;
    private static Object earthShader;
    private static Object windShader;
    private static Object poisonShader;

    /**
     * Release all shaders.
     */
    public static void close() {
        closeQuietly(fireShader);
        closeQuietly(iceShader);
        closeQuietly(lightningShader);
        closeQuietly(earthShader);
        closeQuietly(windShader);
        closeQuietly(poisonShader);
        fireShader = null;
        iceShader = null;
        lightningShader = null;
        earthShader = null;
        windShader = null;
        poisonShader = null;
    }

    private static void closeQuietly(Object shader) {
        if (shader instanceof Closeable c) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static Object getFireShader() {
        if (fireShader == null) {
            fireShader = loadShader("kiemhiep:core/fire");
        }
        return fireShader;
    }

    public static Object getIceShader() {
        if (iceShader == null) {
            iceShader = loadShader("kiemhiep:core/ice");
        }
        return iceShader;
    }

    public static Object getLightningShader() {
        if (lightningShader == null) {
            lightningShader = loadShader("kiemhiep:core/lightning");
        }
        return lightningShader;
    }

    public static Object getEarthShader() {
        if (earthShader == null) {
            earthShader = loadShader("kiemhiep:core/earth");
        }
        return earthShader;
    }

    public static Object getWindShader() {
        if (windShader == null) {
            windShader = loadShader("kiemhiep:core/wind");
        }
        return windShader;
    }

    public static Object getPoisonShader() {
        if (poisonShader == null) {
            poisonShader = loadShader("kiemhiep:core/poison");
        }
        return poisonShader;
    }

    /**
     * Load a shader from resources. Returns null until 1.21 shader API is wired.
     */
    private static Object loadShader(String location) {
        return null;
    }
}
