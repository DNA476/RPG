package com.example.rpg.game.enemy

import com.example.rpg.domain.exercise.ExerciseType

/**
 * Tactical relationship between an enemy and a specific exercise.
 */
data class ExerciseAffinity(
    val exerciseType: ExerciseType,
    val damageMultiplier: Float,
) {
    companion object {
        const val WEAKNESS_MULTIPLIER = 1.5f
        const val RESISTANCE_MULTIPLIER = 0.75f

        fun weakness(type: ExerciseType) = ExerciseAffinity(type, WEAKNESS_MULTIPLIER)

        fun resistance(type: ExerciseType) = ExerciseAffinity(type, RESISTANCE_MULTIPLIER)
    }
}
