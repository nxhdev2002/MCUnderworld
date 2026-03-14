# KiemHiep - Mod Minecraft Kiếm Hiệp

[![Fabric API](https://img.shields.io/badge/Fabric-API-0.141.3+1.21.11-blue)](https://fabricmc.net/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-green)](https://minecraft.net/)
[![License](https://img.shields.io/github/license/kiemhiep/kiemhiep)](LICENSE)

**KiemHiep** là một mod Minecraft Forge/Fabric với chủ đề tiên hiệp kiếm hiệp, mang đến trải nghiệm MMORPG within Minecraft. Với hệ thống tu luyện 10 cảnh giới, combat phong phú, kỹ năng đa dạng và nhiều tính năng hấp dẫn khác.

## 🌟 Tổng Quan

KiemHiep được thiết kế theo kiến trúc modular hiện đại, hỗ trợ cả nền tảng Fabric và Paper/Spigot (tương lai). Đây là một dự án mã nguồn mở, cộng đồng phát triển để mang đến nội dung phong phú và lối chơi sâu sắc.

### Trạng Thái Phát Triển

| Giai đoạn | Tiến độ |
|-----------|---------|
| **Đã hoàn thành** | 23/100 cơ chế (23%) |
| **Đang phát triển** | 10/100 cơ chế (10%) |
| **Kế hoạch** | 67/100 cơ chế (67%) |

```
Đã hoàn thành  ▓▓▓▓░░░░░░░░░░░░░░░░░░  23%
Sắp tới        ░░░░░▓░░░░░░░░░░░░░░░░  10%
Kế hoạch       ░░░░░░░░░░░░░░░░░░░░░░  67%
```

## ✨ Tính Năng Chính

| Hệ thống | Mô tả |
|-----------|-------|
| **🧘 Tu Luyện** | 10 cảnh giới (Mortal → Luyện Khí → Trúc Cơ → Kim Đan → Nguyên Anh → Hóa Thần → Luyện Hư → Hợp Thể → Đại Thừa → Độ Kiếp) |
| **⚔️ Chiến Đấu** | Combat với damage calculation, mana system, kỹ năng, cooldowns |
| **Sect** | Tạo và quản lý tông môn, hệ thống cấp độ, thành viên |
| **🗺️ Dungeon/Boss** | 4 độ khó, boss và mob riêng, loot hệ thống |
| **📜 Kỹ Năng** | 67 kỹ năng với 5 loại (Hỏa, Băng, Sét, Đất, Gió/Độc) |
| **💰 Kinh Tế** | Multi-currency (Vàng, Bạc, Đá Linh) |
| **👤 NPC** | Hệ thống NPC với hội thoại, diálogo, vai trò khác nhau |
| **📜 Nhiệm Vụ** | Quest system với nhiều loại: Chính, Phụ, Hàng ngày, Hàng tuần |

## 🏗️ Kiến Trúc Hệ Thống

```
kiemhiep/
├── common/                  # Common code chung cho server và client
├── server/                  # Server mod - game logic
├── client/                  # Client mod - display và input
├── docs/                    # Documentation
└── README.md                # Document này
```

### Architecture Layers

```
┌───────────────────────────────────────────────────────┐
│          Platform-Specific Layer                      │
│  ┌──────────────┐      ┌───────────────────────┐     │
│  │  Server Mod  │      │     Client Mod        │     │
│  │  (Fabric)    │      │    (Fabric)           │     │
│  └──────────────┘      └───────────────────────┘     │
├───────────────────────────────────────────────────────┤
│              Common Layer                             │
│      (API Interfaces, Models, Events)                │
├───────────────────────────────────────────────────────┤
│           Feature Modules Layer                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────┐  │
│  │Cultivation│ │Combat │ │  Sect  │ │ ... │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────┘  │
└───────────────────────────────────────────────────────┘
```

## ⚙️ Cài Đặt

### Yêu cầu hệ thống

- **Java 21** hoặc mới hơn
- **Fabric Loader** 0.18.4+
- **Fabric API** 0.141.3+1.21.11
- **Minecraft** 1.21.11

### Cài đặt Server (Fabric)

1. **Tải Fabric Loader** từ [fabricmc.net](https://fabricmc.net/)
2. **Cài Fabric API 0.141.3+1.21.11** vào thư mục `mods/`
3. **Copy JAR server** vào thư mục `mods/`
4. **Khởi động server** như bình thường

```
mods/
├── fabric-loader-0.18.4.jar
├── fabric-api-0.141.3+1.21.11.jar
└── kiemhiep-server-1.0.0.jar
```

### Cài đặt Client (Fabric)

1. **Cài Fabric API đúng phiên bản** - **PHẢI** dùng `0.141.3+1.21.11` để tránh lỗi "mod không khớp"
2. **Copy JAR client** vào thư mục `mods/`

```
mods/
├── fabric-api-0.141.3+1.21.11.jar
└── kiemhiep-client-1.0.0.jar
```

### Cấu hình cơ bản

Sau khi chạy lần đầu, file cấu hình sẽ được tạo tại:
```
config/kiemhiep/
├── modules.toml          # Bật/tắt module
├── database.toml         # Cấu hình PostgreSQL (tùy chọn)
└── redis.toml            # Cấu hình Redis (tùy chọn)
```

## 🛠️ Hướng Dẫn Build

### Yêu cầu

- Java 21
- Gradle 8.x (đã bao gồm trong gradlew)

### Build Mod

```bash
# Build server mod
./gradlew :server:build

# Build client mod
./gradlew :client:build

# Build tất cả
./gradlew build

# Clean build
./gradlew clean build
```

### Kết quả build

- **Server JAR**: `server/build/libs/kiemhiep-server-1.0.0.jar`
- **Client JAR**: `client/build/libs/kiemhiep-client-1.0.0.jar`

### Chạy Test

```bash
./gradlew test
```

## 📚 Các Module

Dựa trên code hiện tại, các module đã được implement:

| Module | Trạng thái | Mô tả |
|--------|------------|-------|
| `module-cultivation` | ✅ Hoàn thành | Hệ thống tu luyện 10 cảnh giới (Mortal → Độ Kiếp), mỗi cảnh 9 sub-level |
| `module-skill` | ✅ Hoàn thành | 67 kỹ năng với 5 loại (Hỏa, Băng, Sét, Đất, Gió/Độc) |
| `module-economy` | ✅ Hoàn thành | Multi-currency (Vàng, Bạc, Đá Linh), Wallet repository |
| `module-npcs` | ✅ Hoàn thành | NPC system với dialogue và interaction |
| `module-quest` | ⚙️ Cơ bản | Quest tracking, progress (tích hợp với database) |
| `module-sect` | ⚙️ Cơ bản | Sect repository, CRUD operations |
| `module-combat` | 🚧 Đang làm | Combat logic cơ bản, damage calculation |
| `module-dungeon` | 📋 Kế hoạch | Dungeon, boss, mob systems (chưa implement) |

### Module Details

#### Cultivation Module
- **Classes**: `CultivationModule`, `CultivationServiceImpl`, `CultivationCommands`, `CultivationJoinListener`
- **Tính năng**:
  - 10 cảnh giới (realm), mỗi cảnh 9 sub-level (0-8)
  - Hệ thống EXP và tự động level up
  - Breakthrough: đột phá cảnh giới khi đạt sub-level max
  - `/kiemhiep cultivation` commands

#### Skill Module
- **Classes**: `SkillModule`, `SkillServiceImpl`, `SkillRegistry`, `SkillCommands`, `SkillManager`, `SkillNetworking`
- **67 kỹ năng** chia làm 5 loại:
  - **Hỏa (Fire)**: Fireball, BlazingSword, FlameChompers, InfernoFist, PhoenixFlame, SolarFlare, PyroclasticFlow
  - **Băng (Ice)**: CryoBlast, FrozenCage, IcePrison, ArcticWind, FrozenCage, GlacierSpike
  - **Sét (Thunder)**: Thunder, RagingThunder, ThunderFang, ElectroWave, VajraLightning, QuantumRay
  - **Đất (Earth)**: MudWall, EarthGolem, StoneFist, SeismicPulse, QuakeStomp, SandStorm
  - **Gió (Wind)**: TornadoSweep, GaleSword, ArcticWind, VacuumCut, SonicSlicer
- **Kỹ năng đặc biệt**: Summon (Bear, Wolf, Crab, Owl, EarthGolem), Transformation, TimeBomb, DarkRift
- **Commands**: `/skill list`, `/skill all`, `/skill info <skillId>`, `/skill give`

#### Economy Module
- **Classes**: `EconomyModule`, `EconomyServiceImpl`, `EconomyListener`, `JdbcWalletRepository`
- **Multi-currency**: Vàng (Gold), Bạc (Silver), Đá Linh (Spirit Stone)
- **Transaction system**: Lịch sử giao dịch với `wallets` table (player_id, currency_type, balance)

#### NPCs Module
- **Classes**: `NPCModule`, `NPCServiceImpl`, `NPCListener`, `JdbcNPCRepository`
- **Types**: QUEST_GIVER, SKILL_TRAINER, SHOPKEEPER, GUIDE, MERCHANT, TELEPORTER, SECT_MANAGER
- **Dialogue system**: Hội thoại với options và conditions

#### Sect Module
- **Classes**: `SectModule`, `SectServiceImpl`, `SectListener`, `JdbcSectRepository`
- **Features**: Tạo tông môn, quản lý thành viên, hệ thống cấp độ (level/exp)
- **Relations**: ALLIED (m Warranty), HOSTILE (chiến tranh)

#### Quest Module
- **Classes**: `QuestModule`, `QuestServiceImpl`, `QuestListener`, `JdbcQuestRepository`
- **Types**: MAIN_STORY, SIDE_STORY, DAILY, WEEKLY, EVENT, CHALLENGE
- **Rewards**: CURRENCY, EXPERIENCE, ITEM, SKILL_POINT, CULTIVATION_BOOST

#### Combat Module
- **Classes**: `CombatModule`, `CombatServiceImpl`, `CombatListener`
- **Features**: Damage calculation, mana system, cooldowns, status effects (POISON, SLOW, WEAKEN)
- **Mana/Chân Khí**: Hệ thống mana cho skills

## 📚 Commands

KiemHiep cung cấp các lệnh (commands) sau:

| Command | Mô tả |.Permission |
|---------|-------|-------------|
| `/kiemhiep cultivation info [player]` | Xem thông tin tu luyện của player | Any (self) / OP (other) |
| `/kiemhiep cultivation addExp <player> <amount>` | Thêm EXP tu luyện | OP (level 2+) |
| `/kiemhiep cultivation breakthrough` | Đột phá cảnh giới | Any (must be at max sub-level) |
| `/kiemhiep module list` | Liệt kê modules hiện có | OP (level 2+) |
| `/kiemhiep module reload` | Tải lại cấu hình module | OP (level 2+) |
| `/kiemhiep module enable <id>` | Bật module | OP (level 2+) |
| `/kiemhiep module disable <id>` | Tắt module | OP (level 2+) |
| `/skill list` | Liệt kê kỹ năng của player | Any |
| `/skill all` | Liệt kê tất cả kỹ năng trên hệ thống | OP (level 2+) |
| `/skill info <skillId>` | Xem thông tin kỹ năng | Any |
| `/skill give [player] <skillId> [count]` | Cấp kỹ năng | Any (self) / OP (other) |

## 🌐 API và Integration

KiemHiep cung cấp API mở rộng dễ dàng:

```java
// Lấy thông tin tu luyện
Cultivation cult = cultivationService.getCultivation(playerId).join();

// Thêm kinh nghiệm tu luyện
cultivationService.addExp(playerId, 100).join();

// Tạo tông môn
Sect sect = sectService.createSect(name, leaderId).join();
```

## 🤝 Đóng Góp

Chúng tôi hoan nghênh mọi đóng góp từ cộng đồng!

### Quy trình đóng góp

1. Fork repository
2. Tạo branch tính năng (`git checkout -b feature/amazing-feature`)
3. Commit thay đổi (`git commit -m 'Add amazing feature'`)
4. Push branch (`git push origin feature/amazing-feature`)
5. Mở Pull Request

### Contribution Guidelines

- Code phải tuân thủ style
- Viết test cho chức năng mới
- Cập nhật documentation nếu cần
- Gửi pull request mô tả rõ ràng thay đổi

## 📜 License

Dự án này được cấp phép theo License MIT. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## 🙏 Cảm Ơn

- [FabricMC](https://fabricmc.net/) - Framework mod Minecraft
- Tất cả Contributors và Users của KiemHiep

## 📞 Liên Hệ

- [GitHub Issues](https://github.com/kiemhiep/kiemhiep/issues) - Báo cáo lỗi, đề xuất tính năng
- [Wiki](https://github.com/kiemhiep/kiemhiep/wiki) - Tài liệu chi tiết

---

*Phát triển với ❤️ bởi cộng đồng K65*
