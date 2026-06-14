package com.example.rpg.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rpg.ui.screens.BattleScreen
import com.example.rpg.ui.screens.EnemySelectionScreen
import com.example.rpg.ui.screens.MainMenuScreen
import com.example.rpg.ui.screens.ProfileScreen
import com.example.rpg.ui.screens.StatisticsScreen
import com.example.rpg.ui.screens.VictoryScreen
import com.example.rpg.ui.viewmodel.AppScreen
import com.example.rpg.ui.viewmodel.BattleViewModel

@Composable
fun FitnessRpgApp(viewModel: BattleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.screen) {
        AppScreen.ONBOARDING -> ProfileScreen(
            form = uiState.profileForm,
            isOnboarding = true,
            onWeightChanged = viewModel::updateProfileWeight,
            onHeightChanged = viewModel::updateProfileHeight,
            onSexSelected = viewModel::selectProfileSex,
            onSave = viewModel::saveProfile,
            onSkip = viewModel::skipOnboarding,
            onBack = {},
        )
        AppScreen.MAIN_MENU -> MainMenuScreen(
            exercises = uiState.exercises,
            selectedExercise = uiState.selectedExercise,
            todayEstimatedCalories = uiState.todayEstimatedCalories,
            todayHasActivity = uiState.todayHasActivity,
            usesDefaultWeight = uiState.userProfile.weightKg == null,
            onExerciseSelected = viewModel::selectExercise,
            onContinue = viewModel::openEnemySelection,
            onStatistics = viewModel::openStatistics,
            onProfile = viewModel::openProfile,
        )
        AppScreen.STATISTICS -> StatisticsScreen(
            statistics = uiState.statistics,
            exercises = uiState.exercises,
            onPeriodSelected = viewModel::selectStatisticsPeriod,
            onExerciseSelected = viewModel::selectStatisticsExercise,
            onBack = viewModel::returnFromStatistics,
        )
        AppScreen.PROFILE -> ProfileScreen(
            form = uiState.profileForm,
            isOnboarding = false,
            onWeightChanged = viewModel::updateProfileWeight,
            onHeightChanged = viewModel::updateProfileHeight,
            onSexSelected = viewModel::selectProfileSex,
            onSave = viewModel::saveProfile,
            onSkip = {},
            onBack = viewModel::returnFromProfile,
        )
        AppScreen.ENEMY_SELECTION -> EnemySelectionScreen(
            exercise = requireNotNull(uiState.selectedExercise),
            enemies = uiState.enemyChoices,
            selectedEnemy = uiState.selectedEnemy,
            onEnemySelected = viewModel::selectEnemy,
            onStartBattle = viewModel::startBattle,
            onBack = viewModel::returnToExerciseSelection,
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
