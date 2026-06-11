package com.example.rpg.game.battle

import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseDifficulty
import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.game.enemy.Boss
import org.junit.Assert.assertEquals
import org.junit.Test

class BattleSessionTest {
    @Test
    fun selectedExerciseDamageDefeatsBossAndTracksTotals() {
        val exercise = ExerciseConfig(
            type = ExerciseType.PUSH_UP,
            displayName = "Отжимания",
            description = "",
            baseDamage = 2,
            difficulty = ExerciseDifficulty.MEDIUM,
            detectorStatus = DetectorStatus.EXPERIMENTAL,
        )
        val session = BattleSession(
            boss = Boss("boss", "Boss", 10, 10, ""),
            exercise = exercise,
        )
        session.startBattle()

        repeat(5) { index ->
            session.handleExerciseEvent(
                ExerciseEvent.RepetitionCompleted(ExerciseType.PUSH_UP, index + 1),
            )
        }

        assertEquals(GameState.VICTORY, session.state.value.gameState)
        assertEquals(5, session.state.value.completedRepetitions)
        assertEquals(10, session.state.value.totalDamage)
        assertEquals(0, session.state.value.boss.currentHp)
    }
}
