package com.example.rpg.game.attack

import com.example.rpg.game.player.PlayerStats

/**
 * Calculates final damage from an attack and player stats.
 */
interface DamageCalculator {
    /**
     * Returns the damage that should be applied to the current enemy.
     */
    fun calculate(attackType: AttackType, playerStats: PlayerStats): Int
}
