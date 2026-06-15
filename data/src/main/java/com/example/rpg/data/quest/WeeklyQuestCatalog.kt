package com.example.rpg.data.quest

import com.example.rpg.domain.exercise.ExerciseType

object WeeklyQuestCatalog {
    val quests: List<WeeklyQuest> = listOf(
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
    )

    fun get(id: String): WeeklyQuest? = quests.firstOrNull { it.id == id }
}
