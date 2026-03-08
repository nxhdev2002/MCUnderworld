# Phase 07 — Skill — Code review: các vấn đề

Tài liệu này liệt kê **các vấn đề** phát hiện khi review Phase 07 (module Skill) theo chuẩn fabric-code-reviewer. Không bao gồm phần sửa code.

---

## [BLOCKING] Thiếu kiểm tra và trừ mana khi dùng skill

- **Mô tả:** Phase 07 phụ thuộc Phase 04 (cultivation) để có mana/level. Trong `SkillManager.useSkill` / `doExecute` không có gọi cultivation, không kiểm tra đủ mana và không trừ mana.
- **Hệ quả:** Người chơi có thể dùng skill không giới hạn theo mana.
- **Vị trí:** `SkillManager` (useSkill, doExecute).

---

## [SUGGESTION] CooldownManager — allocation trong hot path

- **Mô tả:** Mỗi lần `isOnCooldown`, `setCooldown`, `getCooldownEndTimeMillis` đều tạo `new Key(playerId, skillId)`.
- **Hệ quả:** Trên nhiều player/skill sẽ tạo nhiều object trong path dùng skill, tăng áp lực GC.
- **Vị trí:** `CooldownManager`.

---

## [SUGGESTION] Lookup skill definition từ DB mỗi lần useSkill

- **Mô tả:** `SkillServiceImpl.useSkill` gọi `definitionRepository.getByItemId(itemId)` mỗi lần dùng skill.
- **Hệ quả:** Trên server đông, đây là DB read trong hot path.
- **Vị trí:** `SkillServiceImpl.useSkill`, gọi từ path cast skill.

---

## [SUGGESTION] CastStateManager.tick — callback có thể ném exception

- **Mô tả:** `CastStateManager.tick` gọi `onComplete.onCastComplete(...)` → `SkillManager.onCastComplete` → `doExecute`. Nếu `doExecute` hoặc platform ném (NPE, lỗi DB, v.v.), exception vọt lên và có thể làm hỏng cả tick loop.
- **Hệ quả:** Một skill lỗi có thể dừng xử lý cast completion cho mọi player trong tick đó.
- **Vị trí:** `SkillModule.onServerTick` (nơi gọi CastStateManager.tick).

---

## [SUGGESTION] Command registration giữ reference cũ khi module disable

- **Mô tả:** `SkillCommands.register` nhận `skillServiceSupplier.get()` và `playerRepositorySupplier.get()` tại thời điểm đăng ký command. Sau đó nếu `SkillModule.onDisable()` set `skillServiceHolder = null`, command vẫn giữ reference tới service cũ (không phải null).
- **Hệ quả:** Khi disable module, command vẫn dùng skill service cũ thay vì phản ánh trạng thái “disabled”.
- **Vị trí:** `SkillCommands.register`, `SkillModule.onDisable`.

## [NIT] SkillContext — getTargetsInRadius() trả về List&lt;?&gt;

- **Mô tả:** API dùng `List<?>` trong khi implementation luôn truyền `List<EntityAdapter>` từ `WorldAdapter.getEntitiesInRadius`. Type quá mở.
- **Hệ quả:** API không rõ kiểu, khó dùng đúng phía skill.
- **Vị trí:** `SkillContext`, `SkillContextImpl`.

---

## [NIT] FabricWorldAdapter.getEntitiesInAabb — tạo list và adapter mỗi lần

- **Mô tả:** `getEntitiesInAabb` tạo `new ArrayList<>(entities.size())` và mỗi entity wrap bằng `new FabricEntityAdapter(e, this)`. Gọi từ `SkillManager.doExecute` mỗi lần skill chạy.
- **Hệ quả:** Tăng allocation; có thể tối ưu sau (reuse list, v.v.).
- **Vị trí:** `FabricWorldAdapter.getEntitiesInAabb`.

---

## [QUESTION] Test coverage cho SkillManager

- **Mô tả:** Chưa có test cho `SkillManager.useSkill` (INVALID_SKILL, ON_COOLDOWN, ALREADY_CASTING, SUCCESS với mock definition + skill). Khi có mana: INSUFFICIENT_MANA.
- **Hệ quả:** Logic useSkill ít được bảo vệ bởi test.
- **Vị trí:** Thiếu test trong `src/test/.../skill/`.

---

## Tóm tắt ưu tiên

| Prefix     | Số lượng | Ý nghĩa                          |
|-----------|----------|-----------------------------------|
| BLOCKING  | 1        | Bắt buộc xử lý trước khi merge   |
| SUGGESTION| 5        | Nên làm để cải thiện/chắc chắn   |
| NIT       | 2        | Nhỏ, tùy chọn                    |
| QUESTION  | 1        | Cần quyết định (ví dụ test)       |

---

*Review theo fabric-code-reviewer: Server Authority, Skill Tick, Entity Spawn, Cooldown Control, Skill Radius, Skill Engine Architecture.*
