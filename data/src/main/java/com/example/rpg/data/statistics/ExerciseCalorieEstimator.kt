package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import kotlin.math.roundToInt

/**
 * Provides a deliberately approximate activity estimate, not a medical measurement.
 */
object ExerciseCalorieEstimator {
    const val DEFAULT_WEIGHT_KG = 70f

    fun estimateCalories(
        statistics: Iterable<DailyExerciseStatistics>,
        weightKg: Float?,
    ): Int {
        val effectiveWeight = weightKg?.takeIf { it > 0f } ?: DEFAULT_WEIGHT_KG
        val calories = statistics.sumOf { entry ->
            val profile = profiles.getValue(entry.exerciseType)
            val repetitionSeconds = entry.repetitions * profile.secondsPerRepetition
            val totalMinutes = (repetitionSeconds + entry.activeSeconds) / 60.0
            totalMinutes * profile.met * 3.5 * effectiveWeight / 200.0
        }
        return calories.roundToInt()
    }

    private data class ExerciseEnergyProfile(
        val met: Double,
        val secondsPerRepetition: Double,
    )

    private val profiles = mapOf(
        ExerciseType.SQUAT to ExerciseEnergyProfile(met = 5.0, secondsPerRepetition = 3.0),
        ExerciseType.PUSH_UP to ExerciseEnergyProfile(met = 8.0, secondsPerRepetition = 3.0),
        ExerciseType.PULL_UP to ExerciseEnergyProfile(met = 8.0, secondsPerRepetition = 4.0),
        ExerciseType.CRUNCH to ExerciseEnergyProfile(met = 3.8, secondsPerRepetition = 2.5),
        ExerciseType.LUNGE to ExerciseEnergyProfile(met = 4.0, secondsPerRepetition = 3.0),
        ExerciseType.JUMPING_JACK to ExerciseEnergyProfile(met = 8.0, secondsPerRepetition = 1.5),
        ExerciseType.PLANK to ExerciseEnergyProfile(met = 3.5, secondsPerRepetition = 0.0),
    )
}
