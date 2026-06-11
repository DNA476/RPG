package com.example.rpg.game.battle

import com.example.rpg.game.attack.AttackType
import com.example.rpg.game.enemy.Boss
import com.example.rpg.game.player.PlayerStats

/**
 * Immutable snapshot of the battle session exposed through StateFlow.
 */
data class BattleSnapshot(
    val gameState: GameState,
    val boss: Boss,
    val playerStats: PlayerStats,
    val completedRepetitions: Int,
    val totalDamage: Int,
    val lastDamage: Int?,
    val lastAttackType: AttackType?,
)
