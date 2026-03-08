package com.kiemhiep.core.skill;

import com.kiemhiep.api.skill.ISkill;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps behavior_id (from DB) to skill instance. Register at mod init; SkillManager gets instance here.
 */
public final class SkillRegistry {

    private static final Map<String, ISkill> BY_BEHAVIOR_ID = new ConcurrentHashMap<>();

    private SkillRegistry() {}

    public static void register(String behaviorId, ISkill skill) {
        if (behaviorId == null || skill == null) {
            throw new IllegalArgumentException("behaviorId and skill must be non-null");
        }
        BY_BEHAVIOR_ID.put(behaviorId, skill);
    }

    public static Optional<ISkill> get(String behaviorId) {
        return Optional.ofNullable(BY_BEHAVIOR_ID.get(behaviorId));
    }

    public static void clear() {
        BY_BEHAVIOR_ID.clear();
    }
}
