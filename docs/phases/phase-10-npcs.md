# Phase 10 — Module NPCs

- **Depends on**: Phase 03 (data)
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: Phase 04, 05, 06 (sau khi 03 xong)

## Sub-tasks

- [ ] **(1)** Models (KiemHiepNPC, NPCDialogue, DialogueCondition, DialogueAction) + interface NPCService (api)
- [ ] **(2)** NPCRepository + migration (npcs, npc_dialogues); prefix kiemhiep_
- [ ] **(3)** NPCServiceImpl: create, get, getDialogues, execute dialogue actions (give item, start quest, …)
- [ ] **(4)** NPCModule: onLoad bind NPCService, onEnable đăng ký listener + command
- [ ] **(5)** Tương tác: Fabric UseEntityCallback hoặc tương đương — right-click entity NPC → mở dialogue; NPC có thể là custom entity hoặc armor stand + interaction
- [ ] **(6)** Commands: /npc create, list, set dialogue; Events: NPCCreateEvent, NPCDeleteEvent, NPCInteractionEvent
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

EntityLimitEnforcer áp dụng khi spawn NPC (Rule 5). NPC type: QUEST_GIVER, SKILL_TRAINER, SHOPKEEPER, … (theo doc).
