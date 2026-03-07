# Phase 07 — Module Skill

- **Depends on**: Phase 04 (cultivation)
- **Tiến độ**: 0% (0/7)
- **Có thể làm song song với**: — (cần cultivation cho mana/level)

## Sub-tasks

- [ ] **(1)** SkillEngine: SkillManager, CooldownManager, EffectManager (server-only); DamageCalculator có thể ở combat (api + impl)
- [ ] **(2)** Models (Skill, PlayerSkill, SkillSlot) + interface SkillService (api)
- [ ] **(3)** SkillRepository + migration (skills, player_skills, skill_slots); seed 20 skills (doc)
- [ ] **(4)** Cooldown server-only (CooldownManager lưu playerId, skillId, cooldownEndTime); update logic mỗi 5–10 tick (Rule 2); skill maxRadius 5–10 block (Rule 9)
- [ ] **(5)** Effect: ưu tiên particle/fake entity, ít entity thật (Rule 3)
- [ ] **(6)** SkillModule: onLoad bind SkillService + SkillEngine components, onEnable đăng ký listener + command
- [ ] **(7)** Listener (hotbar, use skill) + Commands: /skill list, equip, info; client chỉ nhận packet để hiển thị cooldown (Rule 6)
- [ ] **(8)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Tuân thủ minecraft-fabric.md: Server Authority, Skill Tick, Entity Spawn, Cooldown Control, Skill Radius, Skill Engine Architecture.
