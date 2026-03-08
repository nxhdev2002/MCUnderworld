# Phase 07 — Skill — Review sau khi sửa

Đối chiếu với [phase-07-code-review-issues.md](phase-07-code-review-issues.md): trạng thái từng vấn đề sau khi áp dụng các sửa đổi.

---

## Đã xử lý

### [BLOCKING] Thiếu kiểm tra và trừ mana khi dùng skill — **ĐÃ SỬA**

- **Hiện trạng:** Có `ManaProvider` (api/skill), `InMemoryManaProvider` (core/skill). `SkillManager.useSkill` kiểm tra `getCurrentMana` và gọi `consumeMana` trước khi cast/execute; có `UseResult.INSUFFICIENT_MANA`. Module truyền `Optional.of(manaProvider)` vào SkillManager.
- **Kết luận:** Mana được kiểm tra và trừ (một lần khi bắt đầu cast/execute). Không còn BLOCKING.

---

### [SUGGESTION] Lookup skill definition từ DB mỗi lần useSkill — **ĐÃ SỬA**

- **Hiện trạng:** `SkillServiceImpl` có cache `definitionByItemId` (ConcurrentHashMap). `getByItemId` đọc cache trước, miss mới gọi repository rồi put vào cache.
- **Kết luận:** Hot path useSkill không còn đọc DB mỗi lần. Đã xử lý.

---

### [SUGGESTION] Command registration giữ reference cũ khi module disable — **ĐÃ SỬA**

- **Hiện trạng:** `SkillCommands.register` nhận supplier; trong từng execute (list, info, give) gọi `skillServiceSupplier.get()` / `playerRepositorySupplier.get()` và nếu null thì báo "Skill module is disabled." và return 0.
- **Kết luận:** Khi module disable (holder = null), command phản ánh đúng trạng thái. Đã xử lý.

---

### [NIT] SkillContext — getTargetsInRadius() trả về List&lt;?&gt; — **ĐÃ SỬA**

- **Hiện trạng:** `SkillContext.getTargetsInRadius()` trả về `List<EntityAdapter>`; `SkillContextImpl` và `SkillManager.doExecute` dùng đúng kiểu đó.
- **Kết luận:** API rõ kiểu. Đã xử lý.

---

### [QUESTION] Test coverage cho SkillManager — **ĐÃ CẢI THIỆN**

- **Hiện trạng:** Có `SkillManagerTest` (Mockito): test useSkill với INVALID_SKILL, ON_COOLDOWN, ALREADY_CASTING, INSUFFICIENT_MANA (khi ManaProvider có và mana thấp), SUCCESS (khi ManaProvider có và đủ mana).
- **Kết luận:** Logic useSkill đã có test bảo phủ. Đã xử lý.

---

### [SUGGESTION] CastStateManager.tick — callback có thể ném exception — **ĐÃ SỬA**

- **Hiện trạng:** Trong `CastStateManager.tick`, gọi `onComplete.onCastComplete(e.getKey(), e.getValue())` được bọc try-catch; khi catch log (playerId, skillId) và vẫn remove entry (return true).
- **Kết luận:** Exception từ callback không làm hỏng tick loop. Đã xử lý.

---

### [SUGGESTION] SkillManager — kiểm tra mana gọn và tránh race — **ĐÃ SỬA**

- **Hiện trạng:** Chỉ gọi `consumeMana` và kiểm tra kết quả; bỏ bước `getCurrentMana` riêng. Code gọn, tránh race.
- **Kết luận:** Đã xử lý.

---

## Chưa xử lý / Còn mở

### [SUGGESTION] maxRadius từ DB không bị giới hạn (Rule 9) — **CHƯA SỬA (chủ ý)**

- **Ghi chú:** Theo yêu cầu trước đó, tạm thời chưa clamp maxRadius (ví dụ 1.0–10.0). Có thể bổ sung sau nếu cần.

---

### [NIT] CooldownManager — vẫn allocation trong hot path — **MỘT PHẦN**

- **Hiện trạng:** Đã bỏ class `Key`, dùng key dạng string: `playerId.toString() + ":" + skillId` trong method `key()`. Mỗi lần `isOnCooldown` / `setCooldown` / `getCooldownEndTimeMillis` vẫn tạo một String mới.
- **Hệ quả:** Giảm allocation so với Key object nhưng vẫn allocate String mỗi lần gọi.
- **Vị trí:** `CooldownManager.key()`.

---

### [NIT] FabricWorldAdapter.getEntitiesInAabb — tạo list và adapter mỗi lần — **CHƯA SỬA**

- **Hiện trạng:** Không thay đổi so với review trước.
- **Ghi chú:** Có thể để sau khi cần tối ưu thêm.

---

## Vấn đề mới phát hiện sau khi sửa

### [QUESTION] InMemoryManaProvider — mana không hồi theo thời gian

- **Mô tả:** Mana mặc định 100 cho player chưa có bản ghi; sau khi `consumeMana` chỉ còn giảm dần, không có cơ chế hồi theo thời gian hay tick.
- **Hệ quả:** Phù hợp làm implementation tạm/test; khi tích hợp Cultivation/Economy cần provider có persistence và/hoặc hồi mana.
- **Ghi chú:** Không bắt buộc sửa ngay nếu coi đây là placeholder.

---

## Tóm tắt

| Hạng mục | Trước | Sau khi sửa |
|----------|--------|-------------|
| BLOCKING | 1 | 0 (đã sửa) |
| SUGGESTION đã sửa | — | 5 (definition cache, command supplier, test, try-catch cast, mana check gọn) |
| SUGGESTION còn mở | 5 | 1 (maxRadius*, chủ ý chưa clamp) |
| NIT đã sửa | — | 1 (SkillContext List&lt;EntityAdapter&gt;) |
| NIT còn mở | 2 | 2 (CooldownManager String, FabricWorldAdapter) |
| QUESTION | 1 | 1 (InMemoryManaProvider không hồi mana) |

\* maxRadius chủ ý chưa clamp.

---

*Review theo fabric-code-reviewer. Code tham chiếu: SkillManager, SkillModule, SkillServiceImpl, CooldownManager, SkillContext, SkillCommands (server), ManaProvider, InMemoryManaProvider, SkillManagerTest.*
