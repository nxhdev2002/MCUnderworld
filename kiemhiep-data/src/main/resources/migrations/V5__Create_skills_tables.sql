-- V5__Create_skills_tables.sql
-- Skills system tables - stores skills and player skill progress

CREATE TABLE IF NOT EXISTS skills (
    skill_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL,
    description TEXT,
    mana_cost INT NOT NULL DEFAULT 0,
    cooldown_ticks INT NOT NULL DEFAULT 0,
    max_level INT NOT NULL DEFAULT 10
);

CREATE TABLE IF NOT EXISTS player_skills (
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    skill_id VARCHAR(64) NOT NULL REFERENCES skills(skill_id) ON DELETE CASCADE,
    level INT NOT NULL DEFAULT 1,
    exp INT NOT NULL DEFAULT 0,
    learned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id, skill_id)
);

CREATE TABLE IF NOT EXISTS player_skill_slots (
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    slot_index INT NOT NULL,
    skill_id VARCHAR(64) REFERENCES skills(skill_id),
    PRIMARY KEY (player_id, slot_index),
    CONSTRAINT valid_slot CHECK (slot_index >= 0 AND slot_index < 9)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_skills_type ON skills(type);
CREATE INDEX IF NOT EXISTS idx_player_skills_level ON player_skills(player_id, level DESC);

-- Comment
COMMENT ON TABLE skills IS 'Available skills in the game';
COMMENT ON TABLE player_skills IS 'Skills learned by players';
COMMENT ON TABLE player_skill_slots IS 'Equipped skill slots for quick access';

-- Seed default skills
INSERT INTO skills (skill_id, name, type, description, mana_cost, cooldown_ticks, max_level) VALUES
    -- Sword skills
    ('sword_basic', 'Kiếm Pháp Cơ Bản', 'SWORD', 'Kỹ năng kiếm cơ bản', 10, 20, 10),
    ('sword_slash', 'Kiếm Vũ', 'SWORD', 'Một nhát chém mạnh mẽ', 20, 40, 10),
    ('sword_thrust', 'Kiếm Châm', 'SWORD', 'Đâm xuyên thấu đối phương', 25, 50, 10),
    ('sword_whirlwind', 'Kiếm Vũ Bão', 'SWORD', 'Tạo ra cơn bão kiếm xung quanh', 40, 100, 10),
    ('sword_final_blow', 'Kiếm Quyết Kill', 'SWORD', 'Đòn kết liễu tối thượng', 60, 150, 10),
    -- Qi skills
    ('qi_gather', 'Tụ Khí', 'QI', 'Thu thập khí trời', 15, 30, 10),
    ('qi_shield', 'Khí Hộ Thể', 'QI', 'Tạo lá chắn bảo vệ', 30, 60, 10),
    ('qi_blast', 'Khí Bùng Nổ', 'QI', 'Giải phóng năng lượng khí', 35, 70, 10),
    ('qi_heal', 'Khí Trị Liệu', 'QI', 'Dùng khí để chữa lành', 40, 90, 10),
    ('qi_burst', 'Khí Bùng Cháy', 'QI', 'Tăng cường sức mạnh tạm thời', 50, 120, 10),
    -- Body skills
    ('body_dash', 'Thân Pháp Vụt', 'BODY', 'Di chuyển nhanh về phía trước', 20, 40, 10),
    ('body_jump', 'Nhảy Cao', 'BODY', 'Tăng khả năng nhảy', 15, 30, 10),
    ('body_dodge', 'Né Tránh', 'BODY', 'Né đòn tấn công', 25, 50, 10),
    ('body_rage', 'Cuồng Nộ', 'BODY', 'Tăng sức mạnh chiến đấu', 40, 100, 10),
    ('body_meditation', 'Tịnh Tâm', 'BODY', 'Hồi phục nhanh', 30, 80, 10),
    -- Poison skills
    ('poison_fang', 'Nọc Độc', 'POISON', 'Tấn công bằng nọc độc', 25, 50, 10),
    ('poison_cloud', 'Mây Độc', 'POISON', 'Tạo đám mây độc', 35, 70, 10),
    ('poison_cure', 'Giải Độc', 'POISON', 'Giải độc cho bản thân', 30, 60, 10),
    -- Wind skills
    ('wind_blade', 'Phong Nhận', 'WIND', 'Tạo lưỡi dao gió', 35, 70, 10),
    ('storm_call', 'Triệu Hồi Bão', 'WIND', 'Gọi ra cơn bão', 50, 120, 10)
ON CONFLICT (skill_id) DO NOTHING;
