-- V4__Create_transactions_table.sql
-- Transaction history table - stores all currency transactions

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    player_id UUID NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    currency_type VARCHAR(32) NOT NULL,
    amount BIGINT NOT NULL,
    transaction_type VARCHAR(32) NOT NULL,
    description TEXT,
    related_player_id UUID REFERENCES players(player_id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for player transaction lookups
CREATE INDEX IF NOT EXISTS idx_transactions_player_id ON transactions(player_id, created_at DESC);

-- Index for transaction type queries
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(transaction_type);

-- Index for date range queries
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at DESC);

-- Comment
COMMENT ON TABLE transactions IS 'Transaction history for all currency movements';
COMMENT ON COLUMN transactions.amount IS 'Positive for income, negative for expense';
COMMENT ON COLUMN transactions.transaction_type IS 'Type: EARN, SPEND, TRANSFER_IN, TRANSFER_OUT, SYSTEM, QUEST_REWARD, SHOP_PURCHASE';
COMMENT ON COLUMN transactions.description IS 'Optional description of the transaction';
COMMENT ON COLUMN transactions.related_player_id IS 'For transfers, the other party involved';
