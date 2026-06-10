package com.example.rpg.ui.viewmodel

import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.example.rpg.game.battle.GameState

/**
 * Single immutable UI model for the battle screen and victory screen.
 */
data class BattleUiState(
    val gameState: GameState = GameState.IDLE,
    val bossName: String = "Training Dummy",
    val bossCurrentHp: Int = 10,
    val bossMaxHp: Int = 10,
    val completedSquats: Int = 0,
    val exerciseStatus: String = "Ожидание старта",
    val damageMessage: String? = null,
    val poseFrame: PoseFrame? = null,
    val trackingState: PoseTrackingState = PoseTrackingState.INITIALIZING,
)
