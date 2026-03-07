# Tài Liệu Chi Tiết Hệ Thống KiemHiep

## Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc Hệ Thống](#kiến-trúc-hệ-thống)
3. [Cấu Trúc Module](#cấu-trúc-module)
4. [Cơ Sở Dữ Liệu](#cơ-sở-dữ-liệu)
5. [Chi Tiết Các Module](#chi-tiết-các-module)
6. [Hệ Thống Event](#hệ-thống-event)
7. [API và Platform Abstraction](#api-và-platform-abstraction)
8. [Hướng Dẫn Phát Triển](#hướng-dẫn-phát-triển)
9. [Tiến Độ Phát Triển](#tiến-độ-phát-triển)

---

## Tổng Quan

**KiemHiep** là một plugin/mod Minecraft chủ đề tiên hiệp kiếm hiệp, được thiết kế theo kiến trúc modular để hỗ trợ đa nền tảng (Fabric và Paper/Spigot).

### Tính Năng Chính

- **Tu Luyện System**: 10 cảnh giới tu luyện từ Mortal đến Độ Kiếp
- **Chiến Đấu**: Combat system với damage calculation dựa trên tu vi và kỹ năng
- **Tông Môn**: System tạo và quản lý tông môn
- **Dungeon/Boss**: Bí cảnh với boss và mob riêng
- **Kỹ Năng**: 20 kỹ năng chia làm 5 loại (Kiếm, Khí, Thân, Độc, Phong)
- **Kinh Tế**: Multi-currency system (Gold, Silver, Spirit Stone)
- **NPC**: NPC system với dialogue và interaction
- **Nhiệm Vụ**: Quest system với nhiều loại nhiệm vụ

### Trạng Thái Phát Triển

- **Đã hoàn thành**: 23/100 cơ chế (23%)
- **Đang phát triển**: 10/100 cơ chế (10%)
- **Kế hoạch**: 67/100 cơ chế (67%)

---

## Kiến Trúc Hệ Thống

### Cấu Trúc Project

```
kiemhiep/
├── kiemhiep-api/           # API module - Platform-agnostic interfaces
├── kiemhiep-core/          # Core module - Paper-specific plugin (riêng biệt)
├── kiemhiep-data/          # Data module - Database repositories
├── kiemhiep-fabric/        # Fabric mod implementation
├── module-cultivation/     # Tu luyện module
├── module-combat/          # Combat module
├── module-sect/            # Tông môn module
├── module-dungeon/         # Dungeon/Boss module
├── module-quest/           # Nhiệm vụ module
├── module-skill/           # Kỹ năng module
├── module-economy/         # Kinh tế module
├── module-npcs/            # NPC module
└── docs/                   # Documentation
```

### Kiến Trúc Layer

```
┌─────────────────────────────────────────────────────────┐
│              Platform-Specific Layer                    │
│  ┌─────────────────┐  ┌─────────────────────────────┐  │
│  │  kiemhiep-core  │  │     kiemhiep-fabric         │  │
│  │  (Paper/Spigot) │  │     (Fabric Mod)            │  │
│  └─────────────────┘  └─────────────────────────────┘  │
├─────────────────────────────────────────────────────────┤
│              Feature Modules Layer                      │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐  │
│  │Cultivation│ │Combat │ │ Sect  │ │  Dungeon    │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────────┘  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────────┐  │
│  │  Quest  │ │ Skill │ │Economy│ │   NPCs      │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────────┘  │
├─────────────────────────────────────────────────────────┤
│              Data Layer                                 │
│  ┌─────────────────────────────────────────────────┐   │
│  │          kiemhiep-data (Repositories)           │   │
│  └─────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────┤
│              API Layer (Platform-Agnostic)              │
│  ┌─────────────────────────────────────────────────┐   │
│  │  kiemhiep-api (Interfaces, Models, Events)      │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Dependency Injection

Hệ thống sử dụng **Guice** cho dependency injection:

```java
// Ví dụ: FabricModule.java
public class FabricModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CultivationService.class)
            .to(CultivationServiceImpl.class)
            .in(Singleton.class);
        bind(EconomyService.class)
            .to(EconomyServiceImpl.class)
            .in(Singleton.class);
    }
}
```

---

## Cấu Trúc Module

### kiemhiep-api

Module chứa các interface, model, và event platform-agnostic.

#### Cấu Trúc

```
kiemhiep-api/
├── model/           # Data models
├── service/         # Service interfaces
├── event/           # Event classes
├── platform/        # Platform abstraction interfaces
├── module/          # Module interface
└── config/          # Configuration interfaces
```

#### Models Chính

| Model | Mô Tả |
|-------|-------|
| `Cultivation` | Thông tin tu luyện của player (realm, subLevel, exp) |
| `CultivationRealm` | Định nghĩa cảnh giới tu luyện |
| `CombatStats` | Stats chiến đấu (attack, defense, crit, v.v.) |
| `CombatSession` | Session chiến đấu giữa các entities |
| `Sect` | Tông môn thông tin |
| `SectMember` | Thành viên tông môn |
| `SectRelation` | Quan hệ giữa các tông môn |
| `Quest` | Nhiệm vụ thông tin |
| `QuestProgress` | Tiến độ nhiệm vụ |
| `Wallet` | Wallet currency của player |
| `Transaction` | Lịch sử giao dịch |
| `Skill` | Kỹ năng thông tin |
| `PlayerSkill` | Kỹ năng của player |
| `SkillSlot` | Slot kỹ năng equip |
| `DungeonPortal` | Portal dungeon |
| `DungeonInstance` | Instance dungeon đang hoạt động |
| `BossTemplate` | Template boss |
| `BossInstance` | Boss đang hoạt động |
| `MobTemplate` | Template mob |
| `SpawnZone` | Khu vực spawn mob |
| `ConsumableFood` | Thực phẩm tiêu thụ |
| `KiemHiepNPC` | NPC thông tin |
| `NPCDialogue` | Hội thoại NPC |
| `WeaponItem` | Vũ khí thông tin |

#### Service Interfaces

| Service | Mô Tả |
|---------|-------|
| `CultivationService` | Quản lý tu luyện, đột phá, sub-level up |
| `CombatService` | Quản lý combat, damage calculation |
| `SectService` | Quản lý tông môn, members, relations |
| `QuestService` | Quản lý quests, progress, rewards |
| `EconomyService` | Quản lý currency, transactions |
| `SkillService` | Quản lý skills, cooldowns, slots |
| `WeaponLootService` | Quản lý weapon drops |
| `MobSpawnerService` | Quản lý mob spawning |
| `BossService` | Quản lý boss spawning, tracking |
| `DungeonWorldService` | Quản lý dungeon instances |
| `DungeonPortalService` | Quản lý dungeon portals |
| `NPCService` | Quản lý NPCs, dialogues |
| `SkillEffectHandler` | Xử lý skill effects |

#### Events

**Cultivation Events:**
- `CultivationSubLevelUpEvent` - Khi player tăng sub-level
- `CultivationBreakthroughEvent` - Khi player đột phá cảnh giới

**Combat Events:**
- `CombatStartEvent` - Khi combat bắt đầu
- `CombatEndEvent` - Khi combat kết thúc
- `DamageEvent` - Khi gây sát thương
- `CombatEvent` - Base combat event
- `PlayerDeathEvent` - Khi player chết

**Skill Events:**
- `SkillUseEvent` - Khi sử dụng skill
- `SkillLearnEvent` - Khi học skill
- `SkillLevelUpEvent` - Khi skill level up

**Sect Events:**
- `SectJoinEvent` - Khi gia nhập tông môn
- `SectLeaveEvent` - Khi rời tông môn
- `SectRelationChangeEvent` - Khi quan hệ tông môn thay đổi

**Quest Events:**
- `QuestStartEvent` - Khi bắt đầu quest
- `QuestProgressEvent` - Khi quest progress
- `QuestCompleteEvent` - Khi hoàn thành quest

**Economy Events:**
- `TransactionEvent` - Khi có giao dịch
- `WalletUpdateEvent` - Khi wallet thay đổi

**Dungeon Events:**
- `DungeonPortalSpawnEvent` - Khi portal spawn
- `DungeonPortalRemoveEvent` - Khi portal remove
- `DungeonEnterEvent` - Khi vào dungeon
- `DungeonLeaveEvent` - Khi rời dungeon
- `BossSpawnEvent` - Khi boss spawn
- `BossDeathEvent` - Khi boss chết

**NPC Events:**
- `NPCCreateEvent` - Khi tạo NPC
- `NPCDeleteEvent` - Khi xóa NPC
- `NPCInteractionEvent` - Khi tương tác NPC

### kiemhiep-data

Module chứa database repositories và cấu hình.

#### Cấu Trúc

```
kiemhiep-data/
├── database/
│   ├── DatabaseConfig.java      # Cấu hình database connection
│   ├── DatabaseMigrator.java    # Flyway migration
│   └── migrations/              # SQL migration files
├── redis/
│   └── RedisConfig.java         # Cấu hình Redis
└── repository/
    ├── PlayerRepository.java
    ├── CultivationRepository.java
    ├── SectRepository.java
    ├── QuestRepository.java
    ├── WalletRepository.java
    ├── NPCRepository.java
    ├── BossTemplateRepository.java
    ├── DungeonPortalRepository.java
    └── ...
```

#### Database Migration Files

| File | Mô Tả |
|------|-------|
| `V1__Create_core_tables.sql` | Core tables (players) |
| `V2__Create_cultivation_tables.sql` | Cultivation system tables |
| `V3__Create_skill_tables.sql` | Skills & skill slots |
| `V4__Create_sect_tables.sql` | Sect system |
| `V5__Create_economy_tables.sql` | Economy/wallet system |
| `V6__Create_quest_tables.sql` | Quest system |
| `V7__Create_npc_tables.sql` | NPC system |
| `V8__Create_boss_dungeon_tables.sql` | Boss & dungeon |
| `V10__Create_mob_templates.sql` | Mob templates |
| `V11__Boss_templates_and_spawn_zones.sql` | Boss templates & spawn zones |
| `V12__Seed_cultivation_realms.sql` | Seed cultivation realms (10 levels) |
| `V13__Create_consumable_food.sql` | Food/consumable items |
| `V14__Add_mythic_mob_type_to_boss_templates.sql` | MythicMobs integration |
| `V15__Seed_mythic_mob_type_for_boss_templates.sql` | Seed MythicMobs |
| `V16__Seed_dungeon_content.sql` | Seed dungeon content |
| `V17__Add_skill_costs.sql` | Skill costs |
| `V18__Seed_skills.sql` | 20 skills (sword, qi, body, poison, wind) |
| `V19__Seed_skill_trainer_npc.sql` | Skill trainer NPC |
| `V20__Add_npc_skin_type_and_signature.sql` | NPC skin system |
| `V21__Add_skill_icon_custom_model_data.sql` | Skill icons |
| `V22__Add_skill_type_to_skills.sql` | Skill types |

#### Cultivation Realms (10 Levels)

1. **Mortal** (level 0) - Người thường
2. **Luyện Khí** (level 1) - Rèn luyện khí
3. **Trúc Cơ** (level 2) - Xây dựng nền tảng
4. **Kim Đan** (level 3) - Kết đan
5. **Nguyên Anh** (level 4) - Anh nguyên
6. **Hóa Thần** (level 5) - Hóa thần
7. **Luyện Hư** (level 6) - Luyện hư vô
8. **Hợp Thể** (level 7) - Hợp nhất cơ thể
9. **Đại Thừa** (level 8) - Thừa kế đạo
10. **Độ Kiếp** (level 9) - Vượt kiếp

Mỗi cảnh giới có 9 sub-levels (0-8).

### kiemhiep-fabric

Fabric mod implementation, sử dụng Fabric API.

#### Cấu Trúc

```
kiemhiep-fabric/
├── src/main/java/da/kiemhiep/fabric/
│   ├── KiemHiepMod.java         # Main mod class
│   ├── FabricModule.java        # Guice module
│   ├── adapter/
│   │   ├── FabricPlatformProvider.java
│   │   ├── FabricEventBus.java
│   │   ├── FabricPlayerAdapter.java
│   │   ├── FabricWorldAdapter.java
│   │   ├── FabricEntityAdapter.java
│   │   └── FabricLocationAdapter.java
│   ├── command/
│   │   └── CharacterCommand.java
│   ├── config/
│   │   ├── FabricPluginConfig.java
│   │   └── FabricLoreConfig.java
│   ├── listener/
│   │   ├── FabricFirstJoinListener.java
│   │   └── FabricScoreboardListener.java
│   ├── loader/
│   │   └── FabricModuleLoader.java
│   └── ui/
│       └── FabricScoreboardManager.java
└── src/main/resources/
    ├── fabric.mod.json
    └── kiemhiep.properties
```

#### Platform Abstraction

**FabricPlatformProvider** implement các interface từ kiemhiep-api:

```java
public class FabricPlatformProvider implements PlatformProvider {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public CompletableFuture<Optional<PlayerAdapter>> getPlayerAdapter(UUID uuid) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
        return CompletableFuture.completedFuture(
            Optional.ofNullable(player).map(FabricPlayerAdapter::new)
        );
    }
    // ... các method khác
}
```

---

## Chi Tiết Các Module

### module-cultivation

**Chức năng**: Quản lý hệ thống tu luyện

#### Classes Chính

- `CultivationModule.java` - Module entry point
- `CultivationServiceImpl.java` - Service implementation
- `PlayerJoinListener.java` - Listener khi player join

#### API

```java
public interface CultivationService {
    // Get/Set cultivation
    CompletableFuture<Cultivation> getCultivation(UUID playerId);
    CompletableFuture<Void> setCultivation(UUID playerId, Cultivation cultivation);

    // Add exp
    CompletableFuture<Integer> addExp(UUID playerId, int amount);

    // Level up
    CompletableFuture<Void> setSubLevel(UUID playerId, int subLevel);
    CompletableFuture<CultivationBreakthroughResult> breakthrough(UUID playerId);

    // Get realm info
    List<CultivationRealm> getRealms();
    int getExpRequired(int realmLevel);
}
```

#### CultivationBreakthroughResult

```java
public class CultivationBreakthroughResult {
    private final boolean success;
    private final CultivationRealm oldRealm;
    private final CultivationRealm newRealm;
}
```

### module-combat

**Chức năng**: Quản lý chiến đấu

#### Classes Chính

- `CombatModule.java` - Module entry point
- `CombatServiceImpl.java` - Service implementation
- `FabricCombatListener.java` - Fabric combat listener
- `FiveElementAnvilListener.java` - Ngũ hành anvil listener
- `CombatWeaponReader.java` - Weapon loot service
- `SkillServiceStub.java` - Skill service stub

#### Damage Calculation

```java
// Công thức damage cơ bản
damage = baseDamage * (1 + cultivationBonus) * (1 + weaponBonus) * critMultiplier;

// Cultivation bonus
cultivationBonus = cultivationLevel * 0.1; // 10% mỗi level
```

### module-sect

**Chức năng**: Quản lý tông môn

#### Classes Chính

- `SectModule.java` - Module entry point
- `SectServiceImpl.java` - Service implementation
- `SectListener.java` - Event listener

#### Sect Structure

```java
public class Sect {
    private UUID id;
    private String name;
    private UUID leaderUuid;
    private int level;
    private int exp;
    private List<SectMember> members;
    private Map<UUID, SectRelation> relations;
}

public class SectMember {
    private UUID playerId;
    private String rank; // Leader, Elder, Member, Novice
    private long joinDate;
    private int contribution;
}
```

### module-dungeon

**Chức năng**: Quản lý dungeon, boss, mob

#### Classes Chính

- `DungeonModule.java` - Module entry point
- `DungeonWorldServiceImpl.java` - Dungeon world service
- `DungeonPortalServiceImpl.java` - Portal service
- `BossServiceImpl.java` - Boss service
- `MobSpawnerServiceImpl.java` - Mob spawner service
- `WeaponLootServiceImpl.java` - Weapon loot service
- `ConsumableFoodCache.java` - Food cache
- `PortalShapeRenderer.java` - Portal shape renderer

#### Dungeon Difficulties

| Difficulty | Portal Size | Currency Reward | Exp Reward |
|------------|-------------|-----------------|------------|
| EASY | 4x5 | 100-500 | 50-200 |
| NORMAL | 5x6 | 500-2000 | 200-800 |
| HARD | 6x7 | 2000-10000 | 800-3000 |
| EXTREME | 7x8 | 10000-50000 | 3000-15000 |

### module-quest

**Chức năng**: Quản lý nhiệm vụ

#### Classes Chính

- `QuestModule.java` - Module entry point
- `QuestServiceImpl.java` - Service implementation
- `QuestListener.java` - Event listener

#### Quest Types

- `MAIN_STORY` - Nhiệm vụ chính
- `SIDE_STORY` - Nhiệm vụ phụ
- `DAILY` - Nhiệm vụ hàng ngày
- `WEEKLY` - Nhiệm vụ hàng tuần
- `EVENT` - Nhiệm vụ sự kiện
- `CHALLENGE` - Nhiệm vụ thử thách

#### Quest Reward Types

- `CURRENCY` - Tiền thưởng
- `EXPERIENCE` - Exp tu luyện
- `ITEM` - Vật phẩm
- `SKILL_POINT` - Điểm kỹ năng
- `CULTIVATION_BOOST` - Boost tu luyện

### module-skill

**Chức năng**: Quản lý kỹ năng

#### Classes Chính

- `SkillModule.java` - Module entry point
- `SkillServiceImpl.java` - Service implementation
- `SkillHotbarListener.java` - Hotbar listener
- `SkillInventoryListener.java` - Inventory listener

#### Skill Types

| Type | Skills |
|------|--------|
| **Sword** | sword_basic, sword_slash, sword_thrust, sword_whirlwind, sword_final_blow |
| **Qi** | qi_gather, qi_shield, qi_blast, qi_heal, qi_burst |
| **Body** | body_dash, body_jump, body_dodge, body_rage, body_meditation |
| **Poison** | poison_fang, poison_cloud, poison_cure |
| **Wind** | wind_blade, storm_call |

### module-economy

**Chức năng**: Quản lý kinh tế

#### Classes Chính

- `EconomyModule.java` - Module entry point
- `EconomyServiceImpl.java` - Service implementation
- `EconomyListener.java` - Event listener

#### Currency Types

| Currency | Base Value |
|----------|------------|
| GOLD | 10000 |
| SILVER | 100 |
| SPIRIT_STONE | 1 (base currency) |

### module-npcs

**Chức năng**: Quản lý NPC

#### Classes Chính

- `NPCModule.java` - Module entry point
- `NPCServiceImpl.java` - Service implementation
- `NPCListener.java` - Event listener

#### NPC Types

- `QUEST_GIVER` - NPC phát nhiệm vụ
- `SKILL_TRAINER` - NPC dạy kỹ năng
- `SHOPKEEPER` - NPC bán hàng
- `GUIDE` - NPC hướng dẫn
- `MERCHANT` - NPC thương nhân
- `TELEPORTER` - NPC dịch chuyển
- `SECT_MANAGER` - NPC quản lý tông môn
- `CUSTOM` - NPC tùy chỉnh

#### NPC Dialogue Structure

```java
public class NPCDialogue {
    private UUID id;
    private UUID npcId;
    private String dialogueText;
    private List<String> options;
    private List<DialogueCondition> conditions;
    private List<DialogueAction> actions;
}
```

---

## Hệ Thống Event

### Event Dispatcher

```java
public interface EventDispatcher {
    <T extends Event> void fire(T event);
    <T extends Event> void register(Class<T> eventType, EventConsumer<T> consumer);
    <T extends Event> void unregister(Class<T> eventType, EventConsumer<T> consumer);
}
```

### Event Hierarchy

```
Event (interface)
├── PlayerEvent
│   ├── PlayerJoinEvent
│   ├── PlayerQuitEvent
│   └── PlayerDeathEvent
├── CombatEvent
│   ├── CombatStartEvent
│   ├── CombatEndEvent
│   └── DamageEvent
├── CultivationEvent
│   ├── CultivationSubLevelUpEvent
│   └── CultivationBreakthroughEvent
├── SkillEvent
│   ├── SkillUseEvent
│   ├── SkillLearnEvent
│   └── SkillLevelUpEvent
├── SectEvent
│   ├── SectJoinEvent
│   ├── SectLeaveEvent
│   └── SectRelationChangeEvent
├── QuestEvent
│   ├── QuestStartEvent
│   ├── QuestProgressEvent
│   └── QuestCompleteEvent
├── EconomyEvent
│   ├── TransactionEvent
│   └── WalletUpdateEvent
├── DungeonEvent
│   ├── DungeonPortalSpawnEvent
│   ├── DungeonEnterEvent
│   ├── DungeonLeaveEvent
│   ├── BossSpawnEvent
│   └── BossDeathEvent
└── NPCEvent
    ├── NPCCreateEvent
    ├── NPCDeleteEvent
    └── NPCInteractionEvent
```

---

## API và Platform Abstraction

### PlatformProvider Interface

```java
public interface PlatformProvider {
    Platform getPlatform();
    CompletableFuture<Optional<PlayerAdapter>> getPlayerAdapter(UUID uuid);
    CompletableFuture<List<PlayerAdapter>> getOnlinePlayers();
    boolean isPlayerOnline(UUID uuid);
    void broadcast(String message);
    CompletableFuture<Optional<WorldAdapter>> getWorldAdapter(String worldName);
    CompletableFuture<Optional<WorldAdapter>> getMainWorldAdapter();
    CompletableFuture<Optional<EntityAdapter>> getEntityAdapter(UUID entityUuid);
    void runTask(Runnable task);
    void runTaskLater(long delayTicks, Runnable task);
    void runTaskAsync(Runnable task);
    void cancelTask(Object taskId);
    boolean hasPermission(UUID playerUuid, String permission);
    void setPermission(UUID playerUuid, String permission, boolean value);
    Scheduler getScheduler();
    EffectPlayer getEffectPlayer();
    DungeonPortalService getDungeonPortalService();
    DungeonWorldService getDungeonWorldService();
}
```

### PlayerAdapter Interface

```java
public interface PlayerAdapter {
    UUID getUuid();
    String getName();
    void sendMessage(String message);
    void sendMessage(String message, String... placeholders);
    void sendActionBar(String message);
    Location getLocation();
    void teleport(Location location);
    CompletableFuture<Boolean> teleportAsync(Location location);
    int getHealth();
    void setHealth(int health);
    default void setHealth(double health) { setHealth((int) health); }
    int getMaxHealth();
    boolean isOnline();
    boolean isOp();
    World getWorld();
    void giveItem(String material, int amount);
    void removeItem(String material, int amount);
    boolean hasItem(String material, int amount);
    void addEffect(String effectName, int duration, int amplifier);
    void removeEffect(String effectName);
    boolean hasEffect(String effectName);
    int getPotionEffectDuration(String effectName);
    void setGameMode(String gameMode);
    String getGameMode();
    void kick(String reason);
    void ban(String reason);
    void playSound(String soundName);
    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);
    int getExperience();
    void setExperience(int experience);
    int getLevel();
    void setLevel(int level);
    int getFoodLevel();
    void setFoodLevel(int foodLevel);
    Object getRawPlayer();
    boolean hasPermission(String permission);
    WeaponItem getWeaponInMainHand();
}
```

### WorldAdapter Interface

```java
public interface WorldAdapter {
    String getName();
    Location getSpawnLocation();
    void setSpawnLocation(Location location);
    List<UUID> getPlayers();
    List<UUID> getEntities();
    boolean isChunkLoaded(int x, int z);
    CompletableFuture<Void> loadChunk(int x, int z);
    void unloadChunk(int x, int z);
    long getTime();
    void setTime(long time);
    boolean isDay();
    boolean isThundering();
    void setThundering(boolean thundering);
    UUID spawnEntity(String entityType, Location location);
    boolean removeEntity(UUID entityId);
    String getBlockType(int x, int y, int z);
    void setBlockType(int x, int y, int z, String blockType);
    void spawnParticle(String particleType, double x, double y, double z, int count);
    void spawnParticle(String particleType, double x, double y, double z, int count,
                       double offsetX, double offsetY, double offsetZ);
    void spawnParticle(String particleType, double x, double y, double z, int count,
                       double offsetX, double offsetY, double offsetZ, double extra,
                       int r, int g, int b);
    void playSound(double x, double y, double z, String sound, float volume, float pitch);
    void setBlock(int x, int y, int z, String material);
    String getBlock(int x, int y, int z);
    Optional<LocationAdapter> getLocationAt(double x, double y, double z, float yaw, float pitch);
    Optional<EntityAdapter> getEntity(UUID entityId);
    void clearEntityDrops(UUID entityId);
}
```

### EntityAdapter Interface

```java
public interface EntityAdapter {
    UUID getUuid();
    Location getLocation();
    double getHealth();
    void setHealth(double health);
    double getMaxHealth();
    void damage(double amount);
    void heal(double amount);
    boolean isAlive();
    boolean isValid(); // default: return isAlive()
    boolean isDead();
    void addEffect(String effectName, int duration, int amplifier);
    void removeEffect(String effectName);
    boolean hasEffect(String effectName);
    String getType();
    void setCustomName(String name);
    String getCustomName(); // default
    boolean isCustomNameVisible();
    void setCustomNameVisible(boolean visible);
    void setInvulnerable(boolean invulnerable);
    void setMaxHealth(double health);
    void setNbt(String key, String value);
    String getNbt(String key);
    boolean hasNbt(String key); // default: return getNbt(key) != null
    void remove();
    double[] getVelocity();
    void setVelocity(double[] velocity);
    void teleport(Location location);
    String getItemInMainHand();
    void setItemInMainHand(String material);
    Object getRawEntity();
}
```

---

## Hướng Dẫn Phát Triển

### Thêm Module Mới

1. **Tạo module folder**: `module-<name>/`

2. **Tạo module class**:
```java
public class MyModule implements KiemHiepModule {
    @Override
    public String getId() { return "my-module"; }

    @Override
    public String getName() { return "My Module"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void onLoad() { /* init */ }

    @Override
    public void onEnable(Injector injector) { /* enable */ }

    @Override
    public void onDisable() { /* disable */ }

    @Override
    public List<Class<?>> getDependencies() { return List.of(); }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public void setEnabled(boolean enabled) { }
}
```

3. **Register trong FabricModule**:
```java
bind(MyService.class).to(MyServiceImpl.class).in(Singleton.class);
bind(MyListener.class).in(Singleton.class);
```

4. **Register listeners trong FabricEventBus**:
```java
MyListener listener = injector.getInstance(MyListener.class);
// Register Fabric events và call listener methods
```

### Thêm Service Mới

1. **Định nghĩa interface trong kiemhiep-api**:
```java
public interface MyService {
    CompletableFuture<Result> doSomething(UUID playerId);
}
```

2. **Implement trong module**:
```java
@Singleton
public class MyServiceImpl implements MyService {
    @Inject
    public MyServiceImpl(Repository repo) { }

    @Override
    public CompletableFuture<Result> doSomething(UUID playerId) {
        // implementation
    }
}
```

### Thêm Event Mới

1. **Tạo event class trong kiemhiep-api**:
```java
public class MyEvent implements Event {
    private final UUID playerId;
    private final String data;

    public MyEvent(UUID playerId, String data) {
        this.playerId = playerId;
        this.data = data;
    }

    public UUID getPlayerId() { return playerId; }
    public String getData() { return data; }
}
```

2. **Fire event**:
```java
eventDispatcher.fire(new MyEvent(playerId, data));
```

3. **Register listener**:
```java
eventDispatcher.register(MyEvent.class, event -> {
    // handle event
});
```

### Thêm Database Table

1. **Tạo migration file** trong `kiemhiep-data/src/main/resources/migrations/`:
```sql
-- V23__Create_my_table.sql
CREATE TABLE IF NOT EXISTS my_table (
    id VARCHAR(36) PRIMARY KEY,
    player_id VARCHAR(36) NOT NULL,
    data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

2. **Tạo repository**:
```java
public interface MyRepository {
    CompletableFuture<MyData> getData(UUID playerId);
    CompletableFuture<Void> saveData(UUID playerId, MyData data);
}
```

3. **Implement repository**:
```java
@Singleton
public class SqlMyRepository implements MyRepository {
    private final DataSource dataSource;

    @Inject
    public SqlMyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CompletableFuture<MyData> getData(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM my_table WHERE player_id = ?")) {
                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // map result
                }
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }
}
```

### Build và Test

```bash
# Build Fabric mod
./gradlew :kiemhiep-fabric:build

# Build Paper plugin (kiemhiep-core)
./gradlew :kiemhiep-core:build

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

---

## Tiến Độ Phát Triển

### Tổng Quan Roadmap 100 Cơ Chế

Hệ thống KiemHiep đang phát triển với lộ trình 100 cơ chế game:

| Trạng Thái | Số Lượng | Tỷ Lệ |
|------------|----------|-------|
| ✅ Đã triển khai | 23/100 | 23% |
| 🚧 Sắp tới | 10/100 | 10% |
| 📅 Kế hoạch | 67/100 | 67% |

```
Đã hoàn thành  ▓▓▓▓░░░░░░░░░░░░░░░░░░  23%
Sắp tới        ░░░░░▓░░░░░░░░░░░░░░░░  10%
Kế hoạch       ░░░░░░░░░░░░░░░░░░░░░░  67%
```

### Cơ Chế Đã Triển Khai (23/100)

#### 🧘 Tu Luyện (3/15)
- **Tu Tiên Giai Đoạn**: 10 cấp độ (Mortal → Luyện Khí → Trúc Cơ → Kim Đan → Nguyên Anh → Hóa Thần → Luyện Hư → Hợp Thể → Đại Thừa → Độ Kiếp)
- **Sub-level (0-8)**: Mỗi cảnh giới có 9 sub-level
- **EXP System**: Kinh nghiệm tu luyện theo cấp độ
- **Events**: `CultivationSubLevelUpEvent`, `CultivationBreakthroughEvent`

#### ⚔️ Chiến Đấu (5/30)
- **Chân Khí (Mana)**: Hệ thống mana cho skills
- **Hút Huyết/Độc Tấn Công**: Skills gây độc và sát thương
- **Điểm Huyệt Phong Địch (Debuff)**: POISON, SLOW, WEAKEN
- **Trận Pháp Chiến Đấu**: Area damage skills
- **Combo Chiêu Thức**: Cooldown system

#### 🏯 Tông Môn (5/15)
- **Gia Nhập Tông Môn**: Tạo và quản lý tông môn
- **Tông Chủ Đấu Trận**: Leader system
- **Tông Môn Phúc Lợi**: Level và exp tông môn
- **Tông Môn Chiến Tranh**: Relation HOSTILE
- **Tông Môn Minh Ước**: Relation ALLIED

#### 🗺️ Dungeon/Thế Giới (2/4)
- **Bí Cảnh Vô Địch**: 4 độ khó (EASY, NORMAL, HARD, EXTREME)
- **Linh Mạch Ngũ Hành**: Kim, Mộc, Thủy, Hỏa, Thổ

#### 📜 Nhiệm Vụ (2/10)
- **Độc Hành Sứ Mệnh**: Quest tracking
- **Nhóm Nhiệm Vụ**: Event/Challenge quests

#### 🗡️ Đồ Vật (1/10)
- **Thiên Tài Địa Bảo**: Consumable food từ mobs

#### 👤 Kỹ Năng (2/10)
- **Huyền Khí Thần Thông**: QI_BLAST, STORM_CALL
- **Thân Pháp Thần Thông**: BODY_DASH, BODY_JUMP, BODY_DODGE

#### 🌟 Kinh Tế/Xã Hội (3/10)
- **Hệ Thống Kinh Tế**: Multi-currency (Gold, Silver, Spirit Stone)
- **Thương Nhân NPC**: SHOPKEEPER, MERCHANT
- **Thiên Địa Đại Sự Kiện**: Portal spawn events

### Cơ Chế Sắp Tới (10/100) - Ưu Tiên Cao

| # | Cơ Chế | Mô Tả | Module |
|---|--------|-------|--------|
| 1 | **Công Pháp Tu Luyện** | Các công pháp khác nhau với thuộc tính riêng | `module-cultivation` |
| 2 | **Đan Dược Luyện Đan** | Nấu thuốc hỗ trợ tu luyện | `module-cultivation` |
| 3 | **Huyền Khí** | Elemental damage (sấm, lửa, băng) | `module-skill` |
| 4 | **Pháp Bảo** | Đồ vật có phép thuật, skills riêng | `module-items` |
| 5 | **Ngự Kiếm Phi Hành** | Bay trên kiếm | `module-movement` |
| 6 | **Thiên Kiếp** | Sấm sét khi đột phá | `module-cultivation` |
| 7 | **Ngẫu Nhiên Sự Kiện** | Sự kiện随机 trên bản đồ | `module-events` |
| 8 | **Tiên Kiếm** | Kiếm tiên có thuộc tính | `module-items` |
| 9 | **Tiên Trại** | Auction house, player trading | `module-economy` |
| 10 | **Huynh Đệ Kết Nghi** | Brotherhood system | `module-social` |

### Tính Năng Chưa Hoàn Thành (TODO)

#### 1. Tích Hợp Quest với Dungeon/Combat
- Cập nhật tiến độ quest khi kill boss/mob
- Phát thưởng quest (CURRENCY, EXPERIENCE, ITEM, SKILL_POINT, CULTIVATION_BOOST)

#### 2. Skill NPC Trainer
- Kiểm tra NPC type trước khi mở menu (SKILL_TRAINER)

#### 3. Combat
- Sát thương cho entity không phải Player
- Xuyên giáp (armor penetration)

#### 4. Skill - Mana/Utility
- Hệ thống mana hoàn chỉnh (regen, cost)

#### 5. Commands & Permissions
- Lệnh người chơi: `/sect`, `/quest`, `/skill`, `/economy`
- Permission system cho các module

#### 6. Tích Hợp Kiến Trúc
- Velocity proxy (cross-server sync)
- Skript integration
- Multiverse-Core/Portals/Inventories

#### 7. Testing & BDD
- Kịch bản BDD testing
- Integration tests

### Phân Tích Theo Module

| Module | Đã Có | Kế Hoạch | Tổng |
|--------|-------|----------|------|
| `module-cultivation` | 3 | 14 | 17 |
| `module-combat` | 5 | 25 | 30 |
| `module-sect` | 5 | 10 | 15 |
| `module-dungeon` | 2 | 2 | 4 |
| `module-quest` | 2 | 8 | 10 |
| `module-skill` | 2 | 8 | 10 |
| `module-economy` | 3 | 2 | 5 |
| `module-items` | 1 | 9 | 10 |
| `module-npcs` | 2 | 0 | 2 |
| `module-world` | 0 | 10 | 10 |
| `module-social` | 0 | 4 | 4 |
| `module-events` | 1 | 5 | 6 |
| `module-movement` | 0 | 1 | 1 |

### Tài Liệu Tham Khảo

- [mechanics-roadmap.md](mechanics-roadmap.md) - Chi tiết 100 cơ chế
- [TIEN_DO_HOAN_THANH.md](TIEN_DO_HOAN_THANH.md) - Tiến độ hoàn thành
- [minecraft-boss-resource.md](minecraft-boss-resource.md) - Boss resource pack
- [skill-resource-pack-icons.md](skill-resource-pack-icons.md) - Skill icons
- [Minecraft_Skill_Effect_Guide.md](Minecraft_Skill_Effect_Guide.md) - Skill effect guide

---

## Phụ Lục

### File Cấu Hình

#### fabric.mod.json

```json
{
  "schemaVersion": 1,
  "id": "kiemhiep",
  "version": "${version}",
  "name": "KiemHiep",
  "description": "Kiếm Hiệp themed mod - modular cultivation system",
  "authors": ["KiemHiep Team"],
  "license": "MIT",
  "environment": "server",
  "entrypoints": {
    "server": ["da.kiemhiep.fabric.KiemHiepMod"]
  },
  "depends": {
    "fabricloader": ">=0.15.11",
    "minecraft": "~1.21",
    "fabric-api": "*"
  }
}
```

### Database Schema Diagram

```
players
├── player_id (PK)
├── name
└── first_join

cultivation
├── player_id (PK, FK → players)
├── realm_level
├── sub_level
└── exp

wallets
├── player_id (PK, FK → players)
├── currency_type
└── balance

sects
├── sect_id (PK)
├── name
├── leader_uuid
├── level
└── exp

sect_members
├── sect_id (FK → sects)
├── player_id (FK → players)
├── rank
└── contribution

quests
├── quest_id (PK)
├── name
├── type
└── reward_type

quest_progress
├── player_id (FK → players)
├── quest_id (FK → quests)
├── status
└── progress

skills
├── skill_id (PK)
├── name
├── type
└── mana_cost

player_skills
├── player_id (FK → players)
├── skill_id (FK → skills)
└── level

dungeon_portals
├── portal_id (PK)
├── world_key
├── block_x, block_y, block_z
├── difficulty
└── five_elements

boss_templates
├── boss_id (PK)
├── name
├── entity_type
└── health

npcs
├── npc_id (PK)
├── name
├── type
└── location
```

---

*Tài liệu được cập nhật lần cuối: 2026-03-07*
