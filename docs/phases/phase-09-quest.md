# Phase 09 — Module Quest

- **Depends on**: Phase 04, 05 (cultivation, economy)
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: Phase 06, 07 (nếu đã có 04, 05)

## Sub-tasks

- [ ] **(1)** Models (Quest, QuestProgress, reward types) + interface QuestService (api)
- [ ] **(2)** QuestRepository + migration (quests, quest_progress); prefix kiemhiep_
- [ ] **(3)** QuestServiceImpl: start, progress, complete; điều kiện hoàn thành (kill, collect, …); tích hợp event từ combat/cultivation để cập nhật progress
- [ ] **(4)** QuestModule: onLoad bind QuestService, onEnable đăng ký listener + command
- [ ] **(5)** Listener: lắng nghe CombatEvent, CultivationEvent, … để update quest progress; phát thưởng khi complete (CURRENCY, EXPERIENCE, ITEM, SKILL_POINT, CULTIVATION_BOOST)
- [ ] **(6)** Commands: /quest list, start, progress, rewards
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Reward gọi EconomyService, CultivationService; item reward cần platform giveItem. Có thể mở rộng điều kiện (dungeon, NPC) sau.
