package com.example.rpg.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rpg.R
import com.example.rpg.data.FitnessRepository
import com.example.rpg.data.local.SharedPreferencesFitnessRepository
import com.example.rpg.data.local.SharedPreferencesInventoryRepository
import com.example.rpg.data.local.SharedPreferencesWeeklyQuestRepository
import com.example.rpg.data.enemy.EnemyConfig
import com.example.rpg.data.enemy.EnemyRepository
import com.example.rpg.data.enemy.InMemoryEnemyRepository
import com.example.rpg.data.exercise.ExerciseConfigRepository
import com.example.rpg.data.exercise.InMemoryExerciseConfigRepository
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryCatalog
import com.example.rpg.data.inventory.InventoryRepository
import com.example.rpg.data.inventory.InventoryState
import com.example.rpg.data.inventory.ItemBonusType
import com.example.rpg.data.profile.UserProfile
import com.example.rpg.data.profile.UserSex
import com.example.rpg.data.quest.QuestBattleResult
import com.example.rpg.data.quest.WeeklyQuestCatalog
import com.example.rpg.data.quest.WeeklyQuestProgress
import com.example.rpg.data.quest.WeeklyQuestRepository
import com.example.rpg.data.quest.WeeklyQuestState
import com.example.rpg.data.statistics.ExerciseCalorieEstimator
import com.example.rpg.data.statistics.ExerciseStatisticsAggregator
import com.example.rpg.data.statistics.PlayerLevelProgression
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
import com.example.rpg.game.player.EquipmentCombatBonuses
import com.example.rpg.pose.PoseAnalyzer
import com.example.rpg.ui.localization.exerciseFeedbackResource
import java.time.LocalDate
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
    private val fitnessRepository: FitnessRepository = SharedPreferencesFitnessRepository(
        application.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
    ),
    private val inventoryRepository: InventoryRepository = SharedPreferencesInventoryRepository(
        application.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
    ),
    private val weeklyQuestRepository: WeeklyQuestRepository =
        SharedPreferencesWeeklyQuestRepository(
            application.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE),
        ),
    private val currentDate: () -> LocalDate = LocalDate::now,
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
    private var battleEquipmentBonuses = EquipmentCombatBonuses()
    private var userProfile = fitnessRepository.loadProfile()
    private var profileForm = userProfile.toForm()
    private var statisticsPeriod = StatisticsPeriod.LAST_7_DAYS
    private var statisticsExercise: ExerciseType? = null
    private var statistics = StatisticsUiState()
    private var inventoryState: InventoryState = inventoryRepository.loadInventory()
    private var weeklyQuestState: WeeklyQuestState = WeeklyQuestProgress.stateForWeek(
        state = weeklyQuestRepository.loadState(),
        date = currentDate(),
    )
    private var activeQuestId: String? = null
    private var battleArtifactWasEquipped = false
    private var advancedQuestIds = emptySet<String>()
    private var rewardedItemIds = emptySet<String>()
    private var todayEstimatedCalories = 0
    private var todayHasActivity = false
    private var playerLevel = PlayerLevelProgression.MIN_LEVEL
    private var screen = if (fitnessRepository.isOnboardingCompleted()) {
        AppScreen.MAIN_MENU
    } else {
        AppScreen.ONBOARDING
    }
    private val mutableUiState = MutableStateFlow(
        BattleUiState(
            screen = screen,
            exercises = exercises,
            selectedExercise = selectedExercise,
            userProfile = userProfile,
            profileForm = profileForm,
        ),
    )
    private var latestPoseFrame: PoseFrame? = null
    private var exerciseStatusResource = R.string.status_choose_exercise
    private var damageMessage: String? = null
    private var hitEventId = 0L
    private var enemyAttackSecondsRemaining = 15
    private var debuffSecondsRemaining = 0

    val poseAnalyzer = PoseAnalyzer(application)
    val uiState: StateFlow<BattleUiState> = mutableUiState.asStateFlow()

    init {
        weeklyQuestRepository.saveState(weeklyQuestState)
        grantPendingQuestRewards()
        observePoseFrames()
        refreshStatistics()
        publishUiState()
    }

    fun updateProfileWeight(value: String) {
        profileForm = profileForm.copy(
            weightText = value.filter { it.isDigit() || it == '.' || it == ',' },
            weightError = false,
        )
        publishUiState()
    }

    fun updateProfileHeight(value: String) {
        profileForm = profileForm.copy(
            heightText = value.filter(Char::isDigit),
            heightError = false,
        )
        publishUiState()
    }

    fun selectProfileSex(sex: UserSex?) {
        profileForm = profileForm.copy(sex = sex)
        publishUiState()
    }

    fun saveProfile() {
        if (screen != AppScreen.ONBOARDING && screen != AppScreen.PROFILE) return
        val parsedWeight = profileForm.weightText.replace(',', '.').toFloatOrNull()
        val parsedHeight = profileForm.heightText.toIntOrNull()
        val weightError = profileForm.weightText.isNotBlank() &&
            (parsedWeight == null || parsedWeight !in MIN_WEIGHT_KG..MAX_WEIGHT_KG)
        val heightError = profileForm.heightText.isNotBlank() &&
            (parsedHeight == null || parsedHeight !in MIN_HEIGHT_CM..MAX_HEIGHT_CM)
        if (weightError || heightError) {
            profileForm = profileForm.copy(
                weightError = weightError,
                heightError = heightError,
            )
            publishUiState()
            return
        }

        userProfile = UserProfile(
            weightKg = parsedWeight,
            heightCm = parsedHeight,
            sex = profileForm.sex,
        )
        fitnessRepository.saveProfile(userProfile)
        fitnessRepository.completeOnboarding()
        screen = AppScreen.MAIN_MENU
        refreshStatistics()
        publishUiState()
    }

    fun skipOnboarding() {
        if (screen != AppScreen.ONBOARDING) return
        fitnessRepository.completeOnboarding()
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun openStatistics() {
        if (screen != AppScreen.MAIN_MENU) return
        statisticsPeriod = StatisticsPeriod.LAST_7_DAYS
        statisticsExercise = null
        refreshStatistics()
        screen = AppScreen.STATISTICS
        publishUiState()
    }

    fun selectStatisticsPeriod(period: StatisticsPeriod) {
        if (screen != AppScreen.STATISTICS) return
        statisticsPeriod = period
        refreshStatistics()
        publishUiState()
    }

    fun selectStatisticsExercise(exerciseType: ExerciseType?) {
        if (screen != AppScreen.STATISTICS) return
        statisticsExercise = exerciseType
        refreshStatistics()
        publishUiState()
    }

    fun openProfile() {
        if (screen != AppScreen.MAIN_MENU) return
        profileForm = userProfile.toForm()
        screen = AppScreen.PROFILE
        publishUiState()
    }

    fun returnFromProfile() {
        if (screen != AppScreen.PROFILE) return
        profileForm = userProfile.toForm()
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun returnFromStatistics() {
        if (screen != AppScreen.STATISTICS) return
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun openSettings() {
        if (screen != AppScreen.MAIN_MENU) return
        screen = AppScreen.SETTINGS
        publishUiState()
    }

    fun returnFromSettings() {
        if (screen != AppScreen.SETTINGS) return
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun openInventory() {
        if (screen != AppScreen.MAIN_MENU) return
        screen = AppScreen.INVENTORY
        publishUiState()
    }

    fun returnFromInventory() {
        if (screen != AppScreen.INVENTORY) return
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun openQuests() {
        if (screen != AppScreen.MAIN_MENU) return
        refreshWeeklyQuestState()
        screen = AppScreen.QUESTS
        publishUiState()
    }

    fun returnFromQuests() {
        if (screen != AppScreen.QUESTS) return
        screen = AppScreen.MAIN_MENU
        publishUiState()
    }

    fun startQuest(questId: String) {
        if (screen != AppScreen.QUESTS) return
        val quest = currentWeeklyQuests().firstOrNull { it.id == questId } ?: return
        selectedExercise = exerciseConfigRepository.get(quest.exerciseType)
        enemyChoices = enemyRepository.getQuestChoices(
            exerciseType = quest.exerciseType,
            requireResistantEnemy = quest.requiresResistantEnemy,
        )
        selectedEnemy = if (quest.requiresResistantEnemy) {
            enemyChoices.first { it.isResistantTo(quest.exerciseType) }
        } else {
            enemyChoices.first()
        }
        activeQuestId = quest.id
        screen = AppScreen.ENEMY_SELECTION
        publishUiState()
    }

    fun equipInventoryItem(itemId: String) {
        if (screen != AppScreen.INVENTORY) return
        val item = InventoryCatalog.get(itemId) ?: return
        inventoryState = inventoryState.equip(item)
        inventoryRepository.saveInventory(inventoryState)
        publishUiState()
    }

    fun unequipInventorySlot(slot: EquipmentSlot) {
        if (screen != AppScreen.INVENTORY) return
        inventoryState = inventoryState.unequip(slot)
        inventoryRepository.saveInventory(inventoryState)
        publishUiState()
    }

    fun selectExercise(type: ExerciseType) {
        if (screen != AppScreen.MAIN_MENU) return
        selectedExercise = exerciseConfigRepository.get(type)
        exerciseStatusResource = R.string.status_exercise_selected
        publishUiState()
    }

    fun openEnemySelection() {
        if (screen != AppScreen.MAIN_MENU) return
        activeQuestId = null
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
        screen = if (activeQuestId == null) AppScreen.MAIN_MENU else AppScreen.QUESTS
        publishUiState()
    }

    fun startBattle() {
        val enemyConfig = selectedEnemy ?: return
        stopBattleRuntime()
        val boss = enemyRepository.createBoss(
            id = enemyConfig.id,
            playerLevel = playerLevel.coerceAtLeast(1),
        )
        battleEquipmentBonuses = currentEquipmentCombatBonuses()
        battleSession = BattleSession(
            boss = boss,
            exercise = selectedExercise,
            equipmentBonuses = battleEquipmentBonuses,
        )
        activeDetector = detectorFactory.create(selectedExercise.type).also {
            it.reset()
            it.start()
        }
        observeActiveDetector()
        latestPoseFrame = null
        damageMessage = null
        hitEventId = 0L
        debuffSecondsRemaining = 0
        battleArtifactWasEquipped = EquipmentSlot.ARTIFACT in inventoryState.equippedItemIds
        advancedQuestIds = emptySet()
        rewardedItemIds = emptySet()
        screen = AppScreen.BATTLE
        exerciseStatusResource = if (selectedExercise.detectorStatus == DetectorStatus.READY) {
            R.string.feedback_stand_in_frame
        } else {
            R.string.status_experimental_warning
        }
        battleSession?.startTracking()
        startEnemyAttackLoop(boss)
        publishUiState()
    }

    fun returnToMenu() {
        stopBattleRuntime()
        battleSession = null
        activeQuestId = null
        screen = AppScreen.MAIN_MENU
        latestPoseFrame = null
        damageMessage = null
        exerciseStatusResource = R.string.status_choose_exercise
        refreshStatistics()
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
            recordStatistics = false,
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
                    exerciseStatusResource = when (frame.trackingState) {
                        PoseTrackingState.INITIALIZING -> R.string.feedback_tracking_initializing
                        PoseTrackingState.NO_PERSON -> R.string.feedback_no_person
                        PoseTrackingState.ERROR -> R.string.feedback_tracking_error
                        PoseTrackingState.TRACKING -> exerciseStatusResource
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
                    is ExerciseEvent.ExerciseStarted -> {
                        exerciseStatusResource = R.string.status_movement_started
                    }
                    is ExerciseEvent.ExerciseFinished -> Unit
                    is ExerciseEvent.RepetitionCompleted -> handleCompletedRepetition(event)
                }
                publishUiState()
            }
        }
        detectorResultJob = viewModelScope.launch {
            detector.result.collect { result ->
                if (!result.repetitionCompleted) {
                    exerciseStatusResource = exerciseFeedbackResource(result.feedback)
                    publishUiState()
                }
            }
        }
    }

    private fun startEnemyAttackLoop(enemy: com.example.rpg.game.enemy.Enemy) {
        val intervalSeconds = battleEquipmentBonuses.adjustedEnemyAbilityInterval(
            enemyAttackTimingPolicy.intervalSeconds(
                exercise = selectedExercise,
                playerStats = PlayerStats(),
                enemy = enemy,
            ),
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
        debuffJob?.cancel()
        debuffJob = viewModelScope.launch {
            val durationSeconds = battleEquipmentBonuses.adjustedDebuffDuration(
                ability.debuffDurationSeconds,
            )
            for (seconds in durationSeconds downTo 1) {
                debuffSecondsRemaining = seconds
                publishUiState()
                delay(1_000)
            }
            debuffSecondsRemaining = 0
            session.setPlayerAttackMultiplier(1f)
            publishUiState()
        }
        publishUiState()
    }

    private fun handleCompletedRepetition(
        event: ExerciseEvent.RepetitionCompleted,
        recordStatistics: Boolean = true,
    ) {
        val session = battleSession ?: return
        if (session.state.value.gameState == GameState.TRACKING) {
            session.startBattle()
        }
        val previousRepetitionCount = session.state.value.completedRepetitions
        session.handleExerciseEvent(event)
        val battle = session.state.value
        if (recordStatistics && battle.completedRepetitions > previousRepetitionCount) {
            if (event.activeSeconds > 0) {
                fitnessRepository.recordActiveSeconds(
                    exerciseType = event.exerciseType,
                    seconds = event.activeSeconds,
                    date = currentDate(),
                )
            } else {
                fitnessRepository.recordRepetition(
                    exerciseType = event.exerciseType,
                    date = currentDate(),
                )
            }
        }
        val damage = battle.lastDamage ?: return
        exerciseStatusResource = R.string.feedback_repetition_counted
        hitEventId += 1
        showDamageMessage("-$damage")
        if (battle.gameState == GameState.VICTORY) {
            val equipmentRewardIds = grantVictoryEquipmentReward()
            val battleRewardIds = grantResistantVictoryArtifactReward()
            val questRewardIds = recordQuestVictory()
            rewardedItemIds = equipmentRewardIds + battleRewardIds + questRewardIds
            exerciseStatusResource = R.string.status_enemy_defeated
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

    private fun refreshWeeklyQuestState() {
        val refreshedState = WeeklyQuestProgress.stateForWeek(
            state = weeklyQuestState,
            date = currentDate(),
        )
        if (refreshedState != weeklyQuestState) {
            weeklyQuestState = refreshedState
            weeklyQuestRepository.saveState(weeklyQuestState)
        }
    }

    private fun recordQuestVictory(): Set<String> {
        refreshWeeklyQuestState()
        val enemy = selectedEnemy ?: return emptySet()
        val update = WeeklyQuestProgress.recordVictory(
            quests = currentWeeklyQuests(),
            state = weeklyQuestState,
            result = QuestBattleResult(
                exerciseType = selectedExercise.type,
                enemyWasResistant = enemy.isResistantTo(selectedExercise.type),
                artifactWasEquipped = battleArtifactWasEquipped,
            ),
        )
        weeklyQuestState = update.state
        advancedQuestIds = update.advancedQuestIds
        val grantedItemIds = grantPendingQuestRewards()
        weeklyQuestRepository.saveState(weeklyQuestState)
        return grantedItemIds
    }

    private fun grantResistantVictoryArtifactReward(): Set<String> {
        val enemy = selectedEnemy ?: return emptySet()
        if (!enemy.isResistantTo(selectedExercise.type)) return emptySet()
        val rewardItemId = InventoryCatalog.resistantVictoryArtifactItemIds
            .firstOrNull { itemId -> itemId !in inventoryState.ownedItemIds }
            ?: return emptySet()
        inventoryState = inventoryState.addItem(rewardItemId)
        inventoryRepository.saveInventory(inventoryState)
        return setOf(rewardItemId)
    }

    private fun grantVictoryEquipmentReward(): Set<String> {
        val rewardItemId = InventoryCatalog.nextVictoryEquipmentReward(inventoryState.ownedItemIds)
            ?: return emptySet()
        inventoryState = inventoryState.addItem(rewardItemId)
        inventoryRepository.saveInventory(inventoryState)
        return setOf(rewardItemId)
    }

    private fun currentEquipmentCombatBonuses(): EquipmentCombatBonuses {
        val equippedBonuses = inventoryState.equippedItemIds.values
            .mapNotNull(InventoryCatalog::get)
            .flatMap { it.bonuses }
        fun total(type: ItemBonusType): Int = equippedBonuses
            .filter { it.type == type }
            .sumOf { it.value }

        return EquipmentCombatBonuses(
            attackPowerPercent = total(ItemBonusType.ATTACK_POWER_PERCENT),
            debuffDurationReductionPercent = total(
                ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT,
            ),
            enemyAbilityDelaySeconds = total(ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS),
            resistantMatchupDamagePercent = total(
                ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT,
            ),
            weaknessMatchupDamagePercent = total(ItemBonusType.WEAKNESS_MATCHUP_DAMAGE_PERCENT),
            openingAttackDamagePercent = total(ItemBonusType.OPENING_ATTACK_DAMAGE_PERCENT),
            streakDamagePercent = total(ItemBonusType.STREAK_DAMAGE_PERCENT),
        )
    }

    private fun grantPendingQuestRewards(): Set<String> {
        val grantedItemIds = linkedSetOf<String>()
        val newlyRewardedQuestIds = linkedSetOf<String>()
        currentWeeklyQuests().forEach { quest ->
            val isComplete = weeklyQuestState.progressFor(quest) >= quest.requiredVictories
            if (isComplete && quest.id !in weeklyQuestState.rewardedQuestIds) {
                inventoryState = inventoryState.addItem(quest.rewardItemId)
                grantedItemIds += quest.rewardItemId
                newlyRewardedQuestIds += quest.id
            }
        }
        if (newlyRewardedQuestIds.isNotEmpty()) {
            weeklyQuestState = weeklyQuestState.copy(
                rewardedQuestIds = weeklyQuestState.rewardedQuestIds + newlyRewardedQuestIds,
            )
            inventoryRepository.saveInventory(inventoryState)
            weeklyQuestRepository.saveState(weeklyQuestState)
        }
        return grantedItemIds
    }

    private fun publishUiState() {
        refreshWeeklyQuestState()
        val battle = battleSession?.state?.value
        val effectiveEnemyLevel = playerLevel.coerceAtLeast(1)
        val enemy = selectedEnemy?.scaledForLevel(effectiveEnemyLevel)
        mutableUiState.value = BattleUiState(
            screen = screen,
            exercises = exercises,
            selectedExercise = selectedExercise,
            userProfile = userProfile,
            profileForm = profileForm,
            statistics = statistics,
            inventory = InventoryUiState(
                items = InventoryCatalog.items.filter {
                    it.id in inventoryState.ownedItemIds
                },
                equippedItemIds = inventoryState.equippedItemIds,
                totalItemCount = InventoryCatalog.items.size,
            ),
            weeklyQuests = WeeklyQuestsUiState(
                entries = currentWeeklyQuests().map { quest ->
                    QuestUiEntry(
                        quest = quest,
                        progress = weeklyQuestState.progressFor(quest),
                        rewardGranted = quest.id in weeklyQuestState.rewardedQuestIds,
                    )
                },
                daysUntilReset = 8 - currentDate().dayOfWeek.value,
            ),
            advancedQuestIds = advancedQuestIds,
            rewardedItemIds = rewardedItemIds,
            todayEstimatedCalories = todayEstimatedCalories,
            todayHasActivity = todayHasActivity,
            enemyChoices = enemyChoices.map { it.scaledForLevel(effectiveEnemyLevel) },
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
            exerciseStatusResource = exerciseStatusResource,
            damageMessage = damageMessage,
            hitEventId = hitEventId,
            poseFrame = latestPoseFrame,
            trackingState = latestPoseFrame?.trackingState ?: PoseTrackingState.INITIALIZING,
        )
    }

    private fun refreshStatistics() {
        val today = currentDate()
        val allEntries = fitnessRepository.getDailyStatistics()
        val report = ExerciseStatisticsAggregator.aggregate(
            entries = allEntries,
            today = today,
            days = statisticsPeriod.days,
            selectedExercise = statisticsExercise,
        )
        val todayEntries = allEntries.filter { it.date == today }
        val lifetimeEstimatedCalories = ExerciseCalorieEstimator.estimateCalories(
            statistics = allEntries,
            weightKg = userProfile.weightKg,
        )
        playerLevel = PlayerLevelProgression.levelForCalories(lifetimeEstimatedCalories)
        todayEstimatedCalories = ExerciseCalorieEstimator.estimateCalories(
            statistics = todayEntries,
            weightKg = userProfile.weightKg,
        )
        todayHasActivity = todayEntries.any {
            it.repetitions > 0 || it.activeSeconds > 0
        }
        val chartPoints = report.dailyPoints.map {
            StatisticsChartPoint(
                date = it.date,
                repetitions = it.repetitions,
                activeSeconds = it.activeSeconds,
            )
        }
        val summaries = report.totalsByExercise.map { total ->
            ExerciseStatisticsSummary(
                exerciseType = total.exerciseType,
                repetitions = total.repetitions,
                activeSeconds = total.activeSeconds,
            )
        }.sortedByDescending { it.repetitions + it.activeSeconds }

        statistics = StatisticsUiState(
            period = statisticsPeriod,
            selectedExercise = statisticsExercise,
            chartPoints = chartPoints,
            exerciseSummaries = summaries,
            totalRepetitions = report.totalRepetitions,
            totalActiveSeconds = report.totalActiveSeconds,
            estimatedCalories = ExerciseCalorieEstimator.estimateCalories(
                statistics = report.selectedEntries,
                weightKg = userProfile.weightKg,
            ),
            playerLevel = playerLevel,
            maximumPlayerLevel = PlayerLevelProgression.MAX_LEVEL,
            activeDays = report.activeDays,
            usesDefaultWeight = userProfile.weightKg == null,
        )
    }

    private fun currentWeeklyQuests() = WeeklyQuestCatalog.questsForWeek(weeklyQuestState.weekId)

    private fun UserProfile.toForm(): ProfileFormUiState = ProfileFormUiState(
        weightText = weightKg?.let { weight ->
            if (weight % 1f == 0f) weight.toInt().toString() else weight.toString()
        }.orEmpty(),
        heightText = heightCm?.toString().orEmpty(),
        sex = sex,
    )

    class Factory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            BattleViewModel(application) as T
    }

    companion object {
        private const val PREFERENCES_NAME = "fitness_profile_and_statistics"
        private const val MIN_WEIGHT_KG = 20f
        private const val MAX_WEIGHT_KG = 300f
        private const val MIN_HEIGHT_CM = 80
        private const val MAX_HEIGHT_CM = 250
    }
}
