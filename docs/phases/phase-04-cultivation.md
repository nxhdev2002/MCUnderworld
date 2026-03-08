# Phase 04 — Module Cultivation

- **Depends on**: Phase 01, 02, 03
- **Tiến độ**: 0% (0/7)
- **Có thể làm song song với**: Phase 05, 06

## Sub-tasks

- [x] **(1)** Models (Cultivation, CultivationRealm) + interface CultivationService trong api (api/service, api/model hoặc tương đương)
- [x] **(2)** CultivationRepository (interface đã có ở data) + migration bảng cultivation nếu chưa có
- [x] **(3)** CultivationServiceImpl: 10 realm, 9 sub-level mỗi realm, addExp, setSubLevel, breakthrough; getExpRequired(realmLevel)
- [x] **(4)** CultivationModule: implement KiemHiepModule; onLoad bind CultivationService, onEnable đăng ký listener + command
- [x] **(5)** Listener: player join → load cultivation (get hoặc tạo mặc định), cache/Redis nếu dùng
- [x] **(6)** Commands: xem info tu luyện, addExp (admin), breakthrough (player)
- [x] **(7)** Events: CultivationSubLevelUpEvent, CultivationBreakthroughEvent — fire trong service, đăng ký trong module nếu cần
- [x] **(8)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Module đầu tiên làm mẫu lifecycle onLoad/onEnable/onDisable; các module sau tham khảo.
