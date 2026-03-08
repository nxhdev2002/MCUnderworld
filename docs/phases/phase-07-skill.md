# Phase 07 — Module Skill

- **Depends on**: Phase 04 (cultivation)
- **Tiến độ**: 100% (8/8)
- **Có thể làm song song với**: — (cần cultivation cho mana/level)

## Sub-tasks

- [x] **(1)** SkillEngine: SkillManager, CooldownManager, CastStateManager, EffectManager (server-only); DamageCalculator để Phase 08 (Combat)
- [x] **(2)** Models: SkillDefinition (behavior_id, item_id, is_aoe, is_melee, cast_time, consumable, evolution…), PlayerSkill (Skill); effect interfaces (IThunder, IStunable, IHeal, IAreaDamage, …) + BaseSkill + SkillRegistry; interface SkillService (api)
- [x] **(3)** SkillDefinitionRepository + migration (kiemhiep_skill_definitions); giữ SkillRepository (player_skills); seed skill (vd. FIREBALL); không dùng skill_slots (skill = item)
- [x] **(4)** Cooldown server-only (CooldownManager: playerId, skillId, cooldownEndTime); update mỗi 5–10 tick (Rule 2); skill maxRadius 5–10 block (Rule 9)
- [x] **(5)** Effect: abstraction interface (skill implement → có hiệu ứng, có thể override); ưu tiên particle/fake entity (Rule 3)
- [x] **(6)** SkillModule: onLoad bind SkillService + SkillEngine + đăng ký skill items (placeholder); onEnable đăng ký tick + commands; UseItemCallback (comment do mapping 1.21.11)
- [x] **(7)** Listener (UseItemCallback khi bật lại) + Commands: /skill list, info, give; client nhận S2C để hiển thị cooldown (placeholder SkillNetworking)
- [x] **(8)** Build + unit test: `./gradlew build`, unit test (CooldownManager, SkillRegistry), `./gradlew test`

## Ghi chú

- Tuân thủ minecraft-fabric.md: Server Authority, Skill Tick, Entity Spawn, Cooldown Control, Skill Radius, Skill Engine Architecture.
- **Skill = item**: Mỗi skill có item Fabric riêng (đăng ký item + texture); dùng item = use skill. Consumable (trừ stack) hoặc unlimited.
- **Abstraction**: Hiệu ứng = interface (IThunder, IStunable, …); mỗi skill = class implement interface, có thể override; DB có behavior_id map sang SkillRegistry.
- **Còn bổ sung khi mappings ổn định**: UseItemCallback (TypedActionResult/ResourceLocation), đăng ký item bằng ResourceLocation, S2C payload (SkillCooldownPayload) + client receiver.
