# Phase 08 — Module Combat

- **Depends on**: Phase 04, 07 (cultivation, skill)
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: —

## Sub-tasks

- [ ] **(1)** DamageCalculator: công thức damage (cultivation bonus, weapon, crit); CombatStatsCache — cache attack/defense/crit, invalidate khi equipment change (Rule 10)
- [ ] **(2)** CombatService interface + CombatServiceImpl: combat session, damage apply, tích hợp CultivationService + SkillService
- [ ] **(3)** Listener Fabric: attack entity, entity damage — validate server, tính damage, apply (Rule 1)
- [ ] **(4)** Entity query chỉ trong radius (WorldAdapter getEntitiesInBox/radius); skill radius 5–10 block (Rule 4, 9)
- [ ] **(5)** CombatModule: onLoad bind CombatService, onEnable đăng ký listener
- [ ] **(6)** Events: CombatStartEvent, CombatEndEvent, DamageEvent — fire khi bắt đầu/kết thúc combat và khi gây sát thương
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Server-authoritative; client chỉ nhận kết quả để render máu/effect. CombatStatsCache có thể dùng Redis cho multi-instance.
