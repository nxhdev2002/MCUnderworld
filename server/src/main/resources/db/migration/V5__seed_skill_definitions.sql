-- Seed skill definitions. behavior_id must match SkillRegistry (e.g. FIREBALL -> FireballSkill).

INSERT INTO kiemhiep_skill_definitions (skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, parent_skill_id, evolution_level)
VALUES
  ('skill_fireball', 'FIREBALL', 'kiemhiep:skill_fireball', 'Fireball', 20, 40, 8.0, true, false, 'DAMAGE_AOE', 0, true, false, NULL, 0)
ON CONFLICT (skill_id) DO NOTHING;
