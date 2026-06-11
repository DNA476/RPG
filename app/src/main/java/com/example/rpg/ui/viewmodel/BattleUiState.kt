package com.example.rpg.ui.viewmodel

import com.example.rpg.data.enemy.EnemyConfig
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.example.rpg.game.battle.GameState

enum class AppScreen {
    MAIN_MENU,
    ENEMY_SELECTION,
    BATTLE,
    VICTORY,
}

/**
 * Single immutable UI model for the complete encounter flow.
 */
data class BattleUiState(
    val screen: AppScreen = AppScreen.MAIN_MENU,
    val exercises: List<ExerciseConfig> = emptyList(),
    val selectedExercise: ExerciseConfig? = null,
    val enemyChoices: List<EnemyConfig> = emptyList(),
    val selectedEnemy: EnemyConfig? = null,
    val gameState: GameState = GameState.IDLE,
    val bossName: String = "",
    val bossImageResource: String = "",
    val bossCurrentHp: Int = 0,
    val bossMaxHp: Int = 0,
    val completedRepetitions: Int = 0,
    val totalDamage: Int = 0,
    val playerAttackMultiplier: Float = 1f,
    val enemyAttackSecondsRemaining: Int = 15,
    val debuffSecondsRemaining: Int = 0,
    val enemyAbilityMessage: String? = null,
    val exerciseStatus: String = "Выберите упражнение",
    val damageMessage: String? = null,
    val hitEventId: Long = 0L,
    val poseFrame: PoseFrame? = null,
    val trackingState: PoseTrackingState = PoseTrackingState.INITIALIZING,
)
