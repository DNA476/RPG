package com.example.rpg.domain.exercise

/**
 * Latest detector feedback suitable for UI and local diagnostics.
 */
data class ExerciseDetectionResult(
    val repetitionCompleted: Boolean = false,
    val stateLabel: String = "Ожидание",
    val confidence: Float = 0f,
    val debugInfo: String? = null,
)
