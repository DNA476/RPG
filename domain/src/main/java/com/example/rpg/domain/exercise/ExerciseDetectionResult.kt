package com.example.rpg.domain.exercise

/**
 * Latest detector feedback suitable for UI and local diagnostics.
 */
data class ExerciseDetectionResult(
    val repetitionCompleted: Boolean = false,
    val feedback: ExerciseFeedback = ExerciseFeedback.WAITING,
    val confidence: Float = 0f,
    val debugInfo: String? = null,
)

enum class ExerciseFeedback {
    WAITING,
    STAND_IN_FRAME,
    DETECTOR_STOPPED,
    KNEES_NOT_VISIBLE,
    READY_TO_SQUAT,
    STRAIGHTEN_LEGS,
    BOTTOM_REACHED,
    TARGET_REACHED,
    LOWER_MORE,
    REPETITION_COUNTED,
    RETURN_TO_STANCE,
    EXTEND_ARMS,
    PULL_HIGHER,
    CURL_UP,
    OPEN_ARMS_AND_LEGS,
    CLOSE_ARMS_AND_LEGS,
    EXPERIMENTAL_DETECTOR,
    POSE_TRACKED,
    TRACKING_INITIALIZING,
    NO_PERSON,
    TRACKING_ERROR,
}
