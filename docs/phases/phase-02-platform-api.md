# Phase 02 — Platform API

- **Depends on**: Phase 01
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: —

## Sub-tasks

- [x] **(1)** EventDispatcher + event classes (api/event) — fire, register, unregister
- [x] **(2)** api.platform: interfaces `PlatformProvider`, `PlayerAdapter`, `WorldAdapter`, `EntityAdapter`, `Location` (hoặc record)
- [x] **(3)** Fabric adapters: FabricPlatformProvider, FabricPlayerAdapter, FabricWorldAdapter, FabricEntityAdapter, FabricLocationAdapter (platform/)
- [x] **(4)** WorldAdapter: API `getEntitiesInBox(origin, radius)` / getEntitiesInRadius — không scan cả world (Rule 4)
- [x] **(5)** EntityLimitEnforcer + config `config/kiemhiep/limits.json` (Mob 1000, Projectile 1000, NPC 500, …) (core/limit)
- [x] **(6)** TPSMonitor (optional): đọc tick time/TPS, ghi vào biến hoặc callback cho server_metrics (core/monitor)
- [x] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Platform không phụ thuộc module feature; modules sẽ inject PlatformProvider và EventDispatcher.
