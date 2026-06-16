package com.example.rpg.data.enemy

import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.data.quest.WeeklyQuestCatalog
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryEnemyRepositoryTest {
    @Test
    fun returnsThreeDistinctEnemiesWithAtLeastOneFairMatchup() {
        ExerciseType.entries.forEachIndexed { index, exerciseType ->
            val repository = InMemoryEnemyRepository(Random(index))

            val choices = repository.getRandomChoices(exerciseType)

            assertEquals(3, choices.size)
            assertEquals(3, choices.distinctBy { it.id }.size)
            assertTrue(choices.any { !it.isResistantTo(exerciseType) })
        }
    }

    @Test
    fun createsBattleEnemyFromSelectedConfig() {
        val repository = InMemoryEnemyRepository(Random(7))
        val selected = repository.getRandomChoices(ExerciseType.SQUAT).first()

        val boss = repository.createBoss(selected.id)

        assertEquals(selected.name, boss.name)
        assertEquals(selected.maxHp, boss.maxHp)
        assertEquals(selected.ability, boss.ability)
    }

    @Test
    fun questChoicesContainResistantAndFairMatchups() {
        val repository = InMemoryEnemyRepository(Random(11))

        val choices = repository.getQuestChoices(
            exerciseType = ExerciseType.PULL_UP,
            requireResistantEnemy = true,
        )

        assertEquals(3, choices.size)
        assertTrue(choices.any { it.isResistantTo(ExerciseType.PULL_UP) })
        assertTrue(choices.any { !it.isResistantTo(ExerciseType.PULL_UP) })
    }

    @Test
    fun resistantQuestChoicesWorkForEveryRotatedResistantQuest() {
        val resistantQuestExercises = WeeklyQuestCatalog.allQuests
            .filter { it.requiresResistantEnemy }
            .map { it.exerciseType }
            .distinct()

        resistantQuestExercises.forEachIndexed { index, exerciseType ->
            val repository = InMemoryEnemyRepository(Random(index + 100))

            val choices = repository.getQuestChoices(
                exerciseType = exerciseType,
                requireResistantEnemy = true,
            )

            assertEquals(3, choices.size)
            assertTrue(choices.any { it.isResistantTo(exerciseType) })
            assertTrue(choices.any { !it.isResistantTo(exerciseType) })
        }
    }
}
