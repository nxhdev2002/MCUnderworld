-- V1__Create_players_table.sql
-- Core players table - stores basic player information

CREATE TABLE IF NOT EXISTS players (
    player_id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    first_join TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_seen TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for name lookups
CREATE INDEX IF NOT EXISTS idx_players_name ON players(name);

-- Index for last_seen (for cleanup queries)
CREATE INDEX IF NOT EXISTS idx_players_last_seen ON players(last_seen);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to auto-update updated_at
DROP TRIGGER IF EXISTS update_players_updated_at ON players;
CREATE TRIGGER update_players_updated_at
    BEFORE UPDATE ON players
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
