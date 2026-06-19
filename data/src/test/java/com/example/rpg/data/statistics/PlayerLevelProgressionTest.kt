package com.example.rpg.data.statistics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlayerLevelProgressionTest {
    @Test
    fun usesExponentialLifetimeCalorieThresholds() {
        assertEquals(0, PlayerLevelProgression.levelForCalories(199))
        assertEquals(1, PlayerLevelProgression.levelForCalories(200))
        assertEquals(1, PlayerLevelProgression.levelForCalories(399))
        assertEquals(2, PlayerLevelProgression.levelForCalories(400))
        assertEquals(11, PlayerLevelProgression.levelForCalories(409_599))
        assertEquals(12, PlayerLevelProgression.levelForCalories(409_600))
    }

    @Test
    fun clampsCaloriesAndLevelAtProgressionBounds() {
        assertEquals(0, PlayerLevelProgression.levelForCalories(-1))
        assertEquals(12, PlayerLevelProgression.levelForCalories(Int.MAX_VALUE))
        assertThrows(IllegalArgumentException::class.java) {
            PlayerLevelProgression.caloriesRequiredForLevel(0)
        }
    }
}
