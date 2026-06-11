package com.example.rpg.game.enemy

/**
 * Enemy action that temporarily reduces the player's outgoing damage.
 */
data class EnemyAbility(
    val name: String,
    val description: String,
    val attackIntervalSeconds: Int = 15,
    val attackReductionPercent: Int = 25,
    val debuffDurationSeconds: Int = 10,
) {
    val attackMultiplier: Float
        get() = 1f - attackReductionPercent / 100f
}
