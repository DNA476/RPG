package com.example.rpg.ui.localization

import androidx.annotation.StringRes
import com.example.rpg.R
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryItem
import com.example.rpg.data.inventory.ItemBonusType
import com.example.rpg.data.inventory.ItemRarity
import com.example.rpg.data.quest.QuestCategory
import com.example.rpg.domain.exercise.DetectorStatus
import com.example.rpg.domain.exercise.ExerciseDifficulty
import com.example.rpg.domain.exercise.ExerciseFeedback
import com.example.rpg.domain.exercise.ExerciseType
import com.example.rpg.domain.pose.PoseTrackingState

@StringRes
fun exerciseNameResource(type: ExerciseType): Int = when (type) {
    ExerciseType.SQUAT -> R.string.exercise_squat_name
    ExerciseType.PUSH_UP -> R.string.exercise_push_up_name
    ExerciseType.PULL_UP -> R.string.exercise_pull_up_name
    ExerciseType.CRUNCH -> R.string.exercise_crunch_name
    ExerciseType.LUNGE -> R.string.exercise_lunge_name
    ExerciseType.JUMPING_JACK -> R.string.exercise_jumping_jack_name
    ExerciseType.PLANK -> R.string.exercise_plank_name
}

@StringRes
fun exerciseDescriptionResource(type: ExerciseType): Int = when (type) {
    ExerciseType.SQUAT -> R.string.exercise_squat_description
    ExerciseType.PUSH_UP -> R.string.exercise_push_up_description
    ExerciseType.PULL_UP -> R.string.exercise_pull_up_description
    ExerciseType.CRUNCH -> R.string.exercise_crunch_description
    ExerciseType.LUNGE -> R.string.exercise_lunge_description
    ExerciseType.JUMPING_JACK -> R.string.exercise_jumping_jack_description
    ExerciseType.PLANK -> R.string.exercise_plank_description
}

@StringRes
fun difficultyResource(difficulty: ExerciseDifficulty): Int = when (difficulty) {
    ExerciseDifficulty.EASY -> R.string.difficulty_easy
    ExerciseDifficulty.MEDIUM -> R.string.difficulty_medium
    ExerciseDifficulty.HARD -> R.string.difficulty_hard
    ExerciseDifficulty.EXTREME -> R.string.difficulty_extreme
}

@StringRes
fun detectorStatusResource(status: DetectorStatus): Int = when (status) {
    DetectorStatus.READY -> R.string.detector_ready
    DetectorStatus.EXPERIMENTAL -> R.string.detector_experimental
    DetectorStatus.COMING_SOON -> R.string.detector_coming_soon
}

@StringRes
fun exerciseFeedbackResource(feedback: ExerciseFeedback): Int = when (feedback) {
    ExerciseFeedback.WAITING -> R.string.feedback_waiting
    ExerciseFeedback.STAND_IN_FRAME -> R.string.feedback_stand_in_frame
    ExerciseFeedback.DETECTOR_STOPPED -> R.string.feedback_detector_stopped
    ExerciseFeedback.KNEES_NOT_VISIBLE -> R.string.feedback_knees_not_visible
    ExerciseFeedback.READY_TO_SQUAT -> R.string.feedback_ready_to_squat
    ExerciseFeedback.STRAIGHTEN_LEGS -> R.string.feedback_straighten_legs
    ExerciseFeedback.BOTTOM_REACHED -> R.string.feedback_bottom_reached
    ExerciseFeedback.LOWER_MORE -> R.string.feedback_lower_more
    ExerciseFeedback.REPETITION_COUNTED -> R.string.feedback_repetition_counted
    ExerciseFeedback.RETURN_TO_STANCE -> R.string.feedback_return_to_stance
    ExerciseFeedback.EXPERIMENTAL_DETECTOR -> R.string.feedback_experimental_detector
    ExerciseFeedback.POSE_TRACKED -> R.string.feedback_pose_tracked
    ExerciseFeedback.TRACKING_INITIALIZING -> R.string.feedback_tracking_initializing
    ExerciseFeedback.NO_PERSON -> R.string.feedback_no_person
    ExerciseFeedback.TRACKING_ERROR -> R.string.feedback_tracking_error
}

@StringRes
fun trackingStateResource(state: PoseTrackingState): Int = when (state) {
    PoseTrackingState.INITIALIZING -> R.string.tracking_initializing
    PoseTrackingState.TRACKING -> R.string.tracking_active
    PoseTrackingState.NO_PERSON -> R.string.tracking_no_person
    PoseTrackingState.ERROR -> R.string.tracking_error
}

