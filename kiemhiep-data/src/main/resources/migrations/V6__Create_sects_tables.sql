-- V6__Create_sects_tables.sql
-- Sect system tables - stores sects and member relationships

CREATE TABLE IF NOT EXISTS sects (
    sect_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(128) NOT NULL UNIQUE,
    leader_uuid UUID NOT NULL REFERENCES players(player_id),
    level INT NOT NULL DEFAULT 1,
    exp INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sect_members (
    sect_id UUID NOT NULL REFERENCES sects(sect_id) ON DELETE CASCADE,
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    rank VARCHAR(32) NOT NULL DEFAULT 'MEMBER',
    contribution INT NOT NULL DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sect_id, player_id)
);

CREATE TABLE IF NOT EXISTS sect_relations (
    sect_id UUID NOT NULL REFERENCES sects(sect_id) ON DELETE CASCADE,
    related_sect_id UUID NOT NULL REFERENCES sects(sect_id) ON DELETE CASCADE,
    relation_type VARCHAR(32) NOT NULL DEFAULT 'NEUTRAL',
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sect_id, related_sect_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_sects_leader ON sects(leader_uuid);
CREATE INDEX IF NOT EXISTS idx_sect_members_player ON sect_members(player_id);
CREATE INDEX IF NOT EXISTS idx_sect_relations ON sect_relations(sect_id);

-- Trigger
DROP TRIGGER IF EXISTS update_sects_updated_at ON sects;
CREATE TRIGGER update_sects_updated_at
    BEFORE UPDATE ON sects
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment
COMMENT ON TABLE sects IS 'Player sects/guilds';
COMMENT ON TABLE sect_members IS 'Sect membership information';
COMMENT ON TABLE sect_relations IS 'Relations between sects (ALLIED, HOSTILE, NEUTRAL)';
COMMENT ON COLUMN sect_members.rank IS 'Rank: LEADER, ELDER, MEMBER, NOVICE';
COMMENT ON COLUMN sect_relations.relation_type IS 'Relation: ALLIED, HOSTILE, NEUTRAL, WAR';
