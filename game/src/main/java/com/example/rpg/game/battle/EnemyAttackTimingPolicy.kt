package com.example.rpg.game.battle

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.game.enemy.Enemy
import com.example.rpg.game.player.PlayerStats

/**
 * Supplies enemy timing independently from UI clocks.
 */
fun interface EnemyAttackTimingPolicy {
    fun intervalSeconds(
        exercise: ExerciseConfig,
        playerStats: PlayerStats,
        enemy: Enemy,
    ): Int
}

/**
 * First balance pass. This can later use exercise difficulty and a fitness profile.
 */
class FixedEnemyAttackTimingPolicy : EnemyAttackTimingPolicy {
    override fun intervalSeconds(
        exercise: ExerciseConfig,
        playerStats: PlayerStats,
        enemy: Enemy,
    ): Int = enemy.ability.attackIntervalSeconds
}
