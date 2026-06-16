package com.example.rpg.data.quest

import com.example.rpg.domain.exercise.ExerciseType

enum class QuestCategory {
    REGULAR,
    DIFFICULT,
    CHALLENGE,
}

data class WeeklyQuest(
    val id: String,
    val category: QuestCategory,
    val exerciseType: ExerciseType,
    val requiredVictories: Int,
    val requiresResistantEnemy: Boolean,
    val forbidsArtifacts: Boolean,
    val rewardItemId: String,
)

data class WeeklyQuestRotation(
    val weekOffset: Int,
    val quests: List<WeeklyQuest>,
)

data class QuestBattleResult(
    val exerciseType: ExerciseType,
    val enemyWasResistant: Boolean,
    val artifactWasEquipped: Boolean,
)

data class WeeklyQuestState(
    val weekId: String,
    val progressByQuestId: Map<String, Int> = emptyMap(),
    val rewardedQuestIds: Set<String> = emptySet(),
) {
    fun progressFor(quest: WeeklyQuest): Int =
        progressByQuestId[quest.id].orZero().coerceAtMost(quest.requiredVictories)

    private fun Int?.orZero(): Int = this ?: 0
}
