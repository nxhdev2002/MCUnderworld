# Quản lý phase — KiemHiep Fabric

Mỗi phase là một giai đoạn triển khai, quản lý bằng file trong thư mục này. Cập nhật **tiến độ** và **checklist** trong từng file để phân công và làm song song.

## Tổng quan tiến độ

| Phase | Tên | Tiến độ | Depends on | Song song với |
|-------|-----|---------|------------|----------------|
| 01 | [Core module system](phase-01-core-module-system.md) | 100% | — | — |
| 02 | [Platform API](phase-02-platform-api.md) | 0% | 01 | — |
| 03 | [Data layer](phase-03-data-layer.md) | 0% | 02 | — |
| 04 | [Cultivation](phase-04-cultivation.md) | 0% | 01, 02, 03 | 05, 06 |
| 05 | [Economy](phase-05-economy.md) | 0% | 01, 02, 03 | 04, 06 |
| 06 | [Sect](phase-06-sect.md) | 0% | 01, 02, 03 | 04, 05 |
| 07 | [Skill](phase-07-skill.md) | 0% | 04 | — |
| 08 | [Combat](phase-08-combat.md) | 0% | 04, 07 | — |
| 09 | [Quest](phase-09-quest.md) | 0% | 04, 05 | 06, 07 |
| 10 | [NPCs](phase-10-npcs.md) | 0% | 03 | — |
| 11 | [Dungeon](phase-11-dungeon.md) | 0% | 02, 03, 05 | — |

**Tiến độ tổng**: 1 / 11 phase (Phase 01 hoàn thành, đã build + unit test)

## Dependency (thứ tự làm)

```
01 → 02 → 03
            ├→ 04 ──→ 07 ──→ 08
            ├→ 05 ──┘
            ├→ 06        └→ 09
            ├→ 10
            └→ 11 (cần 02, 03, 05)
```

**Có thể làm song song sau khi 01–03 xong**: Phase 04, 05, 06 (và 10, 11 nếu đủ dependency).

## Cách dùng

1. Mở file phase tương ứng (vd. `phase-04-cultivation.md`).
2. Làm lần lượt hoặc giao từng sub-task cho người khác.
3. Khi xong một sub-task: đổi `[ ]` thành `[x]` và cập nhật **Tiến độ** ở đầu file (tính theo số sub-task đã xong / tổng số).
4. **Sau khi implement xong phase**: (a) Chạy **build** (`./gradlew build`); (b) Viết **unit test** cho logic mới, chạy `./gradlew test`. Chỉ coi phase hoàn thành khi build + test đều pass. Trong file phase có thể thêm sub-task "(n) Build + unit test" và đánh dấu khi xong.
5. Cập nhật **README.md** bảng trên: cột Tiến độ từ file phase tương ứng.
