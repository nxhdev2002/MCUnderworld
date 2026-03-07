-- V8__Create_npcs_tables.sql
-- NPC system tables - stores NPCs and dialogues

CREATE TABLE IF NOT EXISTS npcs (
    npc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL,
    skin_type VARCHAR(64),
    world_key VARCHAR(128) NOT NULL,
    pos_x DOUBLE PRECISION NOT NULL,
    pos_y DOUBLE PRECISION NOT NULL,
    pos_z DOUBLE PRECISION NOT NULL,
    yaw FLOAT,
    pitch FLOAT,
    data TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS npc_dialogues (
    dialogue_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    npc_id UUID NOT NULL REFERENCES npcs(npc_id) ON DELETE CASCADE,
    dialogue_text TEXT NOT NULL,
    parent_dialogue_id UUID REFERENCES npc_dialogues(dialogue_id),
    condition_type VARCHAR(32),
    condition_value TEXT,
    action_type VARCHAR(32),
    action_value TEXT,
    display_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS npc_dialogue_options (
    dialogue_id UUID NOT NULL REFERENCES npc_dialogues(dialogue_id) ON DELETE CASCADE,
    option_index INT NOT NULL,
    option_text VARCHAR(512) NOT NULL,
    next_dialogue_id UUID REFERENCES npc_dialogues(dialogue_id),
    PRIMARY KEY (dialogue_id, option_index)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_npcs_type ON npcs(type);
CREATE INDEX IF NOT EXISTS idx_npcs_location ON npcs(world_key, pos_x, pos_y, pos_z);
CREATE INDEX IF NOT EXISTS npc_dialogues_npc ON npc_dialogues(npc_id);

-- Comment
COMMENT ON TABLE npcs IS 'Non-player characters';
COMMENT ON TABLE npc_dialogues IS 'NPC dialogue trees';
COMMENT ON TABLE npc_dialogue_options IS 'Dialogue options for player choices';
COMMENT ON COLUMN npcs.type IS 'Type: QUEST_GIVER, SKILL_TRAINER, SHOPKEEPER, GUIDE, MERCHANT, TELEPORTER, SECT_MANAGER, CUSTOM';
COMMENT ON COLUMN npc_dialogues.condition_type IS 'Condition: QUEST_COMPLETED, HAS_ITEM, MIN_LEVEL, etc.';
COMMENT ON COLUMN npc_dialogues.action_type IS 'Action: GIVE_QUEST, OPEN_SHOP, TELEPORT, etc.';
