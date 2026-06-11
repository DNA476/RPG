package com.example.rpg.game.attack

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.game.enemy.Enemy
import com.example.rpg.game.player.PlayerStats

/**
 * MVP damage calculator that leaves extension points for stats, equipment, combos, and enemy weaknesses.
 */
class FlatDamageCalculator : DamageCalculator {
    override fun calculate(
        exercise: ExerciseConfig,
        playerStats: PlayerStats,
        enemy: Enemy,
    ): Int = exercise.baseDamage
}
