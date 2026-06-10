package com.example.rpg.domain.exercise

/**
 * Exercise detector output. Future detectors should emit these events instead of calling game logic directly.
 */
sealed interface ExerciseEvent {
    val exerciseType: ExerciseType

    /**
     * User moved into the active phase of the exercise.
     */
    data class ExerciseStarted(
        override val exerciseType: ExerciseType,
    ) : ExerciseEvent

    /**
     * User completed a valid full repetition.
     */
    data class RepetitionCompleted(
        override val exerciseType: ExerciseType,
        val repetitionCount: Int,
    ) : ExerciseEvent

    /**
     * User returned to the resting phase after an active movement.
     */
    data class ExerciseFinished(
        override val exerciseType: ExerciseType,
    ) : ExerciseEvent
}
