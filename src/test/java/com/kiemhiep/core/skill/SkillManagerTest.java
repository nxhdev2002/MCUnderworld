package com.kiemhiep.core.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.PlatformProvider;
import com.kiemhiep.api.repository.SkillDefinitionRepository;
import com.kiemhiep.api.skill.ISkill;
import com.kiemhiep.api.skill.ManaProvider;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.core.module.StubPlatformProvider;
import com.kiemhiep.core.skill.impl.FireballSkill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillManagerTest {

    private static final UUID CASTER_ID = UUID.randomUUID();
    private static final long SERVER_TICK = 1000L;

    @Mock
    private SkillDefinitionRepository definitionRepository;
    @Mock
    private ManaProvider manaProvider;

    private CooldownManager cooldownManager;
    private CastStateManager castStateManager;
    private EffectManager effectManager;
    private PlatformProvider platformProvider;
    private SkillManager managerNoMana;
    private SkillManager managerWithMana;

    private static SkillDefinition def(long id, String skillId, String behaviorId, int manaCost, int castTimeTicks) {
        return new SkillDefinition(
            id, skillId, behaviorId, "item:" + skillId, "name", manaCost,
            40, 8.0, false, false, "magic", castTimeTicks, false, false,
            null, 0, Instant.now(), Instant.now());
    }

    @BeforeEach
    void setUp() {
        cooldownManager = new CooldownManager();
        castStateManager = new CastStateManager();
        effectManager = new EffectManager();
        platformProvider = new StubPlatformProvider();
        managerNoMana = new SkillManager(
            definitionRepository, cooldownManager, castStateManager, effectManager,
            platformProvider, Optional.empty());
        managerWithMana = new SkillManager(
            definitionRepository, cooldownManager, castStateManager, effectManager,
            platformProvider, Optional.of(manaProvider));
        SkillRegistry.clear();
        SkillRegistry.register("FIREBALL", FireballSkill.INSTANCE);
    }

    @AfterEach
    void tearDown() {
        SkillRegistry.clear();
    }

    @Test
    void useSkill_returnsInvalidSkill_whenDefinitionNull() {
        assertEquals(SkillManager.UseResult.INVALID_SKILL, managerNoMana.useSkill(CASTER_ID, null, SERVER_TICK));
    }

    @Test
    void useSkill_returnsInvalidSkill_whenBehaviorIdNotInRegistry() {
        SkillDefinition def = def(1L, "unknown", "UNKNOWN_BEHAVIOR", 0, 0);
        assertEquals(SkillManager.UseResult.INVALID_SKILL, managerNoMana.useSkill(CASTER_ID, def, SERVER_TICK));
    }

    @Test
    void useSkill_returnsOnCooldown_whenOnCooldown() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 0, 0);
        long endMillis = System.currentTimeMillis() + 60_000;
        cooldownManager.setCooldown(CASTER_ID, def.skillId(), endMillis);
        assertEquals(SkillManager.UseResult.ON_COOLDOWN, managerNoMana.useSkill(CASTER_ID, def, SERVER_TICK));
    }

    @Test
    void useSkill_returnsAlreadyCasting_whenCasting() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 0, 20);
        castStateManager.startCast(CASTER_ID, def.skillId(), SERVER_TICK + 20, def);
        assertEquals(SkillManager.UseResult.ALREADY_CASTING, managerNoMana.useSkill(CASTER_ID, def, SERVER_TICK));
    }

    @Test
    void useSkill_returnsCastStarted_whenCastTimeTicksPositive() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 0, 20);
        assertEquals(SkillManager.UseResult.CAST_STARTED, managerNoMana.useSkill(CASTER_ID, def, SERVER_TICK));
        assertTrue(castStateManager.isCasting(CASTER_ID));
    }

    @Test
    void useSkill_returnsSuccess_andSetsCooldown_whenInstantCast() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 0, 0);
        assertEquals(SkillManager.UseResult.SUCCESS, managerNoMana.useSkill(CASTER_ID, def, SERVER_TICK));
        assertTrue(cooldownManager.getCooldownEndTimeMillis(CASTER_ID, def.skillId()) > System.currentTimeMillis());
    }

    @Test
    void useSkill_returnsInsufficientMana_whenManaProviderPresentAndCurrentManaTooLow() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 50, 0);
        when(manaProvider.getCurrentMana(CASTER_ID)).thenReturn(30);
        assertEquals(SkillManager.UseResult.INSUFFICIENT_MANA, managerWithMana.useSkill(CASTER_ID, def, SERVER_TICK));
        verify(manaProvider, never()).consumeMana(any(), anyInt());
    }

    @Test
    void useSkill_returnsInsufficientMana_whenConsumeManaReturnsFalse() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 50, 0);
        when(manaProvider.getCurrentMana(CASTER_ID)).thenReturn(50);
        when(manaProvider.consumeMana(CASTER_ID, 50)).thenReturn(false);
        assertEquals(SkillManager.UseResult.INSUFFICIENT_MANA, managerWithMana.useSkill(CASTER_ID, def, SERVER_TICK));
    }

    @Test
    void useSkill_returnsSuccess_whenManaProviderPresentAndManaSufficient() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 10, 0);
        when(manaProvider.getCurrentMana(CASTER_ID)).thenReturn(100);
        when(manaProvider.consumeMana(CASTER_ID, 10)).thenReturn(true);
        assertEquals(SkillManager.UseResult.SUCCESS, managerWithMana.useSkill(CASTER_ID, def, SERVER_TICK));
        verify(manaProvider).consumeMana(CASTER_ID, 10);
    }

    @Test
    void useSkill_ignoresMana_whenManaCostZero() {
        SkillDefinition def = def(1L, "fb", "FIREBALL", 0, 0);
        assertEquals(SkillManager.UseResult.SUCCESS, managerWithMana.useSkill(CASTER_ID, def, SERVER_TICK));
        verify(manaProvider, never()).getCurrentMana(any());
        verify(manaProvider, never()).consumeMana(any(), anyInt());
    }
}
