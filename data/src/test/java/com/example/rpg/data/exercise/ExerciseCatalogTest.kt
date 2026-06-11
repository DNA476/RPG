package com.example.rpg.data.exercise

import com.example.rpg.domain.exercise.ExerciseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseCatalogTest {
    @Test
    fun containsEveryExerciseExactlyOnce() {
        val types = ExerciseCatalog.exercises.map { it.type }

        assertEquals(ExerciseType.entries.size, types.size)
        assertEquals(ExerciseType.entries.toSet(), types.toSet())
        assertTrue(ExerciseCatalog.exercises.all { it.baseDamage > 0 })
    }
}
