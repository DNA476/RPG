package com.example.rpg.game.battle

import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.game.attack.DamageCalculator
import com.example.rpg.game.attack.DefaultExerciseAttackMapper
import com.example.rpg.game.attack.ExerciseAttackMapper
import com.example.rpg.game.attack.FlatDamageCalculator
import com.example.rpg.game.enemy.Boss
import com.example.rpg.game.player.PlayerStats
import com.example.rpg.game.player.EquipmentCombatBonuses
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Game engine for a single battle.
 * It receives exercise events, maps them to attacks, applies damage, and emits battle state.
 */
class BattleSession(
    private var boss: Boss,
    private val exercise: ExerciseConfig,
    private val playerStats: PlayerStats = PlayerStats(),
    private val equipmentBonuses: EquipmentCombatBonuses = EquipmentCombatBonuses(),
    private val exerciseAttackMapper: ExerciseAttackMapper = DefaultExerciseAttackMapper(),
    private val damageCalculator: DamageCalculator = FlatDamageCalculator(),
) {
    private var completedRepetitions = 0
    private var totalDamage = 0
    private var playerAttackMultiplier = 1f
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
        if (event.exerciseType != exercise.type) return

        val attackType = exerciseAttackMapper.map(event.exerciseType)
        val equipmentMultiplier = equipmentBonuses.damageMultiplier(
            enemyAffinityMultiplier = boss.damageMultiplierFor(exercise.type),
            repetitionCount = event.repetitionCount,
        )
        val damage = damageCalculator.calculate(
            exercise = exercise,
            playerStats = playerStats,
            enemy = boss,
            playerAttackMultiplier = playerAttackMultiplier * equipmentMultiplier,
        )
        boss.receiveDamage(damage)
        completedRepetitions = event.repetitionCount
        totalDamage += damage

        val nextState = if (boss.isDefeated()) GameState.VICTORY else GameState.BATTLE
        mutableState.value = createSnapshot(nextState, damage, attackType)
    }

    /**
     * Restarts the session with a fresh boss instance.
     */
    fun reset(newBoss: Boss) {
        boss = newBoss
        completedRepetitions = 0
        totalDamage = 0
        playerAttackMultiplier = 1f
        mutableState.value = createSnapshot(GameState.IDLE, null, null)
    }

    fun setPlayerAttackMultiplier(multiplier: Float) {
        if (mutableState.value.gameState == GameState.VICTORY) return
        playerAttackMultiplier = multiplier.coerceIn(0f, 1f)
        mutableState.value = createSnapshot(mutableState.value.gameState, null, null)
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
        totalDamage = totalDamage,
        playerAttackMultiplier = playerAttackMultiplier,
        lastDamage = lastDamage,
        lastAttackType = lastAttackType,
    )
}
