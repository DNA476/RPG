package com.example.rpg.data.statistics

import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate

data class DailyExerciseStatistics(
    val date: LocalDate,
    val exerciseType: ExerciseType,
    val repetitions: Int = 0,
    val activeSeconds: Int = 0,
)
