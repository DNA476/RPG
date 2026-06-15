package com.example.rpg.ui.viewmodel

import androidx.annotation.StringRes
import com.example.rpg.R
import com.example.rpg.data.enemy.EnemyConfig
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryItem
import com.example.rpg.data.profile.UserProfile
import com.example.rpg.data.profile.UserSex
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.example.rpg.game.battle.GameState
import java.time.LocalDate

enum class AppScreen {
    ONBOARDING,
    MAIN_MENU,
    STATISTICS,
    PROFILE,
    SETTINGS,
    INVENTORY,
    ENEMY_SELECTION,
    BATTLE,
    VICTORY,
}

enum class StatisticsPeriod(
    val days: Long,
) {
    LAST_7_DAYS(7),
    LAST_30_DAYS(30),
    LAST_90_DAYS(90),
}

data class ProfileFormUiState(
    val weightText: String = "",
    val heightText: String = "",
    val sex: UserSex? = null,
    val weightError: Boolean = false,
    val heightError: Boolean = false,
)

data class StatisticsChartPoint(
    val date: LocalDate,
    val repetitions: Int,
    val activeSeconds: Int,
)

data class ExerciseStatisticsSummary(
    val exerciseType: ExerciseType,
    val repetitions: Int,
    val activeSeconds: Int,
)

data class StatisticsUiState(
    val period: StatisticsPeriod = StatisticsPeriod.LAST_7_DAYS,
    val selectedExercise: ExerciseType? = null,
    val chartPoints: List<StatisticsChartPoint> = emptyList(),
    val exerciseSummaries: List<ExerciseStatisticsSummary> = emptyList(),
    val totalRepetitions: Int = 0,
    val totalActiveSeconds: Int = 0,
    val estimatedCalories: Int = 0,
    val activeDays: Int = 0,
    val usesDefaultWeight: Boolean = true,
)

data class InventoryUiState(
    val items: List<InventoryItem> = emptyList(),
    val equippedItemIds: Map<EquipmentSlot, String> = emptyMap(),
)

/**
 * Single immutable UI model for the complete encounter flow.
 */
data class BattleUiState(
    val screen: AppScreen = AppScreen.ONBOARDING,
    val exercises: List<ExerciseConfig> = emptyList(),
    val selectedExercise: ExerciseConfig? = null,
    val userProfile: UserProfile = UserProfile(),
    val profileForm: ProfileFormUiState = ProfileFormUiState(),
    val statistics: StatisticsUiState = StatisticsUiState(),
    val inventory: InventoryUiState = InventoryUiState(),
    val todayEstimatedCalories: Int = 0,
    val todayHasActivity: Boolean = false,
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
    @param:StringRes val exerciseStatusResource: Int = R.string.status_choose_exercise,
    val damageMessage: String? = null,
    val hitEventId: Long = 0L,
    val poseFrame: PoseFrame? = null,
    val trackingState: PoseTrackingState = PoseTrackingState.INITIALIZING,
)
