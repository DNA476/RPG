package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Safe placeholder for detectors that still need a movement-specific state machine.
 */
abstract class ExperimentalExerciseDetector(
    final override val exerciseType: ExerciseType,
) : ExerciseDetector {
    private val mutableEvents = MutableSharedFlow<ExerciseEvent>(extraBufferCapacity = 1)
    private val mutableResult = MutableStateFlow(
        ExerciseDetectionResult(
            feedback = ExerciseFeedback.EXPERIMENTAL_DETECTOR,
            debugInfo = "TODO: implement and calibrate ${exerciseType.name} detection",
        ),
    )
    private var isRunning = false

    final override val events: SharedFlow<ExerciseEvent> = mutableEvents.asSharedFlow()
    final override val result: StateFlow<ExerciseDetectionResult> = mutableResult.asStateFlow()

    final override fun start() {
        isRunning = true
        mutableResult.value = experimentalResult(ExerciseFeedback.STAND_IN_FRAME)
    }

    final override fun stop() {
        isRunning = false
        mutableResult.value = experimentalResult(ExerciseFeedback.DETECTOR_STOPPED)
    }

    final override fun reset() {
        mutableResult.value = experimentalResult(ExerciseFeedback.STAND_IN_FRAME)
    }

    final override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning) return
        mutableResult.value = when (frame.trackingState) {
            PoseTrackingState.TRACKING -> experimentalResult(ExerciseFeedback.POSE_TRACKED)
            PoseTrackingState.INITIALIZING -> experimentalResult(ExerciseFeedback.TRACKING_INITIALIZING)
            PoseTrackingState.NO_PERSON -> experimentalResult(ExerciseFeedback.NO_PERSON)
            PoseTrackingState.ERROR -> experimentalResult(ExerciseFeedback.TRACKING_ERROR)
        }
    }

    private fun experimentalResult(feedback: ExerciseFeedback) = ExerciseDetectionResult(
        feedback = feedback,
        debugInfo = "TODO: implement and calibrate ${exerciseType.name} detection",
    )
}

class PushUpDetector : ExperimentalExerciseDetector(ExerciseType.PUSH_UP)

class PullUpDetector : ExperimentalExerciseDetector(ExerciseType.PULL_UP)

class CrunchDetector : ExperimentalExerciseDetector(ExerciseType.CRUNCH)

class LungeDetector : ExperimentalExerciseDetector(ExerciseType.LUNGE)

class JumpingJackDetector : ExperimentalExerciseDetector(ExerciseType.JUMPING_JACK)

class PlankDetector : ExperimentalExerciseDetector(ExerciseType.PLANK)
