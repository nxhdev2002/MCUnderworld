-- Additional elemental skills (30 new skills) - inspired by anime, fantasy, and xianxia
-- Fire Skills (Hỏa) - 5 skills
-- Ice/Thunder Skills (Thủy) - 5 skills
-- Lightning Skills (Lôi) - 5 skills
-- Earth Skills (Thổ) - 5 skills
-- Wind Skills (Phong) - 5 skills
-- Poison Skills (Độc) - 5 skills

INSERT INTO kiemhiep_skill_definitions (skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, evolution_level)
VALUES
  -- ============================================
  -- FIRE SKILLS (Hỏa) - based on炎, Dragon Ball, One Piece, etc.
  -- ============================================
  ('skill_inferno_fist', 'INFERNO_FIST', 'kiemhiep:skill_inferno_fist', 'Hỏa Hầu Kiếm', 40, 60, 6.0, false, true, 'DAMAGE_SINGLE', 15, true, false, 'Hỏa', NULL, 0),
  ('skill_solar_flare', 'SOLAR_FLARE', 'kiemhiep:skill_solar_flare', 'Thai DươngoSắc', 55, 80, 15.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Hỏa', NULL, 0),
  ('skill_dragon_breath', 'DRAGON_BREATH', 'kiemhiep:skill_dragon_breath', 'Long Hô Hỏa', 75, 90, 12.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Hỏa', NULL, 0),
  ('skill_blazing_sword', 'BLAZING_SWORD', 'kiemhiep:skill_blazing_sword', 'Hỏa Kiếm', 60, 70, 10.0, false, true, 'DAMAGE_SINGLE', 20, true, false, 'Hỏa', NULL, 0),
  ('skill_pyroclastic_flow', 'PYROCLASTIC_FLOW', 'kiemhiep:skill_pyroclastic_flow', 'Hỏa Sơn Lưu', 100, 150, 20.0, true, false, 'DAMAGE_AOE', 50, true, false, 'Hỏa', NULL, 0),

  -- ============================================
  -- ICE/THUNDER SKILLS (Thủy) -冰, Anastasia, Frozen, One Piece
  -- ============================================
  ('skill_glacier_spike', 'GLACIER_SPIKE', 'kiemhiep:skill_glacier_spike', 'Băng NONINFRINGEMENT', 35, 55, 8.0, false, false, 'DAMAGE_SINGLE', 18, true, false, 'Thủy', NULL, 0),
  ('skill_cryo_blast', 'CRYO_BLAST', 'kiemhiep:skill_cryo_blast', 'Băng Tan', 50, 75, 12.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Thủy', NULL, 0),
  ('skill_arctic_wind', 'ARCTIC_WIND', 'kiemhiep:skill_arctic_wind', 'Băng Phong', 45, 65, 14.0, true, false, 'DAMAGE_AOE', 20, true, false, 'Thủy', NULL, 0),
  ('skill_ice_prison', 'ICE_PRISON', 'kiemhiep:skill_ice_prison', 'Băng Lamb', 80, 120, 10.0, false, false, 'DAMAGE_SINGLE', 35, true, false, 'Thủy', NULL, 0),
  ('skill_thunder_fang', 'THUNDER_FANG', 'kiemhiep:skill_thunder_fang', 'Lôi Nha', 40, 60, 15.0, false, false, 'DAMAGE_SINGLE', 12, true, false, 'Thủy', NULL, 0),

  -- ============================================
  -- LIGHTNING SKILLS (Lôi) - based on Raikage, Flash, etc.
  -- ============================================
  ('skill_lightning_stab', 'LIGHTNING_STAB', 'kiemhiep:skill_lightning_stab', 'Lôi Chích', 30, 45, 8.0, false, true, 'DAMAGE_SINGLE', 8, true, false, 'Lôi', NULL, 0),
  ('skill_electro_wave', 'ELECTRO_WAVE', 'kiemhiep:skill_electro_wave', 'Điện L𩶨', 55, 80, 12.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Lôi', NULL, 0),
  ('skill_rAGING_thunder', 'RAGING_THUNDER', 'kiemhiep:skill_raging_thunder', 'Lôi Long', 75, 100, 16.0, true, false, 'DAMAGE_AOE', 35, true, false, 'Lôi', NULL, 0),
  ('skill_chain_thrust', 'CHAIN_THRUST', 'kiemhiep:skill_chain_thrust', 'Liên Hoàn Chích', 45, 70, 10.0, false, true, 'DAMAGE_SINGLE', 15, true, false, 'Lôi', NULL, 0),
  ('skill_vajra_lightning', 'VAJRA_LIGHTNING', 'kiemhiep:skill_vajra_lightning', 'Kim Cang Lôi', 90, 130, 18.0, true, false, 'DAMAGE_AOE', 40, true, false, 'Lôi', NULL, 0),

  -- ============================================
  -- EARTH SKILLS (Thổ) - based on Tsuchikage, Earth Release, etc.
  -- ============================================
  ('skill_stone_fist', 'STONE_FIST', 'kiemhiep:skill_stone_fist', 'ThạchUsageId', 25, 40, 6.0, false, true, 'DAMAGE_SINGLE', 12, true, false, 'Thổ', NULL, 0),
  ('skill_mud_wall', 'MUD_WALL', 'kiemhiep:skill_mud_wall', 'Đ stuck', 40, 60, 10.0, false, false, 'DAMAGE_SINGLE', 20, true, false, 'Thổ', NULL, 0),
  ('skill_seismic_pulse', 'SEISMIC_PULSE', 'kiemhiep:skill_seismic_pulse', 'Địa Sóng Sóng', 65, 90, 14.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Thổ', NULL, 0),
  ('skill_earth_golem', 'EARTH_GOLEM', 'kiemhiep:skill_earth_golem', 'Đất Đất Golem', 85, 120, 8.0, false, false, 'DAMAGE_SINGLE', 40, true, false, 'Thổ', NULL, 0),
  ('skill_quake_stomp', 'QUAKE_STOMP', 'kiemhiep:skill_quake_stomp', 'Địa Chấn Đạp', 55, 75, 12.0, true, false, 'DAMAGE_AOE', 20, true, false, 'Thổ', NULL, 0),

  -- ============================================
  -- WIND SKILLS (Phong) - based on Wind Release, Kaze no Hodokushi, etc.
  -- ============================================
  ('skill_gale_sword', 'GALE_SWORD', 'kiemhiep:skill_gale_sword', 'Phong Lôi Kiếm', 20, 35, 10.0, false, true, 'DAMAGE_SINGLE', 10, true, false, 'Phong', NULL, 0),
  ('skill_sand_storm', 'SAND_STORM', 'kiemhiep:skill_sand_storm', 'Sa Mạc Bão', 45, 70, 16.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Phong', NULL, 0),
  ('skill_vacuum_cut', 'VACUUM_CUT', 'kiemhiep:skill_vacuum_cut', 'Hư Không Cắt', 50, 65, 14.0, false, false, 'DAMAGE_SINGLE', 18, true, false, 'Phong', NULL, 0),
  ('skill_tornado_sweep', 'TORNADO_SWEEP', 'kiemhiep:skill_tornado_sweep', 'Long[pos', 70, 95, 18.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Phong', NULL, 0),
  ('skill_sonic_slicer', 'SONIC_SLICER', 'kiemhiep:skill_sonic_slicer', 'Siêu Âm Cắt', 60, 80, 12.0, false, true, 'DAMAGE_SINGLE', 15, true, false, 'Phong', NULL, 0),

  -- ============================================
  -- POISON SKILLS (Độc) - based on Poison Release,毒, etc.
  -- ============================================
  ('skill_black_spark', 'BLACK_SPARK', 'kiemhiep:skill_black_spark', 'Hắc Diệm', 25, 45, 8.0, false, false, 'DAMAGE_SINGLE', 12, true, false, 'Độc', NULL, 0),
  ('skill_acid_rain', 'ACID_RAIN', 'kiemhiep:skill_acid_rain', 'Mưa Axit', 55, 85, 16.0, true, false, 'DAMAGE_AOE', 30, true, false, 'Độc', NULL, 0),
  ('skill_miasma_blast', 'MIASMA_BLAST', 'kiemhiep:skill_miasma_blast', 'Hôi Tàn', 45, 65, 12.0, true, false, 'DAMAGE_AOE', 20, true, false, 'Độc', NULL, 0),
  ('skill_transformation', 'TRANSFORMATION', 'kiemhiep:skill_transformation', 'Biến Hóa', 80, 110, 10.0, false, false, 'DAMAGE_SINGLE', 35, true, false, 'Độc', NULL, 0),
  ('skill_cursed_gas', 'CURSED_GAS', 'kiemhiep:skill_cursed_gas', 'Khí Ars', 70, 90, 14.0, true, false, 'DAMAGE_AOE', 25, true, false, 'Độc', NULL, 0)

ON CONFLICT (skill_id) DO NOTHING;
