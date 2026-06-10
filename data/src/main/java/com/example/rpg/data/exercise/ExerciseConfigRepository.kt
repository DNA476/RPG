package com.example.rpg.data.exercise

import com.example.rpg.domain.exercise.SquatDetectorConfig

/**
 * Source of exercise detector thresholds and tuning parameters.
 */
interface ExerciseConfigRepository {
    /**
     * Returns squat detector thresholds for the MVP exercise.
     */
    fun getSquatConfig(): SquatDetectorConfig
}
