package com.example.rpg.data.exercise

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.domain.exercise.SquatDetectorConfig

/**
 * In-memory exercise configuration repository.
 * Move these values to calibration, user settings, or remote-config-like local JSON when scaling.
 */
class InMemoryExerciseConfigRepository : ExerciseConfigRepository {
    override fun getAll(): List<ExerciseConfig> = ExerciseCatalog.exercises

    override fun get(type: ExerciseType): ExerciseConfig =
        ExerciseCatalog.exercises.first { it.type == type }

    override fun getSquatConfig(): SquatDetectorConfig = SquatDetectorConfig(
        standingKneeAngleDegrees = 160f,
        squatKneeAngleDegrees = 100f,
        minLandmarkVisibility = 0.55f,
    )
}
