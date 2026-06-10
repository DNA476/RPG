package com.example.rpg.game.battle

import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.game.attack.DamageCalculator
import com.example.rpg.game.attack.DefaultExerciseAttackMapper
import com.example.rpg.game.attack.ExerciseAttackMapper
import com.example.rpg.game.attack.FlatDamageCalculator
import com.example.rpg.game.enemy.Boss
import com.example.rpg.game.player.PlayerStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Game engine for a single battle.
 * It receives exercise events, maps them to attacks, applies damage, and emits battle state.
 */
class BattleSession(
    private var boss: Boss,
    private val playerStats: PlayerStats = PlayerStats(),
    private val exerciseAttackMapper: ExerciseAttackMapper = DefaultExerciseAttackMapper(),
    private val damageCalculator: DamageCalculator = FlatDamageCalculator(),
) {
    private var completedRepetitions = 0
    private val mutableState = MutableStateFlow(createSnapshot(GameState.IDLE, null, null))

    val state: StateFlow<BattleSnapshot> = mutableState.asStateFlow()

    /**
     * Moves the session into active battle state.
     */
    fun startBattle() {
        if (mutableState.value.gameState == GameState.VICTORY) return
        mutableState.value = createSnapshot(GameState.BATTLE, null, null)
    }

    /**
     * Moves the session into tracking state while pose detection warms up.
     */
    fun startTracking() {
        if (mutableState.value.gameState == GameState.IDLE) {
            mutableState.value = createSnapshot(GameState.TRACKING, null, null)
        }
    }

    /**
     * Handles exercise detector output and applies battle effects for completed repetitions.
     */
    fun handleExerciseEvent(event: ExerciseEvent) {
        if (mutableState.value.gameState == GameState.VICTORY || mutableState.value.gameState == GameState.DEFEAT) return
        if (event !is ExerciseEvent.RepetitionCompleted) return

        val attackType = exerciseAttackMapper.map(event.exerciseType)
        val damage = damageCalculator.calculate(attackType, playerStats)
        boss.receiveDamage(damage)
        completedRepetitions = event.repetitionCount

        val nextState = if (boss.isDefeated()) GameState.VICTORY else GameState.BATTLE
        mutableState.value = createSnapshot(nextState, damage, attackType)
    }

    /**
     * Restarts the session with a fresh boss instance.
     */
    fun reset(newBoss: Boss) {
        boss = newBoss
        completedRepetitions = 0
        mutableState.value = createSnapshot(GameState.IDLE, null, null)
    }

    private fun createSnapshot(
        gameState: GameState,
        lastDamage: Int?,
        lastAttackType: com.example.rpg.game.attack.AttackType?,
    ): BattleSnapshot = BattleSnapshot(
        gameState = gameState,
        boss = boss,
        playerStats = playerStats,
        completedRepetitions = completedRepetitions,
        lastDamage = lastDamage,
        lastAttackType = lastAttackType,
    )
}
