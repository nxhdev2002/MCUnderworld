# Minecraft Fabric MMORPG Performance Rules

Guidelines for implementing MMORPG / kiếm hiệp gameplay systems on a Minecraft Fabric server while maintaining high TPS and low server resource usage.

---

# 1. Server Authority Rule

The server must be authoritative for all gameplay logic.

Server responsibilities:

* Damage calculation
* Skill validation
* Cooldown validation
* Collision detection
* Status effects

Client responsibilities:

* Particle rendering
* Animations
* Sound effects
* UI display

Recommended pattern:

```
Client -> Send skill request
Server -> Validate + calculate damage
Server -> Broadcast skill event
Client -> Render effects
```

The server must never trust the client for gameplay decisions.

---

# 2. Skill Tick Rule

Avoid updating skill logic every tick.

Minecraft runs at:

```
20 ticks per second
```

Bad practice:

```
skill.update() every tick
```

Recommended:

```
skill.update() every 5–10 ticks
```

Example:

```
20 ticks = 1 second
update every 5 ticks = 4 updates per second
```

This can reduce CPU usage by up to 80%.

---

# 3. Entity Spawn Rule

Entities are the most expensive objects in the server.

Bad practice:

```
spawn 20 projectile entities per skill
```

With 100 players:

```
100 × 20 = 2000 entities
```

Recommended alternatives:

* client-side particles
* fake entity packets
* visual-only effects

Spawn real entities only when required for:

* collision
* AI behavior
* physical interaction

---

# 4. World Scan Rule

Never scan all entities in the world.

Bad practice:

```
world.getEntities()
```

Correct approach:

```
world.getEntitiesByClass(
    LivingEntity.class,
    player.getBoundingBox().expand(10)
)
```

Always restrict searches to nearby areas.

---

# 5. Entity Limit Rule

Set a hard limit on server entities.

Recommended limits:

| Type       | Limit |
| ---------- | ----- |
| Mob        | 1000  |
| Projectile | 1000  |
| NPC        | 500   |
| Other      | 500   |

Total recommended entity limit:

```
3000 entities
```

---

# 6. Cooldown Control Rule

Cooldown must always be controlled by the server.

Bad practice:

```
Client-side cooldown validation
```

Correct architecture:

```
Server SkillManager
```

Example data structure:

```
playerId
skillId
cooldownEndTime
```

The client should only display cooldown UI.

---

# 7. AI Pathfinding Rule

AI pathfinding is CPU intensive.

Recommended limits:

```
Max active AI mobs = 50
```

Dungeon example:

```
1 boss
10 mobs
```

Avoid scenarios like:

```
100 mobs chasing players simultaneously
```

---

# 8. Network Packet Rule

Do not send packets every tick.

Bad practice:

```
send packet every tick
```

Recommended:

```
batch packets
send every 200ms
```

This significantly reduces network overhead.

---

# 9. Skill Radius Rule

Skills must always have a limited radius.

Bad practice:

```
damage all entities in world
```

Recommended:

```
radius = 5–10 blocks
```

Only affect nearby targets.

---

# 10. Cache Rule

Avoid recalculating player stats every time.

Bad practice:

```
calculate stats every attack
```

Recommended:

```
cache player stats
update when equipment changes
```

---

# 11. Skill Engine Architecture Rule

Centralize skill logic in a dedicated engine.

Recommended architecture:

```
SkillEngine
SkillManager
CooldownManager
EffectManager
DamageCalculator
```

Avoid scattering skill logic across unrelated classes.

---

# 12. TPS Monitoring Rule

TPS (Ticks Per Second) is the primary server performance metric.

Ideal TPS:

```
20 TPS
```

Warning threshold:

```
TPS < 15
```

Monitor:

* tick time
* entity count
* packet rate
* chunk loading

---

# 13. Recommended Server Specs

For a Fabric MMORPG server:

| Players | CPU       | RAM  |
| ------- | --------- | ---- |
| 30      | 2 cores   | 4 GB |
| 60      | 4 cores   | 6 GB |
| 100     | 6–8 cores | 8 GB |

Performance heavily depends on entity count and skill implementation.

---

# Summary

To maintain server performance:

* Keep the server authoritative
* Minimize entity usage
* Limit world scans
* Reduce tick frequency
* Offload rendering to the client
* Monitor TPS continuously

Following these rules allows a Minecraft Fabric MMORPG server to scale to **80–120 players** while maintaining stable performance.
