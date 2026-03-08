# TODO — Phase 07 Skill — Các mục cần fix (sau code review)

Nguồn: review PR #5 (Feat/phase7 skill) + `phase-07-code-review-issues.md` / `phase-07-code-review-after-fixes.md`.

---

## [SUGGESTION] Ưu tiên cao

### 1. Cache `definitionByItemId` không invalidate

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/skill/SkillServiceImpl.java` |
| **Vấn đề** | `definitionByItemId` chỉ put khi miss, không clear khi DB thay đổi (reload/admin). |
| **Việc cần làm** | Thêm method `clearDefinitionCache()` và gọi khi reload skill definitions (nếu có). |
| **Trạng thái** | [x] Đã làm |

```java
// SkillServiceImpl
public void clearDefinitionCache() {
    definitionByItemId.clear();
}
```

---

### 2. Lệnh `/skill all` — quyền truy cập

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/command/SkillCommands.java` |
| **Vấn đề** | Mọi player có thể gọi `/skill all` xem toàn bộ skill trên hệ thống. |
| **Việc cần làm** | Thêm `.requires(s -> s.hasPermission(2))` (hoặc level phù hợp) cho nhánh `skill all`. |
| **Trạng thái** | [x] Đã làm |


## [NIT] Ưu tiên thấp / tùy chọn

### 4. CooldownManager — allocation String mỗi lần gọi

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/skill/CooldownManager.java` |
| **Vấn đề** | `key(playerId, skillId)` tạo String mới mỗi lần `isOnCooldown` / `setCooldown` / `getCooldownEndTimeMillis`. |
| **Việc cần làm** | Cân nhắc cache key (vd. per-player map) hoặc key dạng long; chỉ khi profile thấy cần. |
| **Trạng thái** | [ ] Chưa làm |

---

### 5. FabricWorldAdapter.getEntitiesInAabb — list + adapter mỗi lần

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/platform/FabricWorldAdapter.java` |
| **Vấn đề** | Mỗi lần skill execute tạo ArrayList và FabricEntityAdapter cho từng entity. |
| **Việc cần làm** | Tối ưu sau (reuse list / pool) nếu đo được áp lực allocation. |
| **Trạng thái** | [ ] Chưa làm |

---

### 6. SkillCommands — Stream trong executeList / executeAll

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/command/SkillCommands.java` |
| **Vấn đề** | Dùng stream + reduce để nối chuỗi; command path không phải tick nhưng có thể dùng for + String.join. |
| **Việc cần làm** | Refactor sang `List<String>` + `String.join(", ", parts)` cho gọn và ít allocation. |
| **Trạng thái** | [x] Đã làm |

---

## [QUESTION] Cần quyết định

### 7. InMemoryManaProvider — mana không hồi theo thời gian

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/skill/InMemoryManaProvider.java` (hoặc tương đương) |
| **Vấn đề** | Mana chỉ giảm khi dùng skill, không có cơ chế hồi theo tick/thời gian. |
| **Việc cần làm** | Nếu đây là placeholder → ghi chú rõ; khi tích hợp Cultivation/Economy cần provider có persistence và/hoặc hồi mana. |
| **Trạng thái** | [ ] Chưa quyết định |

---

### 8. /skill give — chưa give item thật

| | |
|---|---|
| **File** | `server/src/main/java/com/kiemhiep/core/command/SkillCommands.java` |
| **Vấn đề** | executeGive chỉ trả message "Use /skill give via SkillModule item registry…", chưa add item vào inventory. |
| **Việc cần làm** | Implement give item qua item registry hoặc cập nhật TODO/comment rõ kế hoạch. |
| **Trạng thái** | [x] Đã làm |

---

## Tóm tắt

| Ưu tiên | Số mục | Ghi chú |
|--------|--------|--------|
| SUGGESTION | 3 | 1–3: cache invalidation, permission /skill all, maxRadius clamp |
| NIT | 3 | 4–6: CooldownManager key, FabricWorldAdapter, SkillCommands stream |
| QUESTION | 2 | 7–8: mana regen, /skill give implementation |

Cập nhật trạng thái bằng cách đánh dấu `[x]` trong từng mục khi hoàn thành.
