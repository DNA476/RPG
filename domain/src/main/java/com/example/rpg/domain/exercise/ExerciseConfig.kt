package com.example.rpg.domain.exercise

/**
 * Player-facing exercise content and its base combat balance.
 */
data class ExerciseConfig(
    val type: ExerciseType,
    val displayName: String,
    val description: String,
    val baseDamage: Int,
    val difficulty: ExerciseDifficulty,
    val detectorStatus: DetectorStatus,
)

enum class ExerciseDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXTREME,
}

enum class DetectorStatus {
    READY,
    EXPERIMENTAL,
    COMING_SOON,
}
