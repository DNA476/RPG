package com.example.rpg.data.quest

import com.example.rpg.domain.exercise.ExerciseType
import java.lang.Math.floorMod
import java.time.LocalDate
import java.time.temporal.WeekFields

object WeeklyQuestCatalog {
    private const val ROTATION_START_WEEK_ID = "2026-W25"

    val rotations: List<WeeklyQuestRotation> = listOf(
        WeeklyQuestRotation(
            weekOffset = 0,
            quests = listOf(
                WeeklyQuest(
                    id = "weekly_regular_squats",
                    category = QuestCategory.REGULAR,
                    exerciseType = ExerciseType.SQUAT,
                    requiredVictories = 2,
                    requiresResistantEnemy = false,
                    forbidsArtifacts = false,
                    rewardItemId = "guardian_wraps",
                ),
                WeeklyQuest(
                    id = "weekly_difficult_pushups",
                    category = QuestCategory.DIFFICULT,
                    exerciseType = ExerciseType.PUSH_UP,
                    requiredVictories = 2,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = false,
                    rewardItemId = "resistance_breaker",
                ),
                WeeklyQuest(
                    id = "weekly_challenge_pullups",
                    category = QuestCategory.CHALLENGE,
                    exerciseType = ExerciseType.PULL_UP,
                    requiredVictories = 2,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = true,
                    rewardItemId = "crown_of_trials",
                ),
            ),
        ),
        WeeklyQuestRotation(
            weekOffset = 1,
            quests = listOf(
                WeeklyQuest(
                    id = "weekly_regular_lunges",
                    category = QuestCategory.REGULAR,
                    exerciseType = ExerciseType.LUNGE,
                    requiredVictories = 2,
                    requiresResistantEnemy = false,
                    forbidsArtifacts = false,
                    rewardItemId = "trailblazer_greaves",
                ),
                WeeklyQuest(
                    id = "weekly_difficult_plank",
                    category = QuestCategory.DIFFICULT,
                    exerciseType = ExerciseType.PLANK,
                    requiredVictories = 2,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = false,
                    rewardItemId = "aegis_compass",
                ),
                WeeklyQuest(
                    id = "weekly_challenge_squats",
                    category = QuestCategory.CHALLENGE,
                    exerciseType = ExerciseType.SQUAT,
                    requiredVictories = 3,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = true,
                    rewardItemId = "titan_cuirass",
                ),
            ),
        ),
        WeeklyQuestRotation(
            weekOffset = 2,
            quests = listOf(
                WeeklyQuest(
                    id = "weekly_regular_crunches",
                    category = QuestCategory.REGULAR,
                    exerciseType = ExerciseType.CRUNCH,
                    requiredVictories = 2,
                    requiresResistantEnemy = false,
                    forbidsArtifacts = false,
                    rewardItemId = "focus_charm",
                ),
                WeeklyQuest(
                    id = "weekly_difficult_pullups",
                    category = QuestCategory.DIFFICULT,
                    exerciseType = ExerciseType.PULL_UP,
                    requiredVictories = 2,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = false,
                    rewardItemId = "skybreaker_staff",
                ),
                WeeklyQuest(
                    id = "weekly_challenge_pushups",
                    category = QuestCategory.CHALLENGE,
                    exerciseType = ExerciseType.PUSH_UP,
                    requiredVictories = 3,
                    requiresResistantEnemy = true,
                    forbidsArtifacts = true,
                    rewardItemId = "sunforged_crown",
                ),
            ),
        ),
    )

    val allQuests: List<WeeklyQuest> = rotations.flatMap(WeeklyQuestRotation::quests)

    val quests: List<WeeklyQuest> = rotations.first().quests

    fun questsForDate(date: LocalDate): List<WeeklyQuest> =
        questsForWeek(WeeklyQuestProgress.weekId(date))

    fun questsForWeek(weekId: String): List<WeeklyQuest> {
        val weeksFromStart = weeksBetween(
            startWeekId = ROTATION_START_WEEK_ID,
            targetWeekId = weekId,
        )
        val rotationIndex = floorMod(weeksFromStart, rotations.size)
        return rotations.first { it.weekOffset == rotationIndex }.quests
    }

    fun get(id: String): WeeklyQuest? = allQuests.firstOrNull { it.id == id }

    private fun weeksBetween(startWeekId: String, targetWeekId: String): Int {
        val start = mondayOfIsoWeek(startWeekId)
        val target = mondayOfIsoWeek(targetWeekId)
        return ((target.toEpochDay() - start.toEpochDay()) / 7).toInt()
    }

    private fun mondayOfIsoWeek(weekId: String): LocalDate {
        val parts = weekId.split("-W")
        require(parts.size == 2) { "Invalid ISO week id: $weekId" }
        val year = parts[0].toInt()
        val week = parts[1].toInt()
        val weekFields = WeekFields.ISO
        return LocalDate.of(year, 1, 4)
            .with(weekFields.weekBasedYear(), year.toLong())
            .with(weekFields.weekOfWeekBasedYear(), week.toLong())
            .with(weekFields.dayOfWeek(), 1)
    }
}
