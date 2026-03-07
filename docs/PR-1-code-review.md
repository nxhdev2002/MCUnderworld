# Code Review: PR #1 – feat: Add platform api

**Repository:** [nxhdev2002/MCUnderworld](https://github.com/nxhdev2002/MCUnderworld)  
**PR:** [#1 – feat: Add platform api](https://github.com/nxhdev2002/MCUnderworld/pull/1)  
**Review date:** 2026-03-07  
**Reviewer:** Fabric Code Reviewer (skill)

---

## Summary

PR thêm Platform API (EventDispatcher, PlatformProvider, adapters), LimitsConfigLoader, EntityLimitEnforcer, TPSMonitor và implementation Fabric. Kiến trúc tách api/core/platform rõ, tuân thủ “chỉ query entity trong vùng” (Rule 4) và có unit test cho core. Một số điểm cần chỉnh về thread-safety, TOCTOU khi spawn, allocation trong tick và độ chính xác doc.

---

## Critical Issues

**Hiện không có issue BLOCKING.** Các điểm dưới là SUGGESTION/QUESTION để tránh lỗi khi mở rộng hoặc dùng trong môi trường đa luồng.

---

## Performance Issues

### [SUGGESTION] Allocation mỗi tick trong `TPSMonitor.update()`

**Why:** Nếu `metricsCallback != null`, mỗi tick (20 lần/giây) tạo `new double[] { lastTps, lastTickTimeMs }`, vi phạm rule “Avoid Object Allocation in Tick Loops”.

**Fix:** Dùng một mảng hoặc holder tái sử dụng:

```java
private final double[] metricsBuffer = new double[2];

public void update(long tickTimeMs) {
    this.lastTickTimeMs = Math.max(1L, tickTimeMs);
    this.lastTps = 1000.0 / this.lastTickTimeMs;
    Consumer<double[]> cb = metricsCallback;
    if (cb != null) {
        metricsBuffer[0] = lastTps;
        metricsBuffer[1] = lastTickTimeMs;
        cb.accept(metricsBuffer);
    }
}
```

### [NIT] `FabricWorldAdapter.getEntitiesInAabb()`

Tạo nhiều `FabricEntityAdapter` mỗi lần gọi; với box lớn sẽ nhiều allocation. Chấp nhận được ở giai đoạn này; nếu sau này đo được đây là hot path thì có thể cân nhắc pool/reuse adapter.

---

## Fabric API & Server Authority

- **WorldAdapter:** `getEntitiesInBox` / `getEntitiesInRadius` dùng `level.getEntitiesOfClass(Entity.class, box)` với AABB giới hạn — đúng Rule 4 (không scan toàn world).
- **Tick:** `onServerTickEnd` chỉ đo tick time và cập nhật TPS, logic rất nhẹ — phù hợp chạy mỗi tick.
- Server vẫn là nơi set server vào `FabricPlatformProvider` và tính TPS; module chỉ đọc qua context — phù hợp server authority.

---

## Correctness & Thread-Safety

### [SUGGESTION] Race giữa `fire()` và `unregister()` trong `EventDispatcherImpl`

**Why:** `handlersByType` dùng `ConcurrentHashMap`, nhưng value là `ArrayList`. Khi một thread gọi `new ArrayList<>(list)` trong `fire()` và thread khác gọi `list.remove(handler)` trong `unregister()`, có thể dẫn tới CME hoặc hành vi không xác định.

**Fix:** Dùng cấu trúc thread-safe cho danh sách handler, ví dụ `CopyOnWriteArrayList`:

```java
handlersByType.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
```

### [SUGGESTION] TOCTOU trong `EntityLimitEnforcer`

**Why:** Giữa `canSpawn(type)` và `recordSpawn(type)` có thể có thread khác cũng spawn cùng type → vượt limit.

**Fix:** Nếu cần đảm bảo không bao giờ vượt limit, nên có một API “atomic”: ví dụ `tryRecordSpawn(entityType)` trả về true nếu còn slot và đã tăng count, false nếu đã đạt limit (và không tăng). Module gọi một lần thay vì canSpawn + recordSpawn tách rời.

---

## Architecture

### [SUGGESTION] Cast `PlatformProvider` sang `FabricPlatformProvider` trong bootstrap

**Why:** `onServerStarted` cast `(FabricPlatformProvider) platformProvider` và gọi `setServer(server)` — bootstrap phụ thuộc implementation Fabric.

**Fix:** Có thể đưa lifecycle “set server” vào platform: ví dụ interface `ServerAware` với `setServer(MinecraftServer)` và bootstrap chỉ gọi nếu `platformProvider instanceof ServerAware`, hoặc đăng ký `SERVER_STARTED` trong package `platform` và inject server ở đó, để bootstrap không cần biết FabricPlatformProvider.

---

## Java & API Best Practices

### [NIT] Javadoc `EventDispatcher.fire()`

- Doc: “Gửi event tới mọi handler đã đăng ký cho type của event **(và supertype)**”.
- Thực tế impl chỉ dispatch theo **exact type** (event.getClass()). Nên sửa doc cho khớp (bỏ “và supertype” hoặc ghi rõ “chỉ exact type”).

### [NIT] `FabricPlatformProvider.getWorld(String worldId)`

- So khớp bằng `level.dimension().toString()`. Format `ResourceKey` (vd. `minecraft:overworld`) có thể khác giữa các phiên bản. Nên ghi trong Javadoc format `worldId` callers phải dùng (ví dụ “dimension registry key string, e.g. minecraft:overworld”) để tránh sai format giữa module và impl.

---

## Testing

- **LimitsConfigLoaderTest, EventDispatcherTest, EntityLimitEnforcerTest, ModuleContextImplTest:** Coverage tốt cho config, event, limit và context.
- **ModuleLoaderTest:** Đã cập nhật dùng `StubEventDispatcher` và `StubPlatformProvider` — đúng hướng.
- Fabric adapters không có unit test (cần Minecraft env) — chấp nhận được; có thể bổ sung integration test sau.

---

## Suggested Refactoring (tóm tắt)

1. **TPSMonitor:** Tránh allocate `double[]` mỗi tick khi có callback (dùng buffer/holder reuse).
2. **EventDispatcherImpl:** Dùng `CopyOnWriteArrayList` (hoặc cơ chế thread-safe tương đương) cho danh sách handler.
3. **EntityLimitEnforcer:** Cân nhắc API atomic “tryRecordSpawn” nếu muốn đảm bảo không vượt limit khi đa luồng.
4. **Bootstrap:** Tách việc “set server” vào platform (interface lifecycle hoặc đăng ký event trong package platform) để bỏ cast sang `FabricPlatformProvider`.
5. **Doc:** Sửa Javadoc `EventDispatcher.fire()` và mô tả rõ format `worldId` trong `PlatformProvider.getWorld()`.

---

## Kết luận

PR có thể merge sau khi cân nhắc các SUGGESTION trên; ưu tiên chỉnh TPSMonitor allocation và EventDispatcherImpl thread-safety nếu code chạy trong môi trường đa luồng hoặc tick callback được dùng. Các điểm còn lại có thể xử lý trong PR tiếp theo hoặc follow-up.