@StringRes
fun enemyNameResource(id: String): Int = when (id) {
    "goblin_scout" -> R.string.enemy_goblin_scout_name
    "goblin_brute" -> R.string.enemy_goblin_brute_name
    "goblin_shaman" -> R.string.enemy_goblin_shaman_name
    "goblin_guard" -> R.string.enemy_goblin_guard_name
    "cave_hound" -> R.string.enemy_cave_hound_name
    "ash_hound" -> R.string.enemy_ash_hound_name
    else -> R.string.enemy_unknown_name
}

@StringRes
fun enemyDescriptionResource(id: String): Int = when (id) {
    "goblin_scout" -> R.string.enemy_goblin_scout_description
    "goblin_brute" -> R.string.enemy_goblin_brute_description
    "goblin_shaman" -> R.string.enemy_goblin_shaman_description
    "goblin_guard" -> R.string.enemy_goblin_guard_description
    "cave_hound" -> R.string.enemy_cave_hound_description
    "ash_hound" -> R.string.enemy_ash_hound_description
    else -> R.string.enemy_unknown_description
}

@StringRes
fun enemyAbilityNameResource(id: String): Int = when (id) {
    "goblin_scout" -> R.string.enemy_goblin_scout_ability
    "goblin_brute" -> R.string.enemy_goblin_brute_ability
    "goblin_shaman" -> R.string.enemy_goblin_shaman_ability
    "goblin_guard" -> R.string.enemy_goblin_guard_ability
    "cave_hound" -> R.string.enemy_cave_hound_ability
    "ash_hound" -> R.string.enemy_ash_hound_ability
    else -> R.string.enemy_unknown_ability
}

@StringRes
fun inventoryItemNameResource(item: InventoryItem): Int = when (item.id) {
    "novice_headband" -> R.string.item_novice_headband
    "iron_training_vest" -> R.string.item_iron_training_vest
    "steady_wraps" -> R.string.item_steady_wraps
    "runners_leggings" -> R.string.item_runners_leggings
    "trail_boots" -> R.string.item_trail_boots
    "oak_training_blade" -> R.string.item_oak_training_blade
    "ember_edge" -> R.string.item_ember_edge
    "resolve_stone" -> R.string.item_resolve_stone
    "echo_charm" -> R.string.item_echo_charm
    "guardian_wraps" -> R.string.item_guardian_wraps
    "resistance_breaker" -> R.string.item_resistance_breaker
    "crown_of_trials" -> R.string.item_crown_of_trials
    else -> R.string.item_unknown
}

@StringRes
fun questCategoryResource(category: QuestCategory): Int = when (category) {
    QuestCategory.REGULAR -> R.string.quest_category_regular
    QuestCategory.DIFFICULT -> R.string.quest_category_difficult
    QuestCategory.CHALLENGE -> R.string.quest_category_challenge
}

@StringRes
fun questTitleResource(id: String): Int = when (id) {
    "weekly_regular_squats" -> R.string.quest_title_regular_squats
    "weekly_difficult_pushups" -> R.string.quest_title_difficult_pushups
    "weekly_challenge_pullups" -> R.string.quest_title_challenge_pullups
    else -> R.string.weekly_quests
}

@StringRes
fun equipmentSlotResource(slot: EquipmentSlot): Int = when (slot) {
    EquipmentSlot.HEAD -> R.string.slot_head
    EquipmentSlot.CHEST -> R.string.slot_chest
    EquipmentSlot.HANDS -> R.string.slot_hands
    EquipmentSlot.LEGS -> R.string.slot_legs
    EquipmentSlot.FEET -> R.string.slot_feet
    EquipmentSlot.WEAPON -> R.string.slot_weapon
    EquipmentSlot.ARTIFACT -> R.string.slot_artifact
}

@StringRes
fun itemRarityResource(rarity: ItemRarity): Int = when (rarity) {
    ItemRarity.COMMON -> R.string.rarity_common
    ItemRarity.RARE -> R.string.rarity_rare
    ItemRarity.EPIC -> R.string.rarity_epic
    ItemRarity.LEGENDARY -> R.string.rarity_legendary
}

@StringRes
fun itemBonusResource(type: ItemBonusType): Int = when (type) {
    ItemBonusType.ATTACK_POWER_PERCENT -> R.string.bonus_attack_power
    ItemBonusType.DEBUFF_DURATION_REDUCTION_PERCENT -> R.string.bonus_debuff_reduction
    ItemBonusType.ENEMY_ABILITY_DELAY_SECONDS -> R.string.bonus_enemy_delay
    ItemBonusType.RESISTANT_MATCHUP_DAMAGE_PERCENT -> R.string.bonus_resistant_damage
}
