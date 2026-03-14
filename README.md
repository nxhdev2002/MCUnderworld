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
| **itles** | Tạo và quản lý tông môn, hệ thống cấp độ, thành viên |
| **🗺️ Dungeon/Boss** | 4 độ khó, boss và mob riêng, loot hệ thống |
| **📜 Kỹ Năng** | 20 kỹ năng chia làm 5 loại (Kiếm, Khí, Thân, Độc, Phong) |
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

| Module | Trạng thái | Mô tả |
|--------|------------|-------|
| `module-cultivation` | ⚡ 20% | Hệ thống tu luyện 10 cảnh giới |
| `module-combat` | ⚔️ 17% | Combat system, damage calculation |
| `module-sect` | 🏯 33% | Tông môn, members, relations |
| `module-dungeon` | 🗺️ 50% | Dungeon, boss, mob systems |
| `module-quest` | 📜 20% | Quest tracking, progress |
| `module-skill` | 👤 20% | 20 kỹ năng, system |
| `module-economy` | 💰 100% | Multi-currency system |
| `module-npcs` | 👤 100% | NPC system, dialogue |

Xem chi tiết trong [docs/phases/README.md](docs/phases/README.md)

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
- Tất cảContributors và Users của KiemHiep

## 📞 Liên Hệ

- [GitHub Issues](https://github.com/kiemhiep/kiemhiep/issues) - Báo cáo lỗi, đề xuất tính năng
- [Wiki](https://github.com/kiemhiep/kiemhiep/wiki) - Tài liệu chi tiết

---

*Phát triển với ❤️ bởi cộng đồng K65*
