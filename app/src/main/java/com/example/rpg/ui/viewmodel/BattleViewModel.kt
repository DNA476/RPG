package com.example.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rpg.data.enemy.EnemyRepository
import com.example.rpg.data.enemy.InMemoryEnemyRepository
import com.example.rpg.data.exercise.ExerciseConfigRepository
import com.example.rpg.data.exercise.InMemoryExerciseConfigRepository
import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.domain.exercise.SquatDetector
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.example.rpg.game.battle.BattleSession
import com.example.rpg.game.battle.GameState
import com.example.rpg.pose.PoseAnalyzer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVVM bridge between camera/pose, exercise detection, battle engine, and Compose UI.
 */
class BattleViewModel(
    application: Application,
    private val enemyRepository: EnemyRepository = InMemoryEnemyRepository(),
    exerciseConfigRepository: ExerciseConfigRepository = InMemoryExerciseConfigRepository(),
) : AndroidViewModel(application) {
    private val squatDetector = SquatDetector(exerciseConfigRepository.getSquatConfig())
    private var battleSession = BattleSession(enemyRepository.getTrainingBoss())
    private val mutableUiState = MutableStateFlow(BattleUiState())
    private var latestPoseFrame: PoseFrame? = null
    private var exerciseStatus = "Нажмите старт и встаньте перед камерой"
    private var damageMessage: String? = null

    val poseAnalyzer = PoseAnalyzer(application)
    val uiState: StateFlow<BattleUiState> = mutableUiState.asStateFlow()

    init {
        observePoseFrames()
        observeExerciseEvents()
        observeBattleState()
    }

    /**
     * Starts pose tracking and the battle loop.
     */
    fun start() {
        squatDetector.start()
        battleSession.startTracking()
        publishUiState()
    }

    /**
     * Resets the boss HP, squat counter, detector phase, and UI state.
     */
    fun resetBattle() {
        squatDetector.reset()
        battleSession.reset(enemyRepository.getTrainingBoss())
        exerciseStatus = "Нажмите старт и встаньте перед камерой"
        damageMessage = null
        start()
    }

    override fun onCleared() {
        squatDetector.stop()
        poseAnalyzer.close()
        super.onCleared()
    }

    private fun observePoseFrames() {
        viewModelScope.launch {
            poseAnalyzer.poseFrame.collect { frame ->
                latestPoseFrame = frame
                exerciseStatus = when (frame.trackingState) {
                    PoseTrackingState.INITIALIZING -> "Инициализация трекинга"
                    PoseTrackingState.TRACKING -> exerciseStatus
                    PoseTrackingState.NO_PERSON -> "Человек не найден"
                    PoseTrackingState.ERROR -> "Ошибка трекинга позы"
                }
                if (frame.trackingState == PoseTrackingState.TRACKING) {
                    if (battleSession.state.value.gameState == GameState.TRACKING) {
                        battleSession.startBattle()
                    }
                    squatDetector.processPoseFrame(frame)
                }
                publishUiState()
            }
        }
    }

    private fun observeExerciseEvents() {
        viewModelScope.launch {
            squatDetector.events.collect { event ->
                when (event) {
                    is ExerciseEvent.ExerciseStarted -> exerciseStatus = "Приседание начато"
                    is ExerciseEvent.ExerciseFinished -> exerciseStatus = "Вернитесь в стойку"
                    is ExerciseEvent.RepetitionCompleted -> {
                        battleSession.handleExerciseEvent(event)
                        val damage = battleSession.state.value.lastDamage ?: 0
                        exerciseStatus = "Повтор засчитан"
                        showDamageMessage("-$damage HP")
                    }
                }
                publishUiState()
            }
        }
    }

    private fun observeBattleState() {
        viewModelScope.launch {
            battleSession.state.collect {
                if (it.gameState == GameState.VICTORY) {
                    exerciseStatus = "Босс побежден"
                    squatDetector.stop()
                }
                publishUiState()
            }
        }
    }

    private fun showDamageMessage(message: String) {
        damageMessage = message
        viewModelScope.launch {
            delay(900)
            if (damageMessage == message) {
                damageMessage = null
                publishUiState()
            }
        }
    }

    private fun publishUiState() {
        val battle = battleSession.state.value
        mutableUiState.value = BattleUiState(
            gameState = battle.gameState,
            bossName = battle.boss.name,
            bossCurrentHp = battle.boss.currentHp,
            bossMaxHp = battle.boss.maxHp,
            completedSquats = battle.completedRepetitions,
            exerciseStatus = exerciseStatus,
            damageMessage = damageMessage,
            poseFrame = latestPoseFrame,
            trackingState = latestPoseFrame?.trackingState ?: PoseTrackingState.INITIALIZING,
        )
    }

    /**
     * Factory that keeps dependency construction explicit without adding DI framework overhead to the MVP.
     */
    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BattleViewModel(application) as T
        }
    }
}
