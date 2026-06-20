package com.example.rpg.game.player

import org.junit.Assert.assertEquals
import org.junit.Test

class EquipmentCombatBonusesTest {
    @Test
    fun damageBonusesApplyOnlyToTheirMatchingAttackConditions() {
        val bonuses = EquipmentCombatBonuses(
            attackPowerPercent = 5,
            resistantMatchupDamagePercent = 10,
            weaknessMatchupDamagePercent = 12,
            openingAttackDamagePercent = 7,
            streakDamagePercent = 9,
        )

        assertEquals(1.22f, bonuses.damageMultiplier(0.75f, 1), 0.0001f)
        assertEquals(1.17f, bonuses.damageMultiplier(1.5f, 2), 0.0001f)
        assertEquals(1.24f, bonuses.damageMultiplier(0.75f, 3), 0.0001f)
        assertEquals(1.05f, bonuses.damageMultiplier(1f, 2), 0.0001f)
    }

    @Test
    fun timingBonusesAreBoundedAndRoundDebuffDurationUp() {
        val bonuses = EquipmentCombatBonuses(
            debuffDurationReductionPercent = 15,
            enemyAbilityDelaySeconds = 3,
        )

        assertEquals(18, bonuses.adjustedEnemyAbilityInterval(15))
        assertEquals(9, bonuses.adjustedDebuffDuration(10))
    }

    @Test
    fun extremeValuesCannotRemoveEnemyAbilitiesOrMoreThanDoubleDamage() {
        val bonuses = EquipmentCombatBonuses(
            attackPowerPercent = 999,
            weaknessMatchupDamagePercent = 999,
            openingAttackDamagePercent = 999,
            debuffDurationReductionPercent = 999,
            enemyAbilityDelaySeconds = 999,
        )

        assertEquals(2f, bonuses.damageMultiplier(1.5f, 1), 0.0001f)
        assertEquals(11, bonuses.adjustedEnemyAbilityInterval(1))
        assertEquals(5, bonuses.adjustedDebuffDuration(10))
    }
}
