package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseStatisticsAggregatorTest {
    private val today = LocalDate.of(2026, 6, 13)

    @Test
    fun fillsMissingDaysAndExcludesEntriesOutsidePeriod() {
        val report = ExerciseStatisticsAggregator.aggregate(
            entries = listOf(
                statistics(daysAgo = 0, ExerciseType.SQUAT, repetitions = 4),
                statistics(daysAgo = 2, ExerciseType.SQUAT, repetitions = 2),
                statistics(daysAgo = 7, ExerciseType.SQUAT, repetitions = 100),
            ),
            today = today,
            days = 7,
            selectedExercise = null,
        )

        assertEquals(7, report.dailyPoints.size)
        assertEquals(listOf(0, 0, 0, 0, 2, 0, 4), report.dailyPoints.map { it.repetitions })
        assertEquals(6, report.totalRepetitions)
        assertEquals(2, report.activeDays)
    }

    @Test
    fun exerciseFilterAffectsChartTotalsButNotPerTypeBreakdown() {
        val report = ExerciseStatisticsAggregator.aggregate(
            entries = listOf(
                statistics(daysAgo = 0, ExerciseType.SQUAT, repetitions = 4),
                statistics(daysAgo = 0, ExerciseType.PUSH_UP, repetitions = 3),
            ),
            today = today,
            days = 7,
            selectedExercise = ExerciseType.PUSH_UP,
        )

        assertEquals(3, report.totalRepetitions)
        assertEquals(3, report.dailyPoints.last().repetitions)
        assertEquals(
            setOf(ExerciseType.SQUAT, ExerciseType.PUSH_UP),
            report.totalsByExercise.map { it.exerciseType }.toSet(),
        )
    }

    private fun statistics(
        daysAgo: Long,
        exerciseType: ExerciseType,
        repetitions: Int,
    ) = DailyExerciseStatistics(
        date = today.minusDays(daysAgo),
        exerciseType = exerciseType,
        repetitions = repetitions,
    )
}
