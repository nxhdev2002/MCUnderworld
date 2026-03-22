-- Sect tables: sect_members and sect_relations (V4)

-- sect_members table for player membership
CREATE TABLE IF NOT EXISTS kiemhiep_sect_members (
    id BIGSERIAL PRIMARY KEY,
    sect_id BIGINT NOT NULL REFERENCES kiemhiep_sects(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    rank VARCHAR(16) NOT NULL DEFAULT 'NOVICE',
    contribution INT NOT NULL DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sect_id, player_id)
);

-- sect_relations table for tông môn relationships
CREATE TABLE IF NOT EXISTS kiemhiep_sect_relations (
    id BIGSERIAL PRIMARY KEY,
    sect_id BIGINT NOT NULL REFERENCES kiemhiep_sects(id) ON DELETE CASCADE,
    related_sect_id BIGINT NOT NULL REFERENCES kiemhiep_sects(id) ON DELETE CASCADE,
    relation_type VARCHAR(16) NOT NULL DEFAULT 'NEUTRAL',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(sect_id, related_sect_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_kiemhiep_sect_members_sect ON kiemhiep_sect_members(sect_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_sect_members_player ON kiemhiep_sect_members(player_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_sect_relations_sect ON kiemhiep_sect_relations(sect_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_sect_relations_related ON kiemhiep_sect_relations(related_sect_id);
