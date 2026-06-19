package com.example.rpg.game.enemy

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Keeps early-level enemy growth shallow while reaching triple base HP at level 12.
 */
object EnemyHealthScaling {
    const val BASE_LEVEL = 1
    const val MAX_LEVEL = 12

    fun multiplierForLevel(level: Int): Double {
        val boundedLevel = level.coerceIn(BASE_LEVEL, MAX_LEVEL)
        val progress = (boundedLevel - BASE_LEVEL).toDouble() / (MAX_LEVEL - BASE_LEVEL)
        return 1.0 + 2.0 * progress.pow(3)
    }

    fun maxHpForLevel(baseMaxHp: Int, level: Int): Int {
        require(baseMaxHp > 0) { "Base enemy HP must be positive" }
        return (baseMaxHp * multiplierForLevel(level)).roundToInt()
    }
}
