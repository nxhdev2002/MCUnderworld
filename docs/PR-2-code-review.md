# Code Review: PR #2 – Feat/phase3 data contract

**Repository:** [nxhdev2002/MCUnderworld](https://github.com/nxhdev2002/MCUnderworld)  
**PR:** [#2 – Feat/phase3 data contract](https://github.com/nxhdev2002/MCUnderworld/pull/2)  
**Review date:** 2026-03-07  
**Reviewer:** Fabric Code Reviewer (skill)

---

## Summary

PR thêm data contract (model + repository API), PostgreSQL (HikariCP + Flyway), Redis (cache + message bus), cache-aside cho Player/Cultivation, và ServerMetricsRecorder ghi TPS vào DB theo chu kỳ. Kiến trúc api/model, api/repository, core/database, core/cache, core/sync rõ ràng; schema và migration đồng bộ với docs. **Cần xử lý dứt điểm merge conflict trong `KiemhiepBootstrap.java` (BLOCKING)** và một số điểm resource lifecycle, security, performance.

---

## Critical Issues

### [BLOCKING] Merge conflict còn sót trong `KiemhiepBootstrap.java`

**Why:** File chứa dấu vết merge chưa resolve:

- **Import trùng:** `EventDispatcher` và `PlatformProvider` được import hai lần.
- **Field trùng:** `eventDispatcher`, `platformProvider`, `limitsConfigLoader`, `entityLimitEnforcer`, `tpsMonitor` được khai báo hai lần (khối static field).
- **Javadoc trùng:** Hai đoạn giống nhau cho class.
- **Lambda trùng:** Trong `contextFactory` có hai dòng giống hệt `new ModuleContextImpl(moduleId, registry, moduleConfigLoader, eventDispatcher, platformProvider);`

Code sẽ **không compile** (duplicate variable declaration).

**Fix:** Xóa toàn bộ dòng trùng, chỉ giữ một bộ import, một bộ field, một đoạn Javadoc, một dòng trong lambda. Kiểm tra lại toàn bộ file sau khi resolve conflict.

---

## Security

### [SUGGESTION] Credential trong config file

**Why:** `database.json` và `redis.json` chứa username/password; file nằm trên disk, dễ lộ nếu backup hoặc clone repo nhầm.

**Fix:** Ghi rõ trong docs: không commit file config thật (dùng .gitignore); production nên đọc password từ biến môi trường hoặc secret manager. Có thể bổ sung hỗ trợ placeholder trong config (vd. `"password": "${DB_PASSWORD}"`) và resolve lúc load.

### [NIT] Redis password

Password Redis đưa vào `RedisURI.builder().withPassword(...)`. Đảm bảo config không bị log (trong RedisConfigLoader/RedisConfig không in password ra log là ổn).

---

## Correctness

### [SUGGESTION] Swallow exception trong `ServerMetricsRecorder.onMetrics`

**Why:** `catch (Exception ignored)` và `lastRecordTime.set(last)` để retry; mọi lỗi (DB, network) đều bị bỏ qua, khó debug.

**Fix:** Ít nhất log ở mức WARN/DEBUG, ví dụ:  
`Kiemhiep.LOGGER.warn("Failed to record server metrics", e);`

### [NIT] Cache parse error trong Cached*Repository

**Why:** `fromJson` trả về `null` khi parse lỗi; `Optional.ofNullable(fromJson(cached))` thành `Optional.empty()`. Client nhận “không có dữ liệu” thay vì lỗi, có thể che dữ liệu cache bị hỏng.

**Fix:** Khi parse lỗi có thể xóa key cache và fallback về delegate (cache-aside correct), ví dụ: nếu `fromJson` null thì `cache.delete(key)` rồi gọi delegate.

### [NIT] Redis SET + EXPIRE không atomic

**Why:** `RedisDistributedCache.set()`: gọi `commands.set(key, value)` rồi mới `commands.expire(key, ...)`. Giữa hai lệnh có thể bị evict hoặc key tồn tại vĩnh viễn nếu expire lỗi.

**Fix:** Dùng SET với option EX (Lettuce: `SetArgs.Builder.ex(ttlSeconds)`) hoặc SETEX nếu API hỗ trợ, để set + TTL trong một lệnh.

---

## Performance

### [SUGGESTION] Repository.findAll() không giới hạn

**Why:** `JdbcCultivationRepository.findAll()` và `JdbcSectRepository.findAll()` dùng `Statement` và `SELECT * FROM table`; bảng lớn sẽ load hết vào memory.

**Fix:** Thêm pagination (limit/offset hoặc cursor) hoặc ghi rõ trong Javadoc rằng method chỉ dùng cho admin/set nhỏ; tránh gọi trong hot path.

### [NIT] ServerMetricsRecorder ghi từ main tick thread

**Why:** `onMetrics` được gọi từ TPSMonitor callback (trên tick thread); khi đến interval thì gọi `repository.save()` (JDBC). Nếu DB chậm có thể block tick.

**Fix:** Ghi bất đồng bộ (queue + worker thread) hoặc chấp nhận rủi ro và ghi rõ trong doc; nếu giữ sync thì nên có timeout/connection pool phù hợp.

---

## Resource & Lifecycle

### [SUGGESTION] Redis client/connection không được đóng

**Why:** `RedisDistributedCache` và `RedisMessageBus` tạo `RedisClient` và connection; bootstrap không đăng ký hook đóng khi server dừng → rò rỉ connection/thread.

**Fix:** Thêm `ServerLifecycleEvents.SERVER_STOPPING` (hoặc tương đương) để gọi `close()` trên Redis cache và message bus; hoặc đăng ký shutdown hook.

### [SUGGESTION] DataSourceHolder không đóng

**Why:** `DataSourceHolder.close()` tồn tại nhưng không được gọi khi server stop; HikariCP pool giữ connection.

**Fix:** Trong lifecycle server stopping, gọi `DataSourceHolder.close()` nếu đã tạo (ví dụ qua `getDataSource()` hoặc holder riêng).

### [SUGGESTION] RedisMessageBus subscriber thread không dừng

**Why:** `subscribeInvalidate` start daemon thread block trên `pubSub.subscribe(CHANNEL)`; khi server shutdown thread vẫn có thể đang block, không có cơ chế unsubscribe/close.

**Fix:** Trong method `close()` của RedisMessageBus: unsubscribe channel, đóng connection, sau đó mới shutdown client; bootstrap gọi close khi server dừng.

---

## Architecture & API

### [QUESTION] ModuleContext không có DataSource / Cache / MessageBus

**Why:** Bootstrap expose `getDataSource()`, `getDistributedCache()`, `getMessageBus()`; `ModuleContextImpl` chỉ có registry, config, eventDispatcher, platformProvider. Module muốn dùng DB/cache phải gọi trực tiếp `KiemhiepBootstrap.get*()`.

**Clarify:** Có chủ đích giữ module context “core only” và phase 3 chỉ dùng static getter, hay sẽ bổ sung DataSource/Cache/MessageBus vào ModuleContext (hoặc một context riêng) trong PR sau?

### [NIT] CachedCultivationRepository.deleteById

**Why:** Gọi `delegate.getById(id)` rồi mới delete và invalidate cache; nếu id không tồn tại thì không invalidate (và không có gì để xóa). Hợp lý nhưng tốn một round-trip. Chấp nhận được; có thể ghi chú trong doc.

---

## Testing

### EntityLimitEnforcerTest — removal của tryRecordSpawn

**Why:** Diff xóa 3 test: `tryRecordSpawn_returnsTrueAndIncrementsWhenUnderLimit`, `tryRecordSpawn_returnsFalseWhenAtLimit`, `tryRecordSpawn_returnsFalseForNull`. Nghĩa là trên nhánh này `EntityLimitEnforcer` không có method `tryRecordSpawn`.

**Clarify:** Nếu `tryRecordSpawn` đã tồn tại ở nhánh khác và bị mất do merge, cần khôi phục method + test; nếu chủ đích bỏ atomic spawn thì ổn, nhưng nên tránh mất behavior không chủ ý.

### Flyway + H2 trong test

**Why:** Migration V1/V2 dùng cú pháp PostgreSQL (BIGSERIAL, etc.). Test dependency có H2; nếu test chạy Flyway với H2 có thể lỗi schema.

**Fix:** Hoặc dùng profile/DB test là PostgreSQL (vd. Testcontainers), hoặc có bộ migration riêng cho H2 / skip migration trong unit test; ghi rõ trong README hoặc doc test.

---

## Điểm tốt

- **Parameterized SQL:** Toàn bộ JDBC dùng `PreparedStatement` và set parameter, không nối chuỗi → tránh SQL injection.
- **Schema & docs:** `docs/web-database-schema.md` và migration V1/V2 khớp nhau; view và index hợp lý cho web/leaderboard.
- **Cache-aside + invalidation:** CachedPlayerRepository / CachedCultivationRepository làm rõ flow: miss → delegate → set cache; save/delete → invalidate và publish.
- **Throttle metrics:** ServerMetricsRecorder dùng interval 10s và CAS để tránh ghi quá dày, phù hợp rule “batch / throttle”.
- **Unit test:** CacheKeysTest, NoOpDistributedCacheTest, DatabaseConfigLoaderTest, RedisConfigLoaderTest, ServerMetricsRecorderTest, CachedPlayerRepositoryTest đủ để tin vào behavior chính.

---

## Suggested refactoring (tóm tắt)

1. **KiemhiepBootstrap:** Resolve merge conflict (xóa duplicate import/field/javadoc/lambda); đảm bảo compile và chạy đúng.
2. **Lifecycle:** Đăng ký SERVER_STOPPING (hoặc tương đương) để: đóng Redis (cache + message bus), đóng DataSourceHolder, dừng subscriber Redis đúng cách.
3. **ServerMetricsRecorder:** Log khi save metrics thất bại (ít nhất WARN); cân nhắc ghi metrics qua queue/worker để không block tick.
4. **RedisDistributedCache.set:** Dùng SET với TTL trong một lệnh (SETEX hoặc SetArgs).
5. **Repository.findAll:** Thêm pagination hoặc ghi rõ trong doc chỉ dùng cho admin/set nhỏ.
6. **Config & credential:** Doc hướng dẫn không commit secret; cân nhắc env var cho production.

---

## Kết luận

- **BLOCKING:** Phải sửa xong merge conflict trong `KiemhiepBootstrap.java` (xóa mọi dòng trùng) trước khi merge.
- **Nên làm:** Đóng Redis và DataSource khi server dừng; log lỗi khi ghi metrics; cân nhắc SETEX/atomic TTL cho Redis.
- **Tùy chọn:** Bổ sung DataSource/Cache/MessageBus vào ModuleContext nếu muốn module không phụ thuộc static getter; làm rõ chiến lược test DB (H2 vs PostgreSQL) và giữ/khôi phục `tryRecordSpawn` nếu cần.

Sau khi xử lý BLOCKING và các SUGGESTION lifecycle/security chính, PR phù hợp để merge cho phase 3 data contract.
