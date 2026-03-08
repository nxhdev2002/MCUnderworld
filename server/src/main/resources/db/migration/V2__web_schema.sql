-- Web schema: views and server_metrics table

CREATE TABLE IF NOT EXISTS kiemhiep_server_metrics (
    id BIGSERIAL PRIMARY KEY,
    server_id VARCHAR(128) NOT NULL,
    ts TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tps DOUBLE PRECISION NOT NULL,
    tick_time_ms BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_kiemhiep_server_metrics_ts ON kiemhiep_server_metrics(ts);
CREATE INDEX IF NOT EXISTS idx_kiemhiep_server_metrics_server_id ON kiemhiep_server_metrics(server_id);

CREATE OR REPLACE VIEW v_player_overview AS
SELECT
    p.id,
    p.uuid,
    p.name,
    p.created_at AS player_created_at,
    c.level AS cultivation_level,
    c.exp AS cultivation_exp,
    COALESCE(w.balance, 0) AS wallet_balance
FROM kiemhiep_players p
LEFT JOIN kiemhiep_cultivation c ON c.player_id = p.id
LEFT JOIN (
    SELECT player_id, SUM(balance) AS balance
    FROM kiemhiep_wallets
    GROUP BY player_id
) w ON w.player_id = p.id;

CREATE OR REPLACE VIEW v_leaderboard_cultivation AS
SELECT
    p.id AS player_id,
    p.uuid,
    p.name,
    c.level,
    c.exp,
    ROW_NUMBER() OVER (ORDER BY c.level DESC, c.exp DESC) AS rank
FROM kiemhiep_players p
JOIN kiemhiep_cultivation c ON c.player_id = p.id
ORDER BY rank;
