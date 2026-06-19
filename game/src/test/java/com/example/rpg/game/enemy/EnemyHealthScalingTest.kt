package com.example.rpg.game.enemy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnemyHealthScalingTest {
    @Test
    fun keepsBaseHealthAtFirstLevelAndTriplesItAtMaximumLevel() {
        assertEquals(20, EnemyHealthScaling.maxHpForLevel(baseMaxHp = 20, level = 1))
        assertEquals(60, EnemyHealthScaling.maxHpForLevel(baseMaxHp = 20, level = 12))
    }

    @Test
    fun cubicCurveGrowsMoreAtLateLevelsThanEarlyLevels() {
        val healthByLevel = (1..12).map { level ->
            EnemyHealthScaling.maxHpForLevel(baseMaxHp = 100, level = level)
        }

        val firstIncrease = healthByLevel[1] - healthByLevel[0]
        val lastIncrease = healthByLevel[11] - healthByLevel[10]

        assertTrue(firstIncrease <= 1)
        assertTrue(lastIncrease >= 40)
        assertTrue(healthByLevel.zipWithNext().all { (first, second) -> second >= first })
    }

    @Test
    fun levelsOutsideRangeUseNearestSupportedLevel() {
        assertEquals(10, EnemyHealthScaling.maxHpForLevel(baseMaxHp = 10, level = 0))
        assertEquals(30, EnemyHealthScaling.maxHpForLevel(baseMaxHp = 10, level = 99))
    }
}
