package com.example.rpg.game.attack

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.game.enemy.Enemy
import com.example.rpg.game.player.PlayerStats
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Applies exercise balance, enemy affinity, and temporary player debuffs.
 */
class FlatDamageCalculator : DamageCalculator {
    override fun calculate(
        exercise: ExerciseConfig,
        playerStats: PlayerStats,
        enemy: Enemy,
        playerAttackMultiplier: Float,
    ): Int {
        val rawDamage = exercise.baseDamage *
            enemy.damageMultiplierFor(exercise.type) *
            playerAttackMultiplier.coerceAtLeast(0f)
        val roundedDamage = if (rawDamage >= exercise.baseDamage) {
            ceil(rawDamage)
        } else {
            floor(rawDamage)
        }
        return roundedDamage.toInt().coerceAtLeast(1)
    }
}
