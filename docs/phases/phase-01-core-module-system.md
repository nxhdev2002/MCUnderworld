# Phase 01 — Core module system

- **Depends on**: —
- **Tiến độ**: 100% (7/7)
- **Có thể làm song song với**: —

## Sub-tasks

- [x] **(1)** Định nghĩa `KiemHiepModule` interface và `ModuleContext` (api/module)
- [x] **(2)** Implement `ModuleRegistry` + đăng ký danh sách module (core/module)
- [x] **(3)** Implement `ModuleLoader`: enable/disable theo dependency (topological), gọi onLoad/onEnable/onDisable (core/module)
- [x] **(4)** ConfigLoader đọc `config/kiemhiep/modules.json` (enabled per module) (core/config)
- [x] **(5)** Brigadier command: `/kiemhiep module list`, `reload`, `enable <id>`, `disable <id>` (core/command)
- [x] **(6)** Tích hợp vào `Kiemhiep.onInitialize()`: KiemhiepBootstrap load modules theo config
- [x] **(7)** Build + unit test: `./gradlew build` pass, unit test JUnit 5 (`ConfigLoaderTest`, `ModuleRegistryImplTest`, `ModuleLoaderTest`, `FakeModule`), `./gradlew test` pass

## Ghi chú

Sau phase này server có thể bật/tắt từng module qua config và lệnh; chưa có logic nghiệp vụ (chỉ skeleton module). Unit test: `src/test/java` — ConfigLoader (Path constructor), ModuleRegistryImpl, ModuleLoader (loadAll, applyConfig, enable/disable).
