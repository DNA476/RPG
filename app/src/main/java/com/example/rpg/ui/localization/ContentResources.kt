package com.example.rpg.ui.localization

import androidx.annotation.StringRes
import com.example.rpg.R
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
