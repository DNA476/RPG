package com.example.rpg.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rpg.ui.screens.BattleScreen
import com.example.rpg.ui.screens.MainMenuScreen
import com.example.rpg.ui.screens.VictoryScreen
import com.example.rpg.ui.viewmodel.AppScreen
import com.example.rpg.ui.viewmodel.BattleViewModel

@Composable
fun FitnessRpgApp(viewModel: BattleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.screen) {
        AppScreen.MAIN_MENU -> MainMenuScreen(
            exercises = uiState.exercises,
            selectedExercise = uiState.selectedExercise,
            onExerciseSelected = viewModel::selectExercise,
            onStartBattle = viewModel::startBattle,
        )
        AppScreen.BATTLE -> BattleScreen(
            uiState = uiState,
            poseAnalyzer = viewModel.poseAnalyzer,
            onBackToMenu = viewModel::returnToMenu,
            onSimulateRepetition = viewModel::simulateRepetition,
        )
        AppScreen.VICTORY -> VictoryScreen(
            bossName = uiState.bossName,
            exerciseName = uiState.selectedExercise?.displayName.orEmpty(),
            completedRepetitions = uiState.completedRepetitions,
            totalDamage = uiState.totalDamage,
            onBackToMenu = viewModel::returnToMenu,
        )
    }
}
