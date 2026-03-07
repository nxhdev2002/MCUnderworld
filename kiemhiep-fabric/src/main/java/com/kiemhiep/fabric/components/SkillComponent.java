package com.kiemhiep.fabric.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Skill component for players using Cardinal Components API.
 */
public class SkillComponent implements ComponentV3, AutoSyncedComponent {
    private final Map<String, Integer> learnedSkills = new HashMap<>();
    private final Map<String, Long> skillCooldowns = new HashMap<>();
    private final Set<String> equippedSkills = new HashSet<>();

    public SkillComponent() {
    }

    /**
     * Check if a skill is learned.
     */
    public boolean hasSkill(String skillId) {
        return learnedSkills.containsKey(skillId);
    }

    /**
     * Get skill level.
     */
    public int getSkillLevel(String skillId) {
        return learnedSkills.getOrDefault(skillId, 0);
    }

    /**
     * Learn a new skill.
     */
    public void learnSkill(String skillId) {
        if (!learnedSkills.containsKey(skillId)) {
            learnedSkills.put(skillId, 1);
        }
    }

    /**
     * Level up a skill.
     */
    public void levelUpSkill(String skillId) {
        learnedSkills.merge(skillId, 1, Integer::sum);
    }

    /**
     * Set skill cooldown.
     */
    public void setCooldown(String skillId, long cooldownEndTime) {
        skillCooldowns.put(skillId, cooldownEndTime);
    }

    /**
     * Check if skill is on cooldown.
     */
    public boolean isOnCooldown(String skillId) {
        Long cooldownEnd = skillCooldowns.get(skillId);
        return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
    }

    /**
     * Get remaining cooldown time in milliseconds.
     */
    public long getRemainingCooldown(String skillId) {
        Long cooldownEnd = skillCooldowns.get(skillId);
        if (cooldownEnd == null) return 0;
        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }

    /**
     * Clear skill cooldown.
     */
    public void clearCooldown(String skillId) {
        skillCooldowns.remove(skillId);
    }

    /**
     * Equip a skill.
     */
    public void equipSkill(String skillId) {
        if (hasSkill(skillId)) {
            equippedSkills.add(skillId);
        }
    }

    /**
     * Unequip a skill.
     */
    public void unequipSkill(String skillId) {
        equippedSkills.remove(skillId);
    }

    /**
     * Get equipped skills.
     */
    public Set<String> getEquippedSkills() {
        return new HashSet<>(equippedSkills);
    }

    /**
     * Get all learned skills.
     */
    public Map<String, Integer> getLearnedSkills() {
        return new HashMap<>(learnedSkills);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        // Read learned skills
        learnedSkills.clear();
        NbtList skillsList = tag.getList("LearnedSkills", 10);
        for (int i = 0; i < skillsList.size(); i++) {
            NbtCompound skillTag = skillsList.getCompound(i);
            String skillId = skillTag.getString("Id");
            int level = skillTag.getInt("Level");
            learnedSkills.put(skillId, level);
        }

        // Read equipped skills
        equippedSkills.clear();
        NbtList equippedList = tag.getList("EquippedSkills", 8);
        for (int i = 0; i < equippedList.size(); i++) {
            equippedSkills.add(equippedList.getString(i));
        }

        // Read cooldowns
        skillCooldowns.clear();
        NbtCompound cooldownsTag = tag.getCompound("Cooldowns");
        for (String key : cooldownsTag.getKeys()) {
            skillCooldowns.put(key, cooldownsTag.getLong(key));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        // Write learned skills
        NbtList skillsList = new NbtList();
        for (Map.Entry<String, Integer> entry : learnedSkills.entrySet()) {
            NbtCompound skillTag = new NbtCompound();
            skillTag.putString("Id", entry.getKey());
            skillTag.putInt("Level", entry.getValue());
            skillsList.add(skillTag);
        }
        tag.put("LearnedSkills", skillsList);

        // Write equipped skills
        NbtList equippedList = new NbtList();
        for (String skillId : equippedSkills) {
            equippedList.add(skillId);
        }
        tag.put("EquippedSkills", equippedList);

        // Write cooldowns
        NbtCompound cooldownsTag = new NbtCompound();
        for (Map.Entry<String, Long> entry : skillCooldowns.entrySet()) {
            cooldownsTag.putLong(entry.getKey(), entry.getValue());
        }
        tag.put("Cooldowns", cooldownsTag);
    }
}
