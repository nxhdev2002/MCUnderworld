# Phase 06 — Module Sect

- **Depends on**: Phase 01, 02, 03
- **Tiến độ**: 0% (0/6)
- **Có thể làm song song với**: Phase 04, 05

## Sub-tasks

- [ ] **(1)** Models (Sect, SectMember, SectRelation) + interface SectService (api)
- [ ] **(2)** SectRepository + migration (sects, sect_members, sect_relations); prefix kiemhiep_
- [ ] **(3)** SectServiceImpl: create, join, leave, relation (ALLIED, HOSTILE, …), list members, getSect(uuid)
- [ ] **(4)** SectModule: onLoad bind SectService, onEnable đăng ký listener + command
- [ ] **(5)** Listener (nếu cần) + Commands: /sect create, join, leave, list, info, relation
- [ ] **(6)** Events: SectJoinEvent, SectLeaveEvent, SectRelationChangeEvent
- [ ] **(7)** Build + unit test: `./gradlew build`, viết unit test, `./gradlew test`

## Ghi chú

Quan hệ tông môn dùng cho combat/quest sau này; có thể cache sect theo player và theo sect_id trên Redis.
