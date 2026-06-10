package com.example.rpg.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rpg.game.battle.GameState
import com.example.rpg.ui.screens.BattleScreen
import com.example.rpg.ui.screens.VictoryScreen
import com.example.rpg.ui.viewmodel.BattleViewModel

/**
 * Root Compose app that switches between MVP game states.
 */
@Composable
fun FitnessRpgApp(viewModel: BattleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    when (uiState.gameState) {
        GameState.VICTORY -> VictoryScreen(
            completedSquats = uiState.completedSquats,
            onRestart = viewModel::resetBattle,
        )
        else -> BattleScreen(
            uiState = uiState,
            poseAnalyzer = viewModel.poseAnalyzer,
        )
    }
}
