-- Core tables (prefix kiemhiep_)

CREATE TABLE IF NOT EXISTS kiemhiep_players (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kiemhiep_cultivation (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    level INT NOT NULL DEFAULT 1,
    exp BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(player_id)
);

CREATE TABLE IF NOT EXISTS kiemhiep_wallets (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    balance BIGINT NOT NULL DEFAULT 0,
    currency VARCHAR(32) NOT NULL DEFAULT 'default',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(player_id, currency)
);

CREATE TABLE IF NOT EXISTS kiemhiep_sects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kiemhiep_quests (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    quest_id VARCHAR(64) NOT NULL,
    progress INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(player_id, quest_id)
);

CREATE TABLE IF NOT EXISTS kiemhiep_skills (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    skill_id VARCHAR(64) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(player_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_kiemhiep_players_uuid ON kiemhiep_players(uuid);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_cultivation_player_id ON kiemhiep_cultivation(player_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_wallets_player_id ON kiemhiep_wallets(player_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_quests_player_id ON kiemhiep_quests(player_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_skills_player_id ON kiemhiep_skills(player_id);
