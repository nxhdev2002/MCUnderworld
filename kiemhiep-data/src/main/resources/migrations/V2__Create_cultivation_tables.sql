-- V2__Create_cultivation_tables.sql
-- Cultivation system tables - stores player cultivation progress

CREATE TABLE IF NOT EXISTS cultivation (
    player_id UUID PRIMARY KEY REFERENCES players(player_id) ON DELETE CASCADE,
    realm_level INT NOT NULL DEFAULT 0,
    sub_level INT NOT NULL DEFAULT 0,
    exp INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for realm level queries (for leaderboards)
CREATE INDEX IF NOT EXISTS idx_cultivation_realm_level ON cultivation(realm_level DESC);

-- Index for exp queries
CREATE INDEX IF NOT EXISTS idx_cultivation_exp ON cultivation(exp DESC);

-- Trigger to auto-update updated_at
DROP TRIGGER IF EXISTS update_cultivation_updated_at ON cultivation;
CREATE TRIGGER update_cultivation_updated_at
    BEFORE UPDATE ON cultivation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment
COMMENT ON TABLE cultivation IS 'Player cultivation progress - realm, sub-level, and experience';
COMMENT ON COLUMN cultivation.realm_level IS 'Cultivation realm level (0-9): 0=Mortal, 1=Luyen Khi, 2=Truc Co, 3=Kim Dan, 4=Nguyen Anh, 5=Hoa Than, 6=Luyen Hu, 7=Hop The, 8=Dai Thua, 9=Do Kiep';
COMMENT ON COLUMN cultivation.sub_level IS 'Sub-level within realm (0-8)';
COMMENT ON COLUMN cultivation.exp IS 'Current experience points';
