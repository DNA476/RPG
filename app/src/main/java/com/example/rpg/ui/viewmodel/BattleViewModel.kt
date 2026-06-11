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
import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseConfig
import com.example.rpg.domain.exercise.ExerciseDetector
import com.example.rpg.domain.exercise.ExerciseDetectorFactory
import com.example.rpg.domain.exercise.ExerciseEvent
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.example.rpg.game.battle.BattleSession
import com.example.rpg.game.battle.GameState
import com.example.rpg.pose.PoseAnalyzer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Coordinates exercise selection, pose detection, combat, and app navigation.
 */
class BattleViewModel(
    application: Application,
    private val enemyRepository: EnemyRepository = InMemoryEnemyRepository(),
    private val exerciseConfigRepository: ExerciseConfigRepository = InMemoryExerciseConfigRepository(),
) : AndroidViewModel(application) {
    private val exercises = exerciseConfigRepository.getAll()
    private val detectorFactory = ExerciseDetectorFactory(exerciseConfigRepository.getSquatConfig())
    private var selectedExercise = exercises.first()
    private var activeDetector: ExerciseDetector? = null
    private var detectorEventsJob: Job? = null
    private var detectorResultJob: Job? = null
    private var battleSession = createBattleSession(selectedExercise)
    private val mutableUiState = MutableStateFlow(
        BattleUiState(
            exercises = exercises,
            selectedExercise = selectedExercise,
            bossName = battleSession.state.value.boss.name,
            bossCurrentHp = battleSession.state.value.boss.currentHp,
            bossMaxHp = battleSession.state.value.boss.maxHp,
        ),
    )
    private var screen = AppScreen.MAIN_MENU
    private var latestPoseFrame: PoseFrame? = null
    private var exerciseStatus = "Выберите упражнение"
    private var damageMessage: String? = null
    private var hitEventId = 0L

    val poseAnalyzer = PoseAnalyzer(application)
    val uiState: StateFlow<BattleUiState> = mutableUiState.asStateFlow()

    init {
        observePoseFrames()
    }

    fun selectExercise(type: ExerciseType) {
        if (screen != AppScreen.MAIN_MENU) return
        selectedExercise = exerciseConfigRepository.get(type)
        exerciseStatus = "Выбрано: ${selectedExercise.displayName}"
        publishUiState()
    }

    fun startBattle() {
        // TODO: promote this selection into a session config for in-battle switching,
        // Auto Mode, and Free Battle Mode.
        stopActiveDetector()
        battleSession = createBattleSession(selectedExercise)
        activeDetector = detectorFactory.create(selectedExercise.type).also {
            it.reset()
            it.start()
        }
        observeActiveDetector()
        latestPoseFrame = null
        damageMessage = null
        hitEventId = 0L
        screen = AppScreen.BATTLE
        exerciseStatus = if (selectedExercise.detectorStatus == DetectorStatus.READY) {
            "Встаньте перед камерой"
        } else {
            "Этот детектор пока экспериментальный. Возможны ошибки распознавания."
        }
        battleSession.startTracking()
        publishUiState()
    }

    fun returnToMenu() {
        stopActiveDetector()
        screen = AppScreen.MAIN_MENU
        latestPoseFrame = null
        damageMessage = null
        exerciseStatus = "Выберите упражнение"
        publishUiState()
    }

    fun simulateRepetition() {
        if (screen != AppScreen.BATTLE) return
        val nextCount = battleSession.state.value.completedRepetitions + 1
        handleCompletedRepetition(
            ExerciseEvent.RepetitionCompleted(
                exerciseType = selectedExercise.type,
                repetitionCount = nextCount,
            ),
        )
    }

    override fun onCleared() {
        stopActiveDetector()
        poseAnalyzer.close()
        super.onCleared()
    }

    private fun createBattleSession(exercise: ExerciseConfig): BattleSession =
        BattleSession(
            boss = enemyRepository.getTrainingBoss(),
            exercise = exercise,
        )

    private fun observePoseFrames() {
        viewModelScope.launch {
            poseAnalyzer.poseFrame.collect { frame ->
                if (screen != AppScreen.BATTLE) return@collect
                latestPoseFrame = frame
                if (frame.trackingState == PoseTrackingState.TRACKING) {
                    if (battleSession.state.value.gameState == GameState.TRACKING) {
                        battleSession.startBattle()
                    }
                    activeDetector?.processPoseFrame(frame)
                } else {
                    exerciseStatus = when (frame.trackingState) {
                        PoseTrackingState.INITIALIZING -> "Инициализация трекинга"
                        PoseTrackingState.NO_PERSON -> "Человек не найден"
                        PoseTrackingState.ERROR -> "Ошибка трекинга позы"
                        PoseTrackingState.TRACKING -> exerciseStatus
                    }
                }
                publishUiState()
            }
        }
    }

    private fun observeActiveDetector() {
        val detector = activeDetector ?: return
        detectorEventsJob = viewModelScope.launch {
            detector.events.collect { event ->
                when (event) {
                    is ExerciseEvent.ExerciseStarted -> exerciseStatus = "Движение начато"
                    is ExerciseEvent.ExerciseFinished -> Unit
                    is ExerciseEvent.RepetitionCompleted -> handleCompletedRepetition(event)
                }
                publishUiState()
            }
        }
        detectorResultJob = viewModelScope.launch {
            detector.result.collect { result ->
                if (!result.repetitionCompleted) {
                    exerciseStatus = result.stateLabel
                    publishUiState()
                }
            }
        }
    }

    private fun handleCompletedRepetition(event: ExerciseEvent.RepetitionCompleted) {
        if (battleSession.state.value.gameState == GameState.TRACKING) {
            battleSession.startBattle()
        }
        battleSession.handleExerciseEvent(event)
        val battle = battleSession.state.value
        val damage = battle.lastDamage ?: return
        exerciseStatus = "Повтор засчитан"
        hitEventId += 1
        showDamageMessage("-$damage")
        if (battle.gameState == GameState.VICTORY) {
            exerciseStatus = "Босс побежден"
            activeDetector?.stop()
            screen = AppScreen.VICTORY
        }
        publishUiState()
    }

    private fun stopActiveDetector() {
        detectorEventsJob?.cancel()
        detectorResultJob?.cancel()
        detectorEventsJob = null
        detectorResultJob = null
        activeDetector?.stop()
        activeDetector = null
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
            screen = screen,
            exercises = exercises,
            selectedExercise = selectedExercise,
            gameState = battle.gameState,
            bossName = battle.boss.name,
            bossCurrentHp = battle.boss.currentHp,
            bossMaxHp = battle.boss.maxHp,
            completedRepetitions = battle.completedRepetitions,
            totalDamage = battle.totalDamage,
            exerciseStatus = exerciseStatus,
            damageMessage = damageMessage,
            hitEventId = hitEventId,
            poseFrame = latestPoseFrame,
            trackingState = latestPoseFrame?.trackingState ?: PoseTrackingState.INITIALIZING,
        )
    }

    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BattleViewModel(application) as T
    }
}
