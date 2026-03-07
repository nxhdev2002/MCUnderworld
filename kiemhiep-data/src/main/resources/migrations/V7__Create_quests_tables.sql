-- V7__Create_quests_tables.sql
-- Quest system tables - stores quests and player progress

CREATE TABLE IF NOT EXISTS quests (
    quest_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    type VARCHAR(32) NOT NULL,
    min_realm_level INT NOT NULL DEFAULT 0,
    reward_currency_type VARCHAR(32),
    reward_currency_amount INT,
    reward_exp INT,
    reward_items TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS quest_progress (
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    quest_id VARCHAR(64) NOT NULL REFERENCES quests(quest_id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL DEFAULT 'NOT_STARTED',
    progress INT NOT NULL DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (player_id, quest_id)
);

CREATE TABLE IF NOT EXISTS quest_objectives (
    quest_id VARCHAR(64) NOT NULL REFERENCES quests(quest_id) ON DELETE CASCADE,
    objective_index INT NOT NULL,
    type VARCHAR(32) NOT NULL,
    description TEXT,
    target_count INT NOT NULL,
    current_count INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (quest_id, objective_index)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_quests_type ON quests(type);
CREATE INDEX IF NOT EXISTS idx_quests_realm ON quests(min_realm_level);
CREATE INDEX IF NOT EXISTS idx_quest_progress_player ON quest_progress(player_id, status);
CREATE INDEX IF NOT EXISTS idx_quest_progress_completed ON quest_progress(player_id, completed_at DESC);

-- Comment
COMMENT ON TABLE quests IS 'Available quests in the game';
COMMENT ON TABLE quest_progress IS 'Player quest progress tracking';
COMMENT ON TABLE quest_objectives IS 'Quest objectives/tasks';
COMMENT ON COLUMN quests.type IS 'Type: MAIN_STORY, SIDE_STORY, DAILY, WEEKLY, EVENT, CHALLENGE';
COMMENT ON COLUMN quest_progress.status IS 'Status: NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED';
