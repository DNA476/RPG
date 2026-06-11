package com.example.rpg.data.exercise

import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseDifficulty
import com.example.rpg.domain.exercise.ExerciseType

/**
 * Single source of truth for exercise presentation, balance, and availability.
 */
object ExerciseCatalog {
    val exercises: List<ExerciseConfig> = listOf(
        ExerciseConfig(
            type = ExerciseType.SQUAT,
            displayName = "Приседания",
            description = "Приседайте до комфортной глубины и возвращайтесь в стойку.",
            baseDamage = 1,
            difficulty = ExerciseDifficulty.EASY,
            detectorStatus = DetectorStatus.READY,
        ),
        ExerciseConfig(
            type = ExerciseType.CRUNCH,
            displayName = "Скручивания",
            description = "Поднимайте корпус, удерживая движение под контролем.",
            baseDamage = 1,
            difficulty = ExerciseDifficulty.EASY,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
        ExerciseConfig(
            type = ExerciseType.LUNGE,
            displayName = "Выпады",
            description = "Чередуйте ноги и сохраняйте устойчивое положение корпуса.",
            baseDamage = 2,
            difficulty = ExerciseDifficulty.MEDIUM,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
        ExerciseConfig(
            type = ExerciseType.PUSH_UP,
            displayName = "Отжимания",
            description = "Опускайте и поднимайте корпус, сохраняя прямую линию тела.",
            baseDamage = 2,
            difficulty = ExerciseDifficulty.MEDIUM,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
        ExerciseConfig(
            type = ExerciseType.PULL_UP,
            displayName = "Подтягивания",
            description = "Подтягивайтесь до перекладины без резких рывков.",
            baseDamage = 3,
            difficulty = ExerciseDifficulty.HARD,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
        ExerciseConfig(
            type = ExerciseType.JUMPING_JACK,
            displayName = "Jumping Jacks",
            description = "Разводите руки и ноги в прыжке, затем возвращайтесь в исходную позицию.",
            baseDamage = 2,
            difficulty = ExerciseDifficulty.MEDIUM,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
        ExerciseConfig(
            type = ExerciseType.PLANK,
            displayName = "Планка",
            description = "Удерживайте ровное положение корпуса в статической позиции.",
            baseDamage = 2,
            difficulty = ExerciseDifficulty.MEDIUM,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        ),
    )
}
