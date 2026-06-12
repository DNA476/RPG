package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate

interface ExerciseStatisticsRepository {
    fun recordRepetition(
        exerciseType: ExerciseType,
        date: LocalDate,
    )

    fun recordActiveSeconds(
        exerciseType: ExerciseType,
        seconds: Int,
        date: LocalDate,
    )

    fun getDailyStatistics(): List<DailyExerciseStatistics>
}
