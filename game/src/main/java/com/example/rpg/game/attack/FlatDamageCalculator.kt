package com.example.rpg.game.attack

import com.example.rpg.game.player.PlayerStats

/**
 * MVP damage calculator. It keeps squat damage equal to 1 while leaving a clear extension point for RPG scaling.
 */
class FlatDamageCalculator : DamageCalculator {
    override fun calculate(attackType: AttackType, playerStats: PlayerStats): Int = attackType.baseDamage
}
