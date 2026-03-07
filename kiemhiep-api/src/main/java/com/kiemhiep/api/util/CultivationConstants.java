package com.kiemhiep.api.util;

import com.kiemhiep.api.model.CultivationRealm;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for cultivation-related constants and helpers.
 */
public final class CultivationConstants {

    // Cultivation Realms (10 levels)
    public static final CultivationRealm MORTAL = new CultivationRealm(0, "Mortal", "Người Thường", 0);
    public static final CultivationRealm LUYNH_KHI = new CultivationRealm(1, "Luyện Khí", "Luyện Khí", 8);
    public static final CultivationRealm TRUC_CO = new CultivationRealm(2, "Trúc Cơ", "Trúc Cơ", 8);
    public static final CultivationRealm KIM_DAN = new CultivationRealm(3, "Kim Đan", "Kim Đan", 8);
    public static final CultivationRealm NGUYEN_ANH = new CultivationRealm(4, "Nguyên Anh", "Nguyên Anh", 8);
    public static final CultivationRealm HOA_THAN = new CultivationRealm(5, "Hóa Thần", "Hóa Thần", 8);
    public static final CultivationRealm LUYEN_HU = new CultivationRealm(6, "Luyện Hư", "Luyện Hư", 8);
    public static final CultivationRealm HOP_THE = new CultivationRealm(7, "Hợp Thể", "Hợp Thể", 8);
    public static final CultivationRealm DAI_THUA = new CultivationRealm(8, "Đại Thừa", "Đại Thừa", 8);
    public static final CultivationRealm DO_KIEP = new CultivationRealm(9, "Độ Kiếp", "Độ Kiếp", 8);

    public static final List<CultivationRealm> ALL_REALMS = Arrays.asList(
            MORTAL, LUYNH_KHI, TRUC_CO, KIM_DAN, NGUYEN_ANH,
            HOA_THAN, LUYEN_HU, HOP_THE, DAI_THUA, DO_KIEP
    );

    // Base experience required for each realm level
    public static final int[] BASE_EXP_REQUIRED = {
            0,      // Mortal (no breakthrough needed)
            100,    // Luyện Khí
            500,    // Trúc Cơ
            1500,   // Kim Đan
            5000,   // Nguyên Anh
            15000,  // Hóa Thần
            50000,  // Luyện Hư
            150000, // Hợp Thể
            500000, // Đại Thừa
            1500000 // Độ Kiếp
    };

    private CultivationConstants() {
        // Utility class
    }

    /**
     * Get all cultivation realms.
     */
    public static List<CultivationRealm> getAllRealms() {
        return ALL_REALMS;
    }

    /**
     * Get the realm by level.
     */
    public static CultivationRealm getRealmByLevel(int level) {
        if (level < 0 || level >= ALL_REALMS.size()) {
            return DO_KIEP; // Return max realm if out of bounds
        }
        return ALL_REALMS.get(level);
    }

    /**
     * Get the experience required to reach next sub-level.
     */
    public static int getExpRequired(int realmLevel) {
        if (realmLevel < 0 || realmLevel >= BASE_EXP_REQUIRED.length) {
            return BASE_EXP_REQUIRED[BASE_EXP_REQUIRED.length - 1];
        }
        return BASE_EXP_REQUIRED[realmLevel];
    }

    /**
     * Check if a realm can breakthrough to the next.
     */
    public static boolean canBreakthrough(int realmLevel) {
        return realmLevel < 9; // Can breakthrough if not at max realm
    }

    /**
     * Get the max sub-level for a realm.
     */
    public static int getMaxSubLevel(int realmLevel) {
        if (realmLevel <= 0) {
            return 0; // Mortal has no sub-levels
        }
        return 8; // All other realms have 9 sub-levels (0-8)
    }
}
