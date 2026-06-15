package com.example.rpg.data.quest

import java.time.LocalDate
import java.time.temporal.WeekFields

data class QuestProgressUpdate(
    val state: WeeklyQuestState,
    val advancedQuestIds: Set<String>,
    val completedQuestIds: Set<String>,
)

object WeeklyQuestProgress {
    fun weekId(date: LocalDate): String {
        val weekFields = WeekFields.ISO
        val weekYear = date.get(weekFields.weekBasedYear())
        val week = date.get(weekFields.weekOfWeekBasedYear())
        return "$weekYear-W${week.toString().padStart(2, '0')}"
    }

    fun stateForWeek(state: WeeklyQuestState?, date: LocalDate): WeeklyQuestState {
        val currentWeekId = weekId(date)
        return state?.takeIf { it.weekId == currentWeekId }
            ?: WeeklyQuestState(weekId = currentWeekId)
    }

    fun recordVictory(
        quests: List<WeeklyQuest>,
        state: WeeklyQuestState,
        result: QuestBattleResult,
    ): QuestProgressUpdate {
        val updatedProgress = state.progressByQuestId.toMutableMap()
        val advancedQuestIds = linkedSetOf<String>()
        val completedQuestIds = linkedSetOf<String>()

        quests.forEach { quest ->
            val previousProgress = state.progressFor(quest)
            if (previousProgress >= quest.requiredVictories || !quest.matches(result)) {
                return@forEach
            }
            val nextProgress = (previousProgress + 1).coerceAtMost(quest.requiredVictories)
            updatedProgress[quest.id] = nextProgress
            advancedQuestIds += quest.id
            if (nextProgress == quest.requiredVictories) {
                completedQuestIds += quest.id
            }
        }

        return QuestProgressUpdate(
            state = state.copy(progressByQuestId = updatedProgress),
            advancedQuestIds = advancedQuestIds,
            completedQuestIds = completedQuestIds,
        )
    }

    private fun WeeklyQuest.matches(result: QuestBattleResult): Boolean {
        if (exerciseType != result.exerciseType) return false
        if (requiresResistantEnemy && !result.enemyWasResistant) return false
        if (forbidsArtifacts && result.artifactWasEquipped) return false
        return true
    }
}
