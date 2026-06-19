package com.example.rpg.data.statistics

/**
 * Converts the player's lifetime estimated calorie total into progression levels.
 */
object PlayerLevelProgression {
    const val MIN_LEVEL = 0
    const val MAX_LEVEL = 12

    fun levelForCalories(totalCalories: Int): Int {
        val safeCalories = totalCalories.coerceAtLeast(0)
        return (1..MAX_LEVEL)
            .lastOrNull { level -> safeCalories >= caloriesRequiredForLevel(level) }
            ?: MIN_LEVEL
    }

    fun caloriesRequiredForLevel(level: Int): Int {
        require(level in 1..MAX_LEVEL) { "Level must be between 1 and $MAX_LEVEL" }
        return 100 * (1 shl level)
    }
}
