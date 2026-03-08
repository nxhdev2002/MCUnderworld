# Phase 03 — Data layer

- **Depends on**: Phase 02
- **Tiến độ**: 0% (0/8)
- **Có thể làm song song với**: —

## Sub-tasks

- [x] **(1)** Flyway + DataSource (HikariCP) + config DB (data/ hoặc core/bootstrap)
- [x] **(2)** Migrations: bảng core (kiemhiep_players, kiemhiep_cultivation, kiemhiep_wallets, kiemhiep_sects, kiemhiep_quests, kiemhiep_skills, …)
- [x] **(3)** Repository interfaces + implementations (PlayerRepository, CultivationRepository, WalletRepository, …) — chỉ DB
- [x] **(4)** Schema web: prefix bảng, migration tạo view `v_player_overview`, `v_leaderboard_cultivation`, bảng `kiemhiep_server_metrics`
- [x] **(5)** Viết `docs/web-database-schema.md`: liệt kê bảng/view, mô tả cột, ví dụ query cho web
- [x] **(6)** DistributedCache interface + Redis impl (RedisDistributedCache); config redis.json; key naming `kiemhiep:<domain>:<id>`
- [x] **(7)** MessageBus / CrossInstanceSync: publishInvalidate, subscribeInvalidate; impl Redis Pub/Sub
- [x] **(8)** Repository tích hợp cache-aside: get → cache trước, miss thì DB rồi set cache; save → DB rồi set/delete cache + publish invalidate
- [x] **(9)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Có thể chia: (1)(2)(3)(4)(5) một người, (6)(7)(8) một người. TPSMonitor (phase 02) ghi vào `kiemhiep_server_metrics` định kỳ.
