package com.example.rpg.data.exercise

import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.domain.exercise.SquatDetectorConfig

/**
 * Source of exercise detector thresholds and tuning parameters.
 */
interface ExerciseConfigRepository {
    fun getAll(): List<ExerciseConfig>

    fun get(type: ExerciseType): ExerciseConfig

    /**
     * Returns squat detector thresholds for the MVP exercise.
     */
    fun getSquatConfig(): SquatDetectorConfig
}
