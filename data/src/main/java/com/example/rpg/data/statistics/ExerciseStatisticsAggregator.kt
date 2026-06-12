package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate

data class DailyActivityPoint(
    val date: LocalDate,
    val repetitions: Int,
    val activeSeconds: Int,
)

data class ExerciseActivityTotal(
    val exerciseType: ExerciseType,
    val repetitions: Int,
    val activeSeconds: Int,
)

data class ExerciseStatisticsReport(
    val dailyPoints: List<DailyActivityPoint>,
    val totalsByExercise: List<ExerciseActivityTotal>,
    val selectedEntries: List<DailyExerciseStatistics>,
    val totalRepetitions: Int,
    val totalActiveSeconds: Int,
    val activeDays: Int,
)

object ExerciseStatisticsAggregator {
    fun aggregate(
        entries: List<DailyExerciseStatistics>,
        today: LocalDate,
        days: Long,
        selectedExercise: ExerciseType?,
    ): ExerciseStatisticsReport {
        require(days > 0) { "Statistics period must contain at least one day" }
        val firstDate = today.minusDays(days - 1)
        val entriesInPeriod = entries.filter { it.date in firstDate..today }
        val selectedEntries = entriesInPeriod.filter {
            selectedExercise == null || it.exerciseType == selectedExercise
        }
        val dailyPoints = (0 until days).map { dayOffset ->
            val date = firstDate.plusDays(dayOffset)
            val entriesForDate = selectedEntries.filter { it.date == date }
            DailyActivityPoint(
                date = date,
                repetitions = entriesForDate.sumOf { it.repetitions },
                activeSeconds = entriesForDate.sumOf { it.activeSeconds },
            )
        }
        val totalsByExercise = ExerciseType.entries.mapNotNull { exerciseType ->
            val exerciseEntries = entriesInPeriod.filter { it.exerciseType == exerciseType }
            val repetitions = exerciseEntries.sumOf { it.repetitions }
            val activeSeconds = exerciseEntries.sumOf { it.activeSeconds }
            if (repetitions == 0 && activeSeconds == 0) {
                null
            } else {
                ExerciseActivityTotal(
                    exerciseType = exerciseType,
                    repetitions = repetitions,
                    activeSeconds = activeSeconds,
                )
            }
        }

        return ExerciseStatisticsReport(
            dailyPoints = dailyPoints,
            totalsByExercise = totalsByExercise,
            selectedEntries = selectedEntries,
            totalRepetitions = selectedEntries.sumOf { it.repetitions },
            totalActiveSeconds = selectedEntries.sumOf { it.activeSeconds },
            activeDays = selectedEntries.map { it.date }.distinct().size,
        )
    }
}
