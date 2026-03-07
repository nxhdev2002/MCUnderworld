package com.kiemhiep.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CultivationRealmTest {

    @Test
    void fromLevel_toLevel_roundTrip_boundaries() {
        assertEquals(1, CultivationRealm.fromLevel(1).realm());
        assertEquals(0, CultivationRealm.fromLevel(1).subLevel());
        assertEquals(1, CultivationRealm.toLevel(1, 0));

        CultivationRealm r9 = CultivationRealm.fromLevel(9);
        assertEquals(1, r9.realm());
        assertEquals(8, r9.subLevel());
        assertEquals(9, CultivationRealm.toLevel(1, 8));

        CultivationRealm r10 = CultivationRealm.fromLevel(10);
        assertEquals(2, r10.realm());
        assertEquals(0, r10.subLevel());
        assertEquals(10, CultivationRealm.toLevel(2, 0));

        CultivationRealm r90 = CultivationRealm.fromLevel(90);
        assertEquals(10, r90.realm());
        assertEquals(8, r90.subLevel());
        assertEquals(90, CultivationRealm.toLevel(10, 8));
    }

    @Test
    void fromLevel_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> CultivationRealm.fromLevel(0));
        assertThrows(IllegalArgumentException.class, () -> CultivationRealm.fromLevel(91));
    }

    @Test
    void toLevel_record_consistency() {
        for (int level = 1; level <= CultivationRealm.MAX_LEVEL; level++) {
            CultivationRealm r = CultivationRealm.fromLevel(level);
            assertEquals(level, r.toLevel());
            assertEquals(level, CultivationRealm.toLevel(r.realm(), r.subLevel()));
        }
    }
}
