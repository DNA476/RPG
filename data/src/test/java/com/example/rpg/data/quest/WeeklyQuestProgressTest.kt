package com.example.rpg.data.quest

import com.example.rpg.data.inventory.InventoryCatalog
import com.example.rpg.data.inventory.ItemRarity
import com.example.rpg.domain.exercise.ExerciseType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyQuestProgressTest {
    private val challenge = WeeklyQuestCatalog.get("weekly_challenge_pullups")!!

    @Test
    fun challengeAdvancesOnlyForResistantEnemyWithoutArtifact() {
        val initial = WeeklyQuestState(weekId = "2026-W25")

        val withArtifact = WeeklyQuestProgress.recordVictory(
            quests = listOf(challenge),
            state = initial,
            result = QuestBattleResult(
                exerciseType = ExerciseType.PULL_UP,
                enemyWasResistant = true,
                artifactWasEquipped = true,
            ),
        )
        val neutralEnemy = WeeklyQuestProgress.recordVictory(
            quests = listOf(challenge),
            state = initial,
            result = QuestBattleResult(
                exerciseType = ExerciseType.PULL_UP,
                enemyWasResistant = false,
                artifactWasEquipped = false,
            ),
        )
        val valid = WeeklyQuestProgress.recordVictory(
            quests = listOf(challenge),
            state = initial,
            result = QuestBattleResult(
                exerciseType = ExerciseType.PULL_UP,
                enemyWasResistant = true,
                artifactWasEquipped = false,
            ),
        )

        assertEquals(0, withArtifact.state.progressFor(challenge))
        assertEquals(0, neutralEnemy.state.progressFor(challenge))
        assertEquals(1, valid.state.progressFor(challenge))
        assertTrue(challenge.id in valid.advancedQuestIds)
    }

    @Test
    fun completionIsReportedOnlyWhenTargetIsReached() {
        val almostComplete = WeeklyQuestState(
            weekId = "2026-W25",
            progressByQuestId = mapOf(challenge.id to 1),
        )

        val update = WeeklyQuestProgress.recordVictory(
            quests = listOf(challenge),
            state = almostComplete,
            result = QuestBattleResult(
                exerciseType = ExerciseType.PULL_UP,
                enemyWasResistant = true,
                artifactWasEquipped = false,
            ),
        )
        val repeated = WeeklyQuestProgress.recordVictory(
            quests = listOf(challenge),
            state = update.state,
            result = QuestBattleResult(
                exerciseType = ExerciseType.PULL_UP,
                enemyWasResistant = true,
                artifactWasEquipped = false,
            ),
        )

        assertEquals(setOf(challenge.id), update.completedQuestIds)
        assertTrue(repeated.completedQuestIds.isEmpty())
    }

    @Test
    fun progressResetsWhenIsoWeekChanges() {
        val previous = WeeklyQuestState(
            weekId = "2026-W24",
            progressByQuestId = mapOf(challenge.id to 2),
        )

        val current = WeeklyQuestProgress.stateForWeek(
            state = previous,
            date = LocalDate.of(2026, 6, 15),
        )

        assertEquals("2026-W25", current.weekId)
        assertTrue(current.progressByQuestId.isEmpty())
    }

    @Test
    fun catalogRewardsExistAndIncreaseWithQuestDifficulty() {
        val rewards = WeeklyQuestCatalog.quests.map { quest ->
            requireNotNull(InventoryCatalog.get(quest.rewardItemId))
        }

        assertEquals(
            listOf(ItemRarity.RARE, ItemRarity.EPIC, ItemRarity.LEGENDARY),
            rewards.map { it.rarity },
        )
        assertTrue(rewards.all { it.questExclusive })
    }
}
