package com.example.rpg.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rpg.data.enemy.EnemyConfig
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
import com.example.rpg.game.battle.FixedEnemyAttackTimingPolicy
import com.example.rpg.game.battle.GameState
import com.example.rpg.game.player.PlayerStats
import com.example.rpg.pose.PoseAnalyzer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Coordinates exercise/enemy selection, pose detection, timed enemy actions, and combat.
 */
class BattleViewModel(
    application: Application,
    private val enemyRepository: EnemyRepository = InMemoryEnemyRepository(),
    private val exerciseConfigRepository: ExerciseConfigRepository = InMemoryExerciseConfigRepository(),
) : AndroidViewModel(application) {
    private val exercises = exerciseConfigRepository.getAll()
    private val detectorFactory = ExerciseDetectorFactory(exerciseConfigRepository.getSquatConfig())
    private val enemyAttackTimingPolicy = FixedEnemyAttackTimingPolicy()
    private val enemyChoicesByExercise = mutableMapOf<ExerciseType, List<EnemyConfig>>()
    private var selectedExercise = exercises.first()
    private var enemyChoices = emptyList<EnemyConfig>()
    private var selectedEnemy: EnemyConfig? = null
    private var activeDetector: ExerciseDetector? = null
    private var detectorEventsJob: Job? = null
    private var detectorResultJob: Job? = null
    private var enemyAttackJob: Job? = null
    private var debuffJob: Job? = null
    private var battleSession: BattleSession? = null
    private val mutableUiState = MutableStateFlow(
        BattleUiState(
            exercises = exercises,
            selectedExercise = selectedExercise,
        ),
    )
    private var screen = AppScreen.MAIN_MENU
    private var latestPoseFrame: PoseFrame? = null
    private var exerciseStatus = "Выберите упражнение"
    private var damageMessage: String? = null
    private var enemyAbilityMessage: String? = null
    private var hitEventId = 0L
    private var enemyAttackSecondsRemaining = 15
    private var debuffSecondsRemaining = 0

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

    fun openEnemySelection() {
        if (screen != AppScreen.MAIN_MENU) return
        enemyChoices = enemyChoicesByExercise.getOrPut(selectedExercise.type) {
            enemyRepository.getRandomChoices(selectedExercise.type)
        }
        selectedEnemy = enemyChoices.first()
        screen = AppScreen.ENEMY_SELECTION
        publishUiState()
    }

    fun selectEnemy(id: String) {
        if (screen != AppScreen.ENEMY_SELECTION) return
        selectedEnemy = enemyChoices.first { it.id == id }
        publishUiState()
    }

    fun returnToExerciseSelection() {
        if (screen != AppScreen.ENEMY_SELECTION) return
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun startBattle() {
        val enemyConfig = selectedEnemy ?: return
        stopBattleRuntime()
        val boss = enemyRepository.createBoss(enemyConfig.id)
        battleSession = BattleSession(
            boss = boss,
            exercise = selectedExercise,
        )
        activeDetector = detectorFactory.create(selectedExercise.type).also {
            it.reset()
            it.start()
        }
        observeActiveDetector()
        latestPoseFrame = null
        damageMessage = null
        enemyAbilityMessage = null
        hitEventId = 0L
        debuffSecondsRemaining = 0
        screen = AppScreen.BATTLE
        exerciseStatus = if (selectedExercise.detectorStatus == DetectorStatus.READY) {
            "Встаньте перед камерой"
        } else {
            "Этот детектор пока экспериментальный. Возможны ошибки распознавания."
        }
        battleSession?.startTracking()
        startEnemyAttackLoop(boss)
        publishUiState()
    }

    fun returnToMenu() {
        stopBattleRuntime()
        battleSession = null
        screen = AppScreen.MAIN_MENU
        latestPoseFrame = null
        damageMessage = null
        enemyAbilityMessage = null
        exerciseStatus = "Выберите упражнение"
        publishUiState()
    }

    fun simulateRepetition() {
        if (screen != AppScreen.BATTLE) return
        val nextCount = battleSession?.state?.value?.completedRepetitions?.plus(1) ?: return
        handleCompletedRepetition(
            ExerciseEvent.RepetitionCompleted(
                exerciseType = selectedExercise.type,
                repetitionCount = nextCount,
            ),
        )
    }

    override fun onCleared() {
        stopBattleRuntime()
        poseAnalyzer.close()
        super.onCleared()
    }

    private fun observePoseFrames() {
        viewModelScope.launch {
            poseAnalyzer.poseFrame.collect { frame ->
                if (screen != AppScreen.BATTLE) return@collect
                latestPoseFrame = frame
                val session = battleSession ?: return@collect
                if (frame.trackingState == PoseTrackingState.TRACKING) {
                    if (session.state.value.gameState == GameState.TRACKING) {
                        session.startBattle()
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

    private fun startEnemyAttackLoop(enemy: com.example.rpg.game.enemy.Enemy) {
        val intervalSeconds = enemyAttackTimingPolicy.intervalSeconds(
            exercise = selectedExercise,
            playerStats = PlayerStats(),
            enemy = enemy,
        )
        enemyAttackJob = viewModelScope.launch {
            while (screen == AppScreen.BATTLE) {
                for (seconds in intervalSeconds downTo 1) {
                    enemyAttackSecondsRemaining = seconds
                    publishUiState()
                    delay(1_000)
                    if (screen != AppScreen.BATTLE) return@launch
                }
                applyEnemyAbility(enemy)
            }
        }
    }

    private fun applyEnemyAbility(enemy: com.example.rpg.game.enemy.Enemy) {
        val session = battleSession ?: return
        if (session.state.value.gameState == GameState.VICTORY) return
        val ability = enemy.ability
        session.setPlayerAttackMultiplier(ability.attackMultiplier)
        enemyAbilityMessage = "${ability.name}: атака -${ability.attackReductionPercent}%"
        debuffJob?.cancel()
        debuffJob = viewModelScope.launch {
            for (seconds in ability.debuffDurationSeconds downTo 1) {
                debuffSecondsRemaining = seconds
                publishUiState()
                delay(1_000)
            }
            debuffSecondsRemaining = 0
            enemyAbilityMessage = null
            session.setPlayerAttackMultiplier(1f)
            publishUiState()
        }
        publishUiState()
    }

    private fun handleCompletedRepetition(event: ExerciseEvent.RepetitionCompleted) {
        val session = battleSession ?: return
        if (session.state.value.gameState == GameState.TRACKING) {
            session.startBattle()
        }
        session.handleExerciseEvent(event)
        val battle = session.state.value
        val damage = battle.lastDamage ?: return
        exerciseStatus = "Повтор засчитан"
        hitEventId += 1
        showDamageMessage("-$damage")
        if (battle.gameState == GameState.VICTORY) {
            exerciseStatus = "Противник побеждён"
            activeDetector?.stop()
            enemyAttackJob?.cancel()
            debuffJob?.cancel()
            screen = AppScreen.VICTORY
        }
        publishUiState()
    }

    private fun stopBattleRuntime() {
        detectorEventsJob?.cancel()
        detectorResultJob?.cancel()
        enemyAttackJob?.cancel()
        debuffJob?.cancel()
        detectorEventsJob = null
        detectorResultJob = null
        enemyAttackJob = null
        debuffJob = null
        activeDetector?.stop()
        activeDetector = null
        debuffSecondsRemaining = 0
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
        val battle = battleSession?.state?.value
        val enemy = selectedEnemy
        mutableUiState.value = BattleUiState(
            screen = screen,
            exercises = exercises,
            selectedExercise = selectedExercise,
            enemyChoices = enemyChoices,
            selectedEnemy = enemy,
            gameState = battle?.gameState ?: GameState.IDLE,
            bossName = battle?.boss?.name ?: enemy?.name.orEmpty(),
            bossImageResource = battle?.boss?.imageResource ?: enemy?.imageResource.orEmpty(),
            bossCurrentHp = battle?.boss?.currentHp ?: enemy?.maxHp ?: 0,
            bossMaxHp = battle?.boss?.maxHp ?: enemy?.maxHp ?: 0,
            completedRepetitions = battle?.completedRepetitions ?: 0,
            totalDamage = battle?.totalDamage ?: 0,
            playerAttackMultiplier = battle?.playerAttackMultiplier ?: 1f,
            enemyAttackSecondsRemaining = enemyAttackSecondsRemaining,
            debuffSecondsRemaining = debuffSecondsRemaining,
            enemyAbilityMessage = enemyAbilityMessage,
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
