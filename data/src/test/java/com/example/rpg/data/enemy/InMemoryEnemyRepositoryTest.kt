package com.example.rpg.data.enemy

import com.example.rpg.domain.exercise.ExerciseType
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
}
