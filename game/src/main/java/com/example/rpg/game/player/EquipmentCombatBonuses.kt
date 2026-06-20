package com.example.rpg.game.player

/**
 * Bounded combat effects supplied by the currently equipped inventory loadout.
 */
data class EquipmentCombatBonuses(
    val attackPowerPercent: Int = 0,
    val debuffDurationReductionPercent: Int = 0,
    val enemyAbilityDelaySeconds: Int = 0,
    val resistantMatchupDamagePercent: Int = 0,
    val weaknessMatchupDamagePercent: Int = 0,
    val openingAttackDamagePercent: Int = 0,
    val streakDamagePercent: Int = 0,
) {
    fun damageMultiplier(
        enemyAffinityMultiplier: Float,
        repetitionCount: Int,
    ): Float {
        var percent = attackPowerPercent.coerceIn(0, MAX_DAMAGE_BONUS_PERCENT)
        if (enemyAffinityMultiplier < 1f) {
            percent += resistantMatchupDamagePercent.coerceIn(0, MAX_DAMAGE_BONUS_PERCENT)
        } else if (enemyAffinityMultiplier > 1f) {
            percent += weaknessMatchupDamagePercent.coerceIn(0, MAX_DAMAGE_BONUS_PERCENT)
        }
        if (repetitionCount == 1) {
            percent += openingAttackDamagePercent.coerceIn(0, MAX_DAMAGE_BONUS_PERCENT)
        }
        if (repetitionCount > 0 && repetitionCount % STREAK_LENGTH == 0) {
            percent += streakDamagePercent.coerceIn(0, MAX_DAMAGE_BONUS_PERCENT)
        }
        return 1f + percent.coerceAtMost(MAX_TOTAL_DAMAGE_BONUS_PERCENT) / 100f
    }

    fun adjustedEnemyAbilityInterval(baseSeconds: Int): Int =
        (baseSeconds + enemyAbilityDelaySeconds.coerceIn(0, MAX_ABILITY_DELAY_SECONDS))
            .coerceAtLeast(1)

    fun adjustedDebuffDuration(baseSeconds: Int): Int {
        if (baseSeconds <= 0) return 0
        val reduction = debuffDurationReductionPercent.coerceIn(0, MAX_DEBUFF_REDUCTION_PERCENT)
        return ((baseSeconds * (100 - reduction)) + 99) / 100
    }

    companion object {
        const val STREAK_LENGTH = 3
        private const val MAX_DAMAGE_BONUS_PERCENT = 50
        private const val MAX_TOTAL_DAMAGE_BONUS_PERCENT = 100
        private const val MAX_ABILITY_DELAY_SECONDS = 10
        private const val MAX_DEBUFF_REDUCTION_PERCENT = 50
    }
}
