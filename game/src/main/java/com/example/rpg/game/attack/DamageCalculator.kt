package com.example.rpg.game.attack

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.game.enemy.Enemy
import com.example.rpg.game.player.PlayerStats

/**
 * Calculates final damage from an attack and player stats.
 */
interface DamageCalculator {
    /**
     * Returns the damage that should be applied to the current enemy.
     */
    fun calculate(
        exercise: ExerciseConfig,
        playerStats: PlayerStats,
        enemy: Enemy,
        playerAttackMultiplier: Float,
    ): Int
}
