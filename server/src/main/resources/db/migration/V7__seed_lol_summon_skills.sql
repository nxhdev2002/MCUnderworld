-- Seed League of Legends Summon skill definitions
-- 20 new skills inspired by LoL Champions: Zilean, Vikor, Ahri, Soraka, Tryndamere, Ekko, Jinx, Lux, Miss Fortune

INSERT INTO kiemhiep_skill_definitions (skill_id, behavior_id, item_id, name, mana_cost, cooldown_ticks, max_radius, is_aoe, is_melee, skill_type, cast_time_ticks, cast_cancellable, consumable, elemental_type, parent_skill_id, evolution_level)
VALUES
  -- ============================================
  -- FX SUMMON (5 skills) - Citadel of FX
  -- ============================================
  -- Zilean: Time Bombs - Bom Thời Gian
  ('skill_zilean_bomb', 'TIME_BOMB', 'kiemhiep:skill_zilean_bomb', 'Bom Thời Gian', 40, 60, 8.0, false, false, 'SUMMON', 20, true, false, 'Lôi', NULL, 0),
  -- Vikor: Light Sentry - Gác Đèn Cơ Khí
  ('skill_viktor_sentry', 'SENTRY_LIGHT', 'kiemhiep:skill_viktor_sentry', 'Gác Đèn Cơ Khí', 35, 50, 6.0, false, false, 'SUMMON', 15, true, false, 'Thổ', NULL, 0),
  -- Soraka: Starlight Heal - Hồi Máu Ánh Sao
  ('skill_soraka_star', 'HEAL_STAR', 'kiemhiep:skill_soraka_star', 'Hồi Máu Ánh Sao', 50, 90, 10.0, true, false, 'HEAL', 30, true, false, 'Lôi', NULL, 0),
  -- Blue Ward - Linh Gác Xanh
  ('skill_ward_blue', 'WARD_BLUE', 'kiemhiep:skill_ward_blue', 'Linh Gác Xanh', 25, 40, 5.0, false, false, 'SUMMON', 10, true, false, 'Thổ', NULL, 0),
  -- Red Ward - Linh Gác Đỏ
  ('skill_ward_red', 'WARD_RED', 'kiemhiep:skill_ward_red', 'Linh Gác Đỏ', 25, 40, 5.0, false, false, 'SUMMON', 10, true, false, 'Hỏa', NULL, 0),

  -- ============================================
  -- SUMMON LINH VẬT (5 skills) - Spirit/Beast Summon
  -- ============================================
  -- Ahri: Fox Spirit - Linh Cú
  ('skill_owl_spirit', 'SPIRIT_OWL', 'kiemhiep:skill_owl_spirit', 'Linh Cú', 55, 80, 8.0, false, false, 'SUMMON', 25, true, false, 'Phong', NULL, 0),
  -- Brand: Wolf Beast - Khuyển Io
  ('skill_wolf_beast', 'BEAST_WOLF', 'kiemhiep:skill_wolf_beast', 'Khuyển Io', 60, 90, 10.0, true, false, 'SUMMON', 30, true, false, 'Hỏa', NULL, 0),
  -- Phoenix - Phượng Hoàng Lửa
  ('skill_phoenix_flame', 'PHOENIX_FLAME', 'kiemhiep:skill_phoenix_flame', 'Phượng Hoàng Lửa', 80, 140, 12.0, true, false, 'SUMMON', 40, true, false, 'Hỏa', NULL, 0),
  -- Crab Summon - Bạo Xa
  ('skill_crab_summon', 'SUMMON_CRAB', 'kiemhiep:skill_crab_summon', 'Bạo Xa', 45, 70, 6.0, false, false, 'SUMMON', 20, true, false, 'Thổ', NULL, 0),
  -- Bear Summon - Hổ Hùng
  ('skill_bear_summon', 'SUMMON_BEAR', 'kiemhiep:skill_bear_summon', 'Hổ Hùng', 70, 120, 10.0, true, false, 'SUMMON', 35, true, false, 'Thổ', NULL, 0),

  -- ============================================
  -- QUANTUM/VOID SUMMON (5 skills)
  -- ============================================
  -- Tryndamere: Void Spawn - Hư Không Sining
  ('skill_void_spawn', 'VOID_SPAWN', 'kiemhiep:skill_void_spawn', 'Hư Không Sining', 65, 100, 8.0, true, false, 'SUMMON', 30, true, false, 'Độc', NULL, 0),
  -- Ekko: Z-Drive - Z-Drive Resonance
  ('skill_ekko_zdrive', 'TIME_BREAKER', 'kiemhiep:skill_ekko_zdrive', 'Z-Drive Resonance', 75, 130, 10.0, true, false, 'SUMMON', 35, true, false, 'Lôi', NULL, 0),
  -- Dark Rift - Ác Huyệt
  ('skill_dark_rift', 'DARK_RIFT', 'kiemhiep:skill_dark_rift', 'Ác Huyệt', 55, 85, 8.0, true, false, 'SUMMON', 25, true, false, 'Độc', NULL, 0),
  -- Quantum Ray - Lượng Tử Tia
  ('skill_quantum_ray', 'QUANTUM_RAY', 'kiemhiep:skill_quantum_ray', 'Lượng Tử Tia', 60, 95, 12.0, true, false, 'SUMMON', 28, true, false, 'Lôi', NULL, 0),
  -- Void Shield - Void Hộ Vệ
  ('skill_shield_summon', 'SUMMON_SHIELD', 'kiemhiep:skill_shield_summon', 'Void Hộ Vệ', 40, 65, 6.0, false, false, 'SHIELD', 15, true, false, 'Hỏa', NULL, 0),

  -- ============================================
  -- ELEMENTAL ECHO (5 skills)
  -- ============================================
  -- Jinx: Flame Chompers - Hỏa Khảm
  ('skill_jinx_chompers', 'FLAME_CHOMP', 'kiemhiep:skill_jinx_chompers', 'Hỏa Khảm', 50, 80, 8.0, true, false, 'SUMMON', 20, true, false, 'Hỏa', NULL, 0),
  -- Miss Fortune: Make It Rain -KNOWN风暴
  ('skill_mf_rain', 'RAIN_ARROWS', 'kiemhiep:skill_mf_rain', 'Vũ Thượng storm', 65, 110, 10.0, true, false, 'SUMMON', 35, true, false, 'Thủy', NULL, 0),
  -- Lux: Final Spark Spike --China Spark Spike
  ('skill_lux_spike', 'LIGHT_SPIKE', 'kiemhiep:skill_lux_spike', 'Cuối Cùng Spark Spike', 70, 120, 12.0, true, false, 'SUMMON', 38, true, false, 'Lôi', NULL, 0),
  -- Frozen Cage - Băng tracing
  ('skill_frozen_cage', 'FROZEN_CAGE', 'kiemhiep:skill_frozen_cage', 'Băng tracing', 55, 90, 8.0, true, false, 'SUMMON', 28, true, false, 'Thủy', NULL, 0),
  -- Electric Snake - Điện Xà
  ('skill_electric_snake', 'ELECTRIC_SNAKE', 'kiemhiep:skill_electric_snake', 'Điện Xà', 60, 100, 10.0, true, false, 'SUMMON', 30, true, false, 'Lôi', NULL, 0)

ON CONFLICT (skill_id) DO NOTHING;
