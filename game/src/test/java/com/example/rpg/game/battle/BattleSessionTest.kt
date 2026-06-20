package com.example.rpg.game.battle

import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseDifficulty
import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.game.enemy.Boss
import com.example.rpg.game.enemy.EnemyAbility
import com.example.rpg.game.enemy.ExerciseAffinity
import com.example.rpg.game.player.EquipmentCombatBonuses
import org.junit.Assert.assertEquals
import org.junit.Test

class BattleSessionTest {
    private val pushUp = ExerciseConfig(
        type = ExerciseType.PUSH_UP,
        displayName = "Отжимания",
        description = "",
        baseDamage = 2,
        difficulty = ExerciseDifficulty.MEDIUM,
        detectorStatus = DetectorStatus.EXPERIMENTAL,
    )

    @Test
    fun weaknessIncreasesConfiguredDamage() {
        val session = BattleSession(
            boss = boss(weakness = ExerciseType.PUSH_UP),
            exercise = pushUp,
        )
        session.startBattle()

        session.handleExerciseEvent(repetition(1))

        assertEquals(3, session.state.value.lastDamage)
    }

    @Test
    fun resistanceAndEnemyDebuffReduceDamageWithoutImmunity() {
        val session = BattleSession(
            boss = boss(resistance = ExerciseType.PUSH_UP),
            exercise = pushUp,
        )
        session.startBattle()
        session.setPlayerAttackMultiplier(0.75f)

        session.handleExerciseEvent(repetition(1))

        assertEquals(1, session.state.value.lastDamage)
        assertEquals(0.75f, session.state.value.playerAttackMultiplier)
    }

    @Test
    fun repetitionsEventuallyDefeatEnemyAndTrackTotals() {
        val session = BattleSession(
            boss = boss(maxHp = 10),
            exercise = pushUp,
        )
        session.startBattle()

        repeat(5) { index -> session.handleExerciseEvent(repetition(index + 1)) }

        assertEquals(GameState.VICTORY, session.state.value.gameState)
        assertEquals(5, session.state.value.completedRepetitions)
        assertEquals(10, session.state.value.totalDamage)
        assertEquals(0, session.state.value.boss.currentHp)
    }

    @Test
    fun equipmentDamageBonusesAreAppliedByTheBattleEngine() {
        val session = BattleSession(
            boss = boss(maxHp = 20),
            exercise = pushUp,
            equipmentBonuses = EquipmentCombatBonuses(
                openingAttackDamagePercent = 50,
            ),
        )
        session.startBattle()

        session.handleExerciseEvent(repetition(1))
        session.handleExerciseEvent(repetition(2))

        assertEquals(3, session.state.value.totalDamage - session.state.value.lastDamage!!)
        assertEquals(2, session.state.value.lastDamage)
        assertEquals(5, session.state.value.totalDamage)
    }

    private fun repetition(count: Int) =
        ExerciseEvent.RepetitionCompleted(ExerciseType.PUSH_UP, count)

    private fun boss(
        maxHp: Int = 20,
        weakness: ExerciseType = ExerciseType.LUNGE,
        resistance: ExerciseType = ExerciseType.SQUAT,
    ) = Boss(
        id = "boss",
        name = "Boss",
        description = "",
        maxHp = maxHp,
        currentHp = maxHp,
        imageResource = "",
        weakness = ExerciseAffinity.weakness(weakness),
        resistance = ExerciseAffinity.resistance(resistance),
        ability = EnemyAbility("Roar", ""),
    )
}
