-- V3__Create_wallets_tables.sql
-- Economy system tables - stores player currency balances

CREATE TABLE IF NOT EXISTS wallets (
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    currency_type VARCHAR(32) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id, currency_type)
);

-- Index for balance queries
CREATE INDEX IF NOT EXISTS idx_wallets_balance ON wallets(currency_type, balance DESC);

-- Trigger to auto-update updated_at
DROP TRIGGER IF EXISTS update_wallets_updated_at ON wallets;
CREATE TRIGGER update_wallets_updated_at
    BEFORE UPDATE ON wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comment
COMMENT ON TABLE wallets IS 'Player wallet balances - multi-currency support';
COMMENT ON COLUMN wallets.currency_type IS 'Currency type: GOLD, SILVER, SPIRIT_STONE';
COMMENT ON COLUMN wallets.balance IS 'Current balance in smallest unit (e.g., 10000 gold = 10000)';

-- Insert default currency types for all existing players
-- SPIRIT_STONE (base currency)
INSERT INTO wallets (player_id, currency_type, balance)
SELECT player_id, 'SPIRIT_STONE', 0
FROM players
ON CONFLICT (player_id, currency_type) DO NOTHING;

-- SILVER
INSERT INTO wallets (player_id, currency_type, balance)
SELECT player_id, 'SILVER', 0
FROM players
ON CONFLICT (player_id, currency_type) DO NOTHING;

-- GOLD
INSERT INTO wallets (player_id, currency_type, balance)
SELECT player_id, 'GOLD', 0
FROM players
ON CONFLICT (player_id, currency_type) DO NOTHING;
