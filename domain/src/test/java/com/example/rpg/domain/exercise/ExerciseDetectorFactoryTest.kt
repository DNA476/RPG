package com.example.rpg.domain.exercise

import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseDetectorFactoryTest {
    @Test
    fun createsDetectorForEveryExerciseType() {
        val factory = ExerciseDetectorFactory()

        ExerciseType.entries.forEach { type ->
            assertEquals(type, factory.create(type).exerciseType)
        }
    }
}
