-- V9__Create_dungeons_boss_tables.sql
-- Dungeon and Boss system tables

CREATE TABLE IF NOT EXISTS dungeon_portals (
    portal_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    world_key VARCHAR(128) NOT NULL,
    block_x INT NOT NULL,
    block_y INT NOT NULL,
    block_z INT NOT NULL,
    difficulty VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
    five_elements VARCHAR(32) NOT NULL DEFAULT 'NONE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dungeon_instances (
    instance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    portal_id UUID REFERENCES dungeon_portals(portal_id),
    owner_uuid UUID NOT NULL REFERENCES players(player_id),
    world_key VARCHAR(128) NOT NULL,
    difficulty VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS boss_templates (
    boss_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    health DOUBLE PRECISION NOT NULL,
    damage DOUBLE PRECISION NOT NULL,
    armor DOUBLE PRECISION NOT NULL,
    exp_reward INT NOT NULL DEFAULT 0,
    currency_reward_min INT NOT NULL DEFAULT 0,
    currency_reward_max INT NOT NULL DEFAULT 0,
    loot_table VARCHAR(128),
    mythic_mob_type VARCHAR(128),
    abilities TEXT
);

CREATE TABLE IF NOT EXISTS boss_instances (
    instance_id UUID NOT NULL REFERENCES dungeon_instances(instance_id) ON DELETE CASCADE,
    boss_id VARCHAR(64) NOT NULL REFERENCES boss_templates(boss_id),
    entity_uuid UUID,
    health DOUBLE PRECISION NOT NULL,
    max_health DOUBLE PRECISION NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    spawned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    defeated_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (instance_id, boss_id)
);

CREATE TABLE IF NOT EXISTS mob_templates (
    mob_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    health DOUBLE PRECISION NOT NULL,
    damage DOUBLE PRECISION NOT NULL,
    exp_reward INT NOT NULL DEFAULT 0,
    loot_table VARCHAR(128),
    mythic_mob_type VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS spawn_zones (
    zone_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    world_key VARCHAR(128) NOT NULL,
    center_x INT NOT NULL,
    center_y INT NOT NULL,
    center_z INT NOT NULL,
    radius INT NOT NULL DEFAULT 50,
    mob_type VARCHAR(64) NOT NULL,
    max_mobs INT NOT NULL DEFAULT 10,
    respawn_ticks INT NOT NULL DEFAULT 600,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_dungeon_portals_location ON dungeon_portals(world_key, block_x, block_y, block_z);
CREATE INDEX IF NOT EXISTS idx_dungeon_instances_owner ON dungeon_instances(owner_uuid, status);
CREATE INDEX IF NOT EXISTS idx_spawn_zones_world ON spawn_zones(world_key, active);

-- Comment
COMMENT ON TABLE dungeon_portals 'Portal entries to dungeons';
COMMENT ON TABLE dungeon_instances 'Active dungeon instances';
COMMENT ON TABLE boss_templates 'Boss template definitions';
COMMENT ON TABLE boss_instances 'Active boss fights';
COMMENT ON TABLE mob_templates 'Mob template definitions';
COMMENT ON TABLE spawn_zones 'Mob spawning zones';
COMMENT ON COLUMN dungeon_portals.difficulty IS 'Difficulty: EASY, NORMAL, HARD, EXTREME';
COMMENT ON COLUMN dungeon_portals.five_elements IS 'Element: KIM, MOC, THUY, HOA, THO, NONE';
COMMENT ON COLUMN dungeon_instances.status IS 'Status: ACTIVE, COMPLETED, FAILED, EXPIRED';
COMMENT ON COLUMN boss_instances.status IS 'Status: ACTIVE, DEAD';
