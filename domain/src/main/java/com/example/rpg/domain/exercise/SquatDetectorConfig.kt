package com.example.rpg.domain.exercise

/**
 * Tunable thresholds for squat recognition.
 * Lower knee angles mean a deeper squat; higher angles mean a standing position.
 */
data class SquatDetectorConfig(
    val standingKneeAngleDegrees: Float = 160f,
    val squatKneeAngleDegrees: Float = 100f,
    val minLandmarkVisibility: Float = 0.55f,
    val eventBufferCapacity: Int = 16,
)
