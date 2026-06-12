package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseCalorieEstimatorTest {
    @Test
    fun heavierProfileProducesHigherEstimate() {
        val statistics = listOf(
            DailyExerciseStatistics(
                date = LocalDate.of(2026, 6, 13),
                exerciseType = ExerciseType.SQUAT,
                repetitions = 20,
            ),
        )

        val lightEstimate = ExerciseCalorieEstimator.estimateCalories(statistics, weightKg = 50f)
        val heavyEstimate = ExerciseCalorieEstimator.estimateCalories(statistics, weightKg = 100f)

        assertTrue(heavyEstimate > lightEstimate)
    }

    @Test
    fun missingWeightUsesDefaultWeight() {
        val statistics = listOf(
            DailyExerciseStatistics(
                date = LocalDate.of(2026, 6, 13),
                exerciseType = ExerciseType.PLANK,
                activeSeconds = 60,
            ),
        )

        assertEquals(
            ExerciseCalorieEstimator.estimateCalories(
                statistics,
                ExerciseCalorieEstimator.DEFAULT_WEIGHT_KG,
            ),
            ExerciseCalorieEstimator.estimateCalories(statistics, weightKg = null),
        )
    }
}
