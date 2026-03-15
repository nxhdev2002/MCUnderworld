-- Economy tables: transactions (wallets table exists in V1)

CREATE TABLE IF NOT EXISTS kiemhiep_transactions (
    id BIGSERIAL PRIMARY KEY,
    from_player_id BIGINT REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    to_player_id BIGINT REFERENCES kiemhiep_players(id) ON DELETE CASCADE,
    amount BIGINT NOT NULL,
    currency_type VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kiemhiep_transactions_from ON kiemhiep_transactions(from_player_id);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_transactions_to ON kiemhiep_transactions(to_player_id);
