package com.kiemhiep.shader;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

/**
 * Manager cho các shader hiệu ứng nguyên tố và skill effect.
 * Elemental (fire, ice, etc.) và các effectType khác (time_bomb, bear_summon, ...) có thể
 * resolve tới post-effect (post_effect/&lt;name&gt;.json). Shader loading stubbed until 1.21 API is wired.
 */
public final class ShaderManager {

    private ShaderManager() {}

    private static Object fireShader;
    private static Object iceShader;
    private static Object lightningShader;
    private static Object earthShader;
    private static Object windShader;
    private static Object poisonShader;

    /** Cache for non-elemental effect types (post-effect by name). Key = post-effect name (e.g. time_bomb, crab_summon). */
    private static final Map<String, Object> effectCache = new ConcurrentHashMap<>();

    /** effectType sent by server -> post-effect file name (when different). */
    private static final Map<String, String> EFFECT_TYPE_ALIASES = Map.of(
        "summon_crab", "crab_summon",
        "meteor", "sky_split"
    );

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
        for (Object v : effectCache.values()) {
            closeQuietly(v);
        }
        effectCache.clear();
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
            fireShader = loadPostEffectByName("fire");
        }
        return fireShader;
    }

    public static Object getIceShader() {
        if (iceShader == null) {
            iceShader = loadPostEffectByName("ice");
        }
        return iceShader;
    }

    public static Object getLightningShader() {
        if (lightningShader == null) {
            lightningShader = loadPostEffectByName("lightning");
        }
        return lightningShader;
    }

    public static Object getEarthShader() {
        if (earthShader == null) {
            earthShader = loadPostEffectByName("earth");
        }
        return earthShader;
    }

    public static Object getWindShader() {
        if (windShader == null) {
            windShader = loadPostEffectByName("wind");
        }
        return windShader;
    }

    public static Object getPoisonShader() {
        if (poisonShader == null) {
            poisonShader = loadPostEffectByName("poison");
        }
        return poisonShader;
    }

    /**
     * Lấy shader/post-effect theo loại hiệu ứng. Elemental dùng 6 loại; các effectType khác
     * (time_bomb, bear_summon, sentry_light, ...) resolve theo tên post_effect/&lt;name&gt;.json (có alias nếu cần).
     */
    public static Object getShaderByType(String effectType) {
        if (effectType == null) return null;
        return switch (effectType) {
            case "fire" -> getFireShader();
            case "ice" -> getIceShader();
            case "lightning", "thunder" -> getLightningShader();
            case "earth" -> getEarthShader();
            case "wind" -> getWindShader();
            case "poison" -> getPoisonShader();
            default -> effectCache.computeIfAbsent(
                EFFECT_TYPE_ALIASES.getOrDefault(effectType, effectType),
                ShaderManager::loadPostEffectByName
            );
        };
    }

    /**
     * Load post-effect by name (identifier kiemhiep:&lt;name&gt;, file post_effect/&lt;name&gt;.json).
     * Uses Minecraft ShaderManager.getPostChain; returns null if not on client or load fails.
     */
    private static Object loadPostEffectByName(String name) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.renderer.ShaderManager loader = mc.getShaderManager();
        if (loader == null) return null;
        try {
            return loader.getPostChain(
                Identifier.fromNamespaceAndPath("kiemhiep", name),
                Set.of(PostChain.MAIN_TARGET_ID)
            );
        } catch (Exception e) {
            return null;
        }
    }
}
