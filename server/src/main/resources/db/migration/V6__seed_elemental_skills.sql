-- Seed elemental skill definitions for the 6 elemental types:
-- Fire (Hỏa), Ice/Thunder (Thủy), Lightning (Lôi), Earth (Thổ), Wind (Phong), Poison (Độc)

INSERT INTO kiemhiep_skill_definitions (skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, evolution_level)
VALUES
  -- Fire Skills (Hỏa)
  ('skill_fire_blast', 'FIRE_BLAST', 'kiemhiep:skill_fire_blast', 'Hỏa Luân', 30, 60, 8.0, true, false, 'DAMAGE_AOE', 20, true, false, 'Hỏa', NULL, 0),
  ('skill_fire_storm', 'FIRE_STORM', 'kiemhiep:skill_fire_storm', 'Phong Lửa Cyclone', 80, 120, 12.0, true, false, 'DAMAGE_AOE', 40, true, false, 'Hỏa', NULL, 0),
  ('skill_fire_wall', 'FIRE_WALL', 'kiemhiep:skill_fire_wall', 'Hỏa Tường', 50, 90, 10.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Hỏa', NULL, 0),

  -- Ice/Thunder Skills (Thủy - Băng giá)
  ('skill_ice_shard', 'ICE_SHARD', 'kiemhiep:skill_ice_shard', 'Băng Tiễn', 25, 50, 10.0, false, false, 'DAMAGE_SINGLE', 15, true, false, 'Thủy', NULL, 0),
  ('skill_frost_wave', 'FROST_WAVE', 'kiemhiep:skill_frost_wave', 'Băng Lăng Sóng', 60, 90, 15.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Thủy', NULL, 0),
  ('skill_ice_crystal', 'ICE_CRYSTAL', 'kiemhiep:skill_ice_crystal', 'Băng Tinh Thạch', 70, 100, 8.0, true, false, 'DAMAGE_AOE', 35, true, false, 'Thủy', NULL, 0),

  -- Lightning Skills (Lôi)
  ('skill_lightning_bolt', 'LIGHTNING_BOLT', 'kiemhiep:skill_lightning_bolt', 'Lôi Chých', 35, 70, 20.0, false, false, 'DAMAGE_SINGLE', 10, true, false, 'Lôi', NULL, 0),
  ('skill_chain_lightning', 'CHAIN_LIGHTNING', 'kiemhiep:skill_chain_lightning', 'Liên Hoàn Lôi', 90, 140, 25.0, true, false, 'DAMAGE_AOE', 50, true, false, 'Lôi', NULL, 0),
  ('skill_thunder_storm', 'THUNDER_STORM', 'kiemhiep:skill_thunder_storm', 'Bão Sét', 120, 180, 18.0, true, false, 'DAMAGE_AOE', 60, true, false, 'Lôi', NULL, 0),

  -- Earth Skills (Thổ)
  ('skill_earth_spike', 'EARTH_SPIKE', 'kiemhiep:skill_earth_spike', 'Địa Ám Chân', 20, 45, 6.0, false, false, 'DAMAGE_SINGLE', 15, true, false, 'Thổ', NULL, 0),
  ('skill_tremor', 'TREMOR', 'kiemhiep:skill_tremor', 'Địa Chấn', 70, 110, 10.0, true, false, 'DAMAGE_AOE', 40, true, false, 'Thổ', NULL, 0),
  ('skill_earth_barrier', 'EARTH_BARRIER', 'kiemhiep:skill_earth_barrier', 'Địa Giới Hào', 55, 80, 8.0, false, false, 'DAMAGE_SINGLE', 30, true, false, 'Thổ', NULL, 0),

  -- Wind Skills (Phong)
  ('skill_wind_cut', 'WIND_CUT', 'kiemhiep:skill_wind_cut', 'Phong Lữ Kiếm', 15, 35, 12.0, false, false, 'DAMAGE_SINGLE', 10, true, false, 'Phong', NULL, 0),
  ('skill_cyclone', 'CYCLONE', 'kiemhiep:skill_cyclone', 'Long Xuyên Phong', 85, 130, 14.0, true, false, 'DAMAGE_AOE', 35, true, false, 'Phong', NULL, 0),
  ('skill_sonic_boom', 'SONIC_BOOM', 'kiemhiep:skill_sonic_boom', 'Sonic Song', 65, 100, 16.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Phong', NULL, 0),

  -- Poison Skills (Độc)
  ('skill_poison_dart', 'POISON_DART', 'kiemhiep:skill_poison_dart', 'Độc Tiễn', 18, 40, 8.0, false, false, 'DAMAGE_SINGLE', 12, true, false, 'Độc', NULL, 0),
  ('skill_poison_cloud', 'POISON_CLOUD', 'kiemhiep:skill_poison_cloud', 'Độc Khí vây quanh', 65, 100, 10.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Độc', NULL, 0),
  ('skill_venom_web', 'VENOM_WEB', 'kiemhiep:skill_venom_web', 'Hitim Độc Mạng', 55, 85, 12.0, true, false, 'DAMAGE_AOE', 20, true, false, 'Độc', NULL, 0)
ON CONFLICT (skill_id) DO NOTHING;
