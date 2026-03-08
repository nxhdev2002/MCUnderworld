package com.kiemhiep.core.skill;

import com.kiemhiep.api.skill.ISkill;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.core.skill.impl.FireballSkill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkillRegistryTest {

    @AfterEach
    void tearDown() {
        SkillRegistry.clear();
    }

    @Test
    void register_and_get() {
        SkillRegistry.register("FIREBALL", FireballSkill.INSTANCE);
        var opt = SkillRegistry.get("FIREBALL");
        assertTrue(opt.isPresent());
        assertSame(FireballSkill.INSTANCE, opt.get());
    }

    @Test
    void get_returnsEmptyForUnknown() {
        assertTrue(SkillRegistry.get("UNKNOWN").isEmpty());
    }

    @Test
    void clear_removesAll() {
        SkillRegistry.register("FIREBALL", FireballSkill.INSTANCE);
        SkillRegistry.clear();
        assertTrue(SkillRegistry.get("FIREBALL").isEmpty());
    }

    @Test
    void register_rejectsNullBehaviorId() {
        assertThrows(IllegalArgumentException.class, () ->
            SkillRegistry.register(null, new ISkill() {
                @Override
                public void execute(SkillContext ctx) {}
            }));
    }

    @Test
    void register_rejectsNullSkill() {
        assertThrows(IllegalArgumentException.class, () -> SkillRegistry.register("X", null));
    }
}
