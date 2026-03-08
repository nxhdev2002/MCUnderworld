package com.kiemhiep.cultivation;

import com.kiemhiep.api.event.CultivationBreakthroughEvent;
import com.kiemhiep.api.event.CultivationSubLevelUpEvent;
import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.model.CultivationRealm;
import com.kiemhiep.api.repository.CultivationRepository;
import com.kiemhiep.api.service.CultivationService;

import java.time.Instant;
import java.util.Optional;

/**
 * Implementation: 10 realms, 9 sub-levels per realm (level 1–90).
 * addExp levels sub-levels and fires events; breakthrough advances to next realm when at sub 8.
 */
public class CultivationServiceImpl implements CultivationService {

    private final CultivationRepository repository;
    private final EventDispatcher eventDispatcher;

    public CultivationServiceImpl(CultivationRepository repository, EventDispatcher eventDispatcher) {
        this.repository = repository;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Optional<Cultivation> get(long playerId) {
        return repository.getByPlayerId(playerId);
    }

    @Override
    public Cultivation getOrCreate(long playerId) {
        return repository.getByPlayerId(playerId)
            .orElseGet(() -> {
                Cultivation created = new Cultivation(
                    0,
                    playerId,
                    1,
                    0L,
                    Instant.now(),
                    Instant.now()
                );
                return repository.save(created);
            });
    }

    @Override
    public void addExp(long playerId, long amount) {
        Cultivation c = getOrCreate(playerId);
        int level = clampLevel(c.level());
        long exp = c.exp() + amount;

        while (level < CultivationRealm.MAX_LEVEL) {
            int realm = getRealm(level);
            long required = getExpRequired(realm);
            if (exp < required) {
                break;
            }
            exp -= required;
            int oldLevel = level;
            level++;
            eventDispatcher.fire(new CultivationSubLevelUpEvent(playerId, oldLevel, level));
            if (getSubLevel(level) == 0 && getSubLevel(oldLevel) == CultivationRealm.SUB_LEVELS_PER_REALM - 1) {
                eventDispatcher.fire(new CultivationBreakthroughEvent(playerId, getRealm(oldLevel), getRealm(level)));
            }
        }

        if (level == CultivationRealm.MAX_LEVEL && exp > 0) {
            exp = 0;
        }
        Cultivation updated = new Cultivation(c.id(), c.playerId(), level, exp, c.createdAt(), Instant.now());
        repository.save(updated);
    }

    @Override
    public void setSubLevel(long playerId, int realm, int subLevel) {
        if (realm < 1 || realm > CultivationRealm.MAX_REALM) {
            throw new IllegalArgumentException("realm must be 1.." + CultivationRealm.MAX_REALM);
        }
        if (subLevel < 0 || subLevel >= CultivationRealm.SUB_LEVELS_PER_REALM) {
            throw new IllegalArgumentException("subLevel must be 0.." + (CultivationRealm.SUB_LEVELS_PER_REALM - 1));
        }
        int level = CultivationRealm.toLevel(realm, subLevel);
        Cultivation c = getOrCreate(playerId);
        Cultivation updated = new Cultivation(c.id(), c.playerId(), level, 0L, c.createdAt(), Instant.now());
        repository.save(updated);
    }

    @Override
    public boolean breakthrough(long playerId) {
        Cultivation c = getOrCreate(playerId);
        int level = clampLevel(c.level());
        int subLevel = getSubLevel(level);
        int realm = getRealm(level);
        if (subLevel != CultivationRealm.SUB_LEVELS_PER_REALM - 1 || realm >= CultivationRealm.MAX_REALM) {
            return false;
        }
        int newLevel = level + 1;
        Cultivation updated = new Cultivation(c.id(), c.playerId(), newLevel, 0L, c.createdAt(), Instant.now());
        repository.save(updated);
        eventDispatcher.fire(new CultivationBreakthroughEvent(playerId, realm, getRealm(newLevel)));
        return true;
    }

    @Override
    public long getExpRequired(int realmLevel) {
        if (realmLevel < 1 || realmLevel > CultivationRealm.MAX_REALM) {
            return Long.MAX_VALUE;
        }
        return 100L * realmLevel;
    }

    @Override
    public int getRealm(int level) {
        return CultivationRealm.fromLevel(clampLevel(level)).realm();
    }

    @Override
    public int getSubLevel(int level) {
        return CultivationRealm.fromLevel(clampLevel(level)).subLevel();
    }

    private static int clampLevel(int level) {
        if (level < 1) return 1;
        return Math.min(level, CultivationRealm.MAX_LEVEL);
    }
}
