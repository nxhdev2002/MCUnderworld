# Code Review: PR #3 – Feat/phase3 cultivation

**Repository:** [nxhdev2002/MCUnderworld](https://github.com/nxhdev2002/MCUnderworld)  
**PR:** [#3 – Feat/phase3 cultivation](https://github.com/nxhdev2002/MCUnderworld/pull/3/changes)  
**Review date:** 2026-03-07  
**Reviewer:** Fabric Code Reviewer (skill)

---

## Summary

PR tách project thành client/server, thêm module cultivation (realm/sub-level, exp, breakthrough), dùng JDBC + cache-aside + Redis message bus, event lifecycle và command. Kiến trúc rõ (api/core/cultivation/platform), SQL dùng PreparedStatement, không có logic nặng mỗi tick. **Cần sửa phân quyền lệnh (BLOCKING)** và vài điểm hiệu năng/API/dependency.

---

## Critical Issues

### [BLOCKING] Lệnh cultivation và module không có kiểm tra quyền

**Why:** `CultivationCommands` và `ModuleCommands` không gọi `.requires(...)`. Mặc định Brigadier dùng permission level 0 → mọi người chơi có thể:

- `/kiemhiep cultivation addExp <player> <amount>` → tăng exp bất kỳ cho bất kỳ player.
- `/kiemhiep cultivation breakthrough` → tự breakthrough.
- `/kiemhiep module enable|disable|reload` → bật/tắt module, reload config.

Dẫn tới lợi dụng gameplay và quản trị.

**Fix:** Gắn permission cho từng nhánh lệnh, ví dụ:

```java
// CultivationCommands – ví dụ: addExp chỉ op/admin
.then(Commands.literal("addExp")
    .requires(source -> source.hasPermission(2))  // op level
    .then(Commands.argument("player", EntityArgument.player())
        ...

// info: self = 0, xem người khác = 2
// breakthrough: 0 (self) hoặc 2 tùy design

// ModuleCommands – list/reload/enable/disable chỉ op
.dispatcher.register(Commands.literal("kiemhiep")
    .requires(source -> source.hasPermission(2))
    .then(module));
```

Chuẩn hóa level (0/2/4) theo từng lệnh (chỉ mình / op / admin).

---

### [BLOCKING] JOIN listener – kiểu tham số handler có thể không khớp Fabric API

**Why:** `ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler, ...)` – Fabric API thường dùng kiểu như `ServerPlayNetworkHandler` (hoặc tên tương đương theo mapping). Dùng `ServerGamePacketListenerImpl` có thể đúng với Mojang mappings 1.21 nhưng cần xác nhận với phiên bản Fabric/API bạn dùng; sai kiểu sẽ lỗi biên dịch hoặc runtime.

**Fix:** Kiểm tra signature thực tế của `ServerPlayConnectionEvents.JOIN` trong Fabric API version đang dùng (Javadoc hoặc source). Nếu API đổi thành `ServerPlayNetworkHandler`, đổi tham số đầu tiên cho đúng và lấy player bằng `handler.getPlayer()` (hoặc API tương ứng). Đảm bảo build và test join server thành công.

---

## Performance Issues

### [SUGGESTION] `addExp` có thể gây nhiều event và một lần lưu lớn

**Why:** Trong `CultivationServiceImpl.addExp`, vòng `while` có thể tăng nhiều level trong một lần gọi (tối đa ~90). Mỗi level tăng gọi `eventDispatcher.fire(CultivationSubLevelUpEvent)` và có thể `CultivationBreakthroughEvent`. Nếu listener làm nặng (DB, gửi packet, scan world) sẽ gây spike khi add exp lớn một lần.

**Fix:** (1) Giới hạn số level-up mỗi lần (ví dụ tối đa 1 realm hoặc N level), phần exp dư áp dụng lần sau hoặc bỏ qua. Hoặc (2) gom event: fire một event “CultivationLevelsChanged(playerId, oldLevel, newLevel)” thay vì từng SubLevelUp/Breakthrough, để listener xử lý một lần. Kèm unit test cho `addExp` với amount rất lớn.

---

### [SUGGESTION] `findAll()` cultivation – load full table

**Why:** `JdbcCultivationRepository.findAll()` SELECT toàn bộ `kiemhiep_cultivation` vào memory. Comment đã ghi “admin or small datasets only” nhưng vẫn dễ bị gọi nhầm từ code khác hoặc từ command không giới hạn → OOM khi số bản ghi lớn.

**Fix:** Giữ `findAll()` cho admin, nhưng: (1) không expose qua command công khai; (2) thêm limit tối đa (ví dụ 1000) hoặc pagination; (3) hoặc đổi tên/package để rõ “admin only”. Nếu có command list cultivation toàn server thì bắt buộc phải limit/pagination.

---

## Fabric API Best Practices

### [SUGGESTION] Client/server split và entrypoint

**Why:** Tách client/server và `environment: "client"` trong fabric.mod.json của client là đúng hướng, tránh load server-only code (DB, Redis, cultivation logic) trên client.

**Fix:** Đảm bảo client không depend vào server subproject; chỉ server đăng ký cultivation commands và JOIN listener. Đã đúng trong PR, chỉ cần giữ và kiểm tra CI/build từng subproject.

---

### [NIT] Unregister command / JOIN listener khi disable module

**Why:** `CultivationModule.onDisable()` comment nói “Fabric does not support unregistering command or JOIN listener”. Đúng là Brigadier/Fabric thường không hỗ trợ unregister command; listener JOIN một khi register thì giữ tới khi server tắt.

**Fix:** Không cần sửa code; nên bật module cultivation chỉ khi server có DB. Nếu sau này muốn “tắt” cultivation thực sự, có thể: trong JOIN listener và command handler kiểm tra `CultivationModule.isEnabled()` và return sớm khi disabled (command vẫn tồn tại nhưng không làm gì).

---

## Java Optimization Suggestions

### [SUGGESTION] Dùng primitive và tránh boxing trong hot path

**Why:** Trong `CultivationServiceImpl` và repository, đa số đã dùng `long`, `int`; record `Cultivation` dùng primitive. Tiếp tục tránh `Integer`/`Long` trong path xử lý exp/level (ví dụ collection, map key) để giảm allocation.

**Fix:** Khi thêm cache key hoặc event payload, ưu tiên `long playerId`, `int level` thay vì boxed. Đã ổn ở code hiện tại, chỉ cần duy trì khi mở rộng.

---

### [SUGGESTION] Dependency Gson không khai báo trong server

**Why:** `CachedCultivationRepository`, `CachedPlayerRepository`, config loaders, `RedisMessageBus` dùng `com.google.gson`. `server/build.gradle` chỉ khai báo Jackson, không khai báo Gson → Gson đang đến từ transitive dependency (Fabric/Minecraft). Phiên bản có thể thay đổi theo loader/Fabric, dễ lệch hành vi hoặc security.

**Fix:** Thêm vào `server/build.gradle`:

```gradle
implementation "com.google.code.gson:gson:2.10.1"
include "com.google.code.gson:gson:2.10.1"
```

Hoặc chuẩn hóa dùng Jackson cho toàn bộ (cache JSON, config, Redis) vì đã có sẵn trong build, tránh hai thư viện JSON.

---

## Suggested Refactoring

### 1. Thêm permission cho cultivation commands (trích đoạn)

```java
// CultivationCommands.java – ví dụ
var cultivation = Commands.literal("cultivation")
    .then(Commands.literal("info")
        .executes(...)
        .then(Commands.argument("player", EntityArgument.player())
            .requires(source -> source.hasPermission(2))
            .executes(...)))
    .then(Commands.literal("addExp")
        .requires(source -> source.hasPermission(2))
        .then(Commands.argument("player", EntityArgument.player())
            ...
```

### 2. (Tùy chọn) Giới hạn level-up mỗi lần trong addExp

```java
// CultivationServiceImpl – ví dụ cap
private static final int MAX_LEVEL_UPS_PER_CALL = 9; // 1 realm

while (level < CultivationRealm.MAX_LEVEL && levelUpsThisCall < MAX_LEVEL_UPS_PER_CALL) {
    // ... existing logic ...
    levelUpsThisCall++;
}
// Optionally: store remainder exp for next tick or cap at current level
```

---

## Điểm tốt

- **Server authority:** Cultivation chỉ xử lý trên server (service + repository), client chỉ nhận qua command/UI.
- **SQL:** Toàn bộ JDBC dùng `PreparedStatement`, không nối chuỗi SQL → tránh injection.
- **Tick:** Chỉ có `ServerTickEvents.END_SERVER_TICK` cho TPSMonitor (cập nhật thời gian tick + callback nhẹ), không có logic cultivation mỗi tick.
- **Schema:** Bảng cultivation có FK, UNIQUE(player_id), index player_id; migration rõ ràng.
- **Kiến trúc:** Tách api / core / cultivation / platform, cache-aside + invalidation qua MessageBus phù hợp multi-instance.

---

**Kết luận:** Ưu tiên sửa **permission cho commands** và **kiểu tham số JOIN listener** trước khi merge; các mục còn lại có thể xử lý trong PR tiếp theo hoặc issue riêng.
