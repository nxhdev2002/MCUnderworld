# Phase 11 — Module Dungeon

- **Depends on**: Phase 02, 03, 05 (platform/limit, data, economy cho reward)
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: Phase 10 (sau khi 03 xong)

## Sub-tasks

- [ ] **(1)** Models (DungeonPortal, DungeonInstance, BossTemplate, BossInstance, MobTemplate, SpawnZone) + interfaces DungeonPortalService, DungeonWorldService, BossService, MobSpawnerService (api)
- [ ] **(2)** Repositories + migrations (dungeon_portals, dungeon_instances, boss_templates, mob_templates, spawn_zones); prefix kiemhiep_
- [ ] **(3)** EntityLimitEnforcer: gọi trước khi spawn mob/boss; config max AI active (vd. 50), max mob per instance (vd. 1 boss + 10 mob) (Rule 5, 7)
- [ ] **(4)** Portal: block hoặc entity đại diện portal; tạo DungeonInstance (world copy hoặc dimension riêng); difficulty EASY/NORMAL/HARD/EXTREME (doc)
- [ ] **(5)** Boss/mob spawn: vanilla entity hoặc custom entity + attributes; không dùng MythicMobs; WeaponLootService, ConsumableFoodCache nếu cần
- [ ] **(6)** DungeonModule: onLoad bind các service, onEnable đăng ký listener (portal spawn, player enter/leave), commands; Events DungeonEnter, DungeonLeave, BossSpawn, BossDeath
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Linh Mạch Ngũ Hành (Kim Mộc Thủy Hỏa Thổ) có thể là thuộc tính portal/instance. Reward currency/exp theo difficulty (doc).
