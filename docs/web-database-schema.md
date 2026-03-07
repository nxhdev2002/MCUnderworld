# Web database schema

Tất cả bảng và view dùng prefix `kiemhiep_`. Schema dùng cho web dashboard và leaderboard.

## Bảng (tables)

### kiemhiep_players

Lưu thông tin người chơi.

| Cột         | Kiểu                     | Mô tả                |
|------------|---------------------------|----------------------|
| id         | BIGSERIAL PRIMARY KEY     | PK                   |
| uuid       | VARCHAR(36) NOT NULL      | UUID người chơi      |
| name       | VARCHAR(64) NOT NULL      | Tên hiển thị         |
| created_at | TIMESTAMP WITH TIME ZONE  | Lúc tạo              |
| updated_at | TIMESTAMP WITH TIME ZONE  | Lúc cập nhật         |

### kiemhiep_cultivation

Tu vi / cultivation theo player.

| Cột         | Kiểu     | Mô tả           |
|------------|----------|-----------------|
| id         | BIGSERIAL | PK              |
| player_id  | BIGINT   | FK → kiemhiep_players |
| level      | INT      | Cấp tu vi       |
| exp        | BIGINT   | Kinh nghiệm     |
| created_at | TIMESTAMP WITH TIME ZONE | |
| updated_at | TIMESTAMP WITH TIME ZONE | |

### kiemhiep_wallets

Ví tiền theo player và currency.

| Cột         | Kiểu     | Mô tả           |
|------------|----------|-----------------|
| id         | BIGSERIAL | PK              |
| player_id  | BIGINT   | FK → kiemhiep_players |
| balance    | BIGINT   | Số dư           |
| currency   | VARCHAR(32) | Loại tiền tệ  |
| created_at | TIMESTAMP WITH TIME ZONE | |
| updated_at | TIMESTAMP WITH TIME ZONE | |

UNIQUE(player_id, currency).

### kiemhiep_sects

Môn phái.

| Cột         | Kiểu     | Mô tả     |
|------------|----------|-----------|
| id         | BIGSERIAL | PK        |
| name       | VARCHAR(64) | Tên môn phái |
| created_at | TIMESTAMP WITH TIME ZONE | |
| updated_at | TIMESTAMP WITH TIME ZONE | |

### kiemhiep_quests

Tiến độ nhiệm vụ theo player.

| Cột         | Kiểu     | Mô tả        |
|------------|----------|--------------|
| id         | BIGSERIAL | PK           |
| player_id  | BIGINT   | FK → kiemhiep_players |
| quest_id   | VARCHAR(64) | ID nhiệm vụ |
| progress   | INT      | Tiến độ      |
| created_at | TIMESTAMP WITH TIME ZONE | |
| updated_at | TIMESTAMP WITH TIME ZONE | |

UNIQUE(player_id, quest_id).

### kiemhiep_skills

Kỹ năng theo player.

| Cột         | Kiểu     | Mô tả        |
|------------|----------|--------------|
| id         | BIGSERIAL | PK           |
| player_id  | BIGINT   | FK → kiemhiep_players |
| skill_id   | VARCHAR(64) | ID kỹ năng  |
| level      | INT      | Cấp kỹ năng  |
| created_at | TIMESTAMP WITH TIME ZONE | |
| updated_at | TIMESTAMP WITH TIME ZONE | |

UNIQUE(player_id, skill_id).

### kiemhiep_server_metrics

Metrics TPS / tick time ghi định kỳ từ server.

| Cột          | Kiểu     | Mô tả           |
|-------------|----------|-----------------|
| id          | BIGSERIAL | PK              |
| server_id   | VARCHAR(128) | Định danh server |
| ts          | TIMESTAMP WITH TIME ZONE | Thời điểm |
| tps         | DOUBLE PRECISION | TPS      |
| tick_time_ms | BIGINT  | Thời gian tick (ms) |

---

## View

### v_player_overview

Tổng quan player: thông tin cơ bản + cultivation + tổng balance ví.

| Cột                | Mô tả              |
|--------------------|--------------------|
| id                 | player id          |
| uuid               | UUID               |
| name               | Tên                |
| player_created_at  | Lúc tạo            |
| cultivation_level  | Cấp tu vi          |
| cultivation_exp    | Exp tu vi          |
| wallet_balance     | Tổng balance (tất cả currency) |

### v_leaderboard_cultivation

Bảng xếp hạng tu vi (level, exp giảm dần).

| Cột       | Mô tả        |
|-----------|--------------|
| player_id | ID player    |
| uuid      | UUID         |
| name      | Tên          |
| level     | Cấp tu vi    |
| exp       | Exp          |
| rank      | Thứ hạng (ROW_NUMBER) |

---

## Ví dụ query cho web

### Lấy player overview (một player)

```sql
SELECT * FROM v_player_overview WHERE id = ?;
```

### Lấy top 100 leaderboard cultivation

```sql
SELECT player_id, uuid, name, level, exp, rank
FROM v_leaderboard_cultivation
LIMIT 100;
```

### Lấy server metrics gần nhất (theo server_id)

```sql
SELECT server_id, ts, tps, tick_time_ms
FROM kiemhiep_server_metrics
WHERE server_id = ?
ORDER BY ts DESC
LIMIT 1;
```

### Lấy lịch sử TPS 1 giờ gần nhất

```sql
SELECT ts, tps, tick_time_ms
FROM kiemhiep_server_metrics
WHERE server_id = ? AND ts >= NOW() - INTERVAL '1 hour'
ORDER BY ts ASC;
```

---

## Config & security

- **Không commit file config thật:** `config/kiemhiep/database.json` và `config/kiemhiep/redis.json` chứa username/password. Thêm vào `.gitignore` hoặc dùng file mẫu; không đẩy secret lên repo.
- **Production:** Nên đọc password từ biến môi trường hoặc secret manager. Có thể bổ sung hỗ trợ placeholder (vd. `"password": "${DB_PASSWORD}"`) và resolve khi load trong phiên bản sau.
- **Log:** Loader chỉ log đường dẫn file khi lỗi, không in nội dung config hay password.
