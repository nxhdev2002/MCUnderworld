-- Skill definitions (catalog). behavior_id maps to SkillRegistry.

CREATE TABLE IF NOT EXISTS kiemhiep_skill_definitions (
    id BIGSERIAL PRIMARY KEY,
    skill_id VARCHAR(64) NOT NULL UNIQUE,
    behavior_id VARCHAR(64) NOT NULL,
    item_id VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    mana_cost INT NOT NULL DEFAULT 0,
    cooldown_ticks INT NOT NULL DEFAULT 40,
    max_radius DOUBLE PRECISION NOT NULL DEFAULT 8.0,
    is_aoe BOOLEAN NOT NULL DEFAULT FALSE,
    is_melee BOOLEAN NOT NULL DEFAULT FALSE,
    skill_type VARCHAR(32) NOT NULL DEFAULT 'DAMAGE_SINGLE',
    cast_time_ticks INT NOT NULL DEFAULT 0,
    cast_cancellable BOOLEAN NOT NULL DEFAULT TRUE,
    consumable BOOLEAN NOT NULL DEFAULT FALSE,
    elemental_type VARCHAR(16) NOT NULL DEFAULT 'NONE',
    parent_skill_id VARCHAR(64),
    evolution_level INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kiemhiep_skill_definitions_behavior_id ON kiemhiep_skill_definitions(behavior_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_skill_definitions_item_id ON kiemhiep_skill_definitions(item_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_skill_definitions_parent ON kiemhiep_skill_definitions(parent_skill_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_skill_definitions_elemental_type ON kiemhiep_skill_definitions(elemental_type);
