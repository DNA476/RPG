package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Detects complete squat repetitions from hip-knee-ankle angles.
 * A repetition is counted only after the user stands, squats below the configured knee angle, and stands again.
 */
class SquatDetector(
    private val config: SquatDetectorConfig = SquatDetectorConfig(),
) : ExerciseDetector {
    private enum class Phase { WAITING_FOR_STAND, STANDING, BOTTOM }

    private val mutableEvents = MutableSharedFlow<ExerciseEvent>(
        extraBufferCapacity = config.eventBufferCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableResult = MutableStateFlow(
        ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME),
    )
    private var isRunning = false
    private var phase = Phase.WAITING_FOR_STAND
    private var repetitionCount = 0

    override val exerciseType: ExerciseType = ExerciseType.SQUAT
    override val events: SharedFlow<ExerciseEvent> = mutableEvents.asSharedFlow()
    override val result: StateFlow<ExerciseDetectionResult> = mutableResult.asStateFlow()

    override fun start() {
        isRunning = true
    }

    override fun stop() {
        isRunning = false
        mutableResult.value = mutableResult.value.copy(feedback = ExerciseFeedback.DETECTOR_STOPPED)
    }

    override fun reset() {
        repetitionCount = 0
        phase = Phase.WAITING_FOR_STAND
        mutableResult.value = ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME)
    }

    override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning || frame.trackingState != PoseTrackingState.TRACKING) return

        val kneeAngle = averageVisibleKneeAngle(frame)
        if (kneeAngle == null) {
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.KNEES_NOT_VISIBLE,
                debugInfo = "Required hip, knee, and ankle landmarks are not visible",
            )
            return
        }
        val isStanding = kneeAngle >= config.standingKneeAngleDegrees
        val isAtBottom = kneeAngle <= config.squatKneeAngleDegrees

        when (phase) {
            Phase.WAITING_FOR_STAND -> {
                mutableResult.value = ExerciseDetectionResult(
                    feedback = if (isStanding) {
                        ExerciseFeedback.READY_TO_SQUAT
                    } else {
                        ExerciseFeedback.STRAIGHTEN_LEGS
                    },
                    confidence = frameConfidence(frame),
                    debugInfo = "kneeAngle=$kneeAngle",
                )
                if (isStanding) phase = Phase.STANDING
            }
            Phase.STANDING -> if (isAtBottom) {
                phase = Phase.BOTTOM
                mutableResult.value = ExerciseDetectionResult(
                    feedback = ExerciseFeedback.BOTTOM_REACHED,
                    confidence = frameConfidence(frame),
                    debugInfo = "kneeAngle=$kneeAngle",
                )
                mutableEvents.tryEmit(ExerciseEvent.ExerciseStarted(exerciseType))
            } else {
                mutableResult.value = ExerciseDetectionResult(
                    feedback = ExerciseFeedback.LOWER_MORE,
                    confidence = frameConfidence(frame),
                    debugInfo = "kneeAngle=$kneeAngle",
                )
            }
            Phase.BOTTOM -> if (isStanding) {
                repetitionCount += 1
                phase = Phase.STANDING
                mutableResult.value = ExerciseDetectionResult(
                    repetitionCompleted = true,
                    feedback = ExerciseFeedback.REPETITION_COUNTED,
                    confidence = frameConfidence(frame),
                    debugInfo = "kneeAngle=$kneeAngle; repetitions=$repetitionCount",
                )
                mutableEvents.tryEmit(ExerciseEvent.RepetitionCompleted(exerciseType, repetitionCount))
                mutableEvents.tryEmit(ExerciseEvent.ExerciseFinished(exerciseType))
            } else {
                mutableResult.value = ExerciseDetectionResult(
                    feedback = ExerciseFeedback.RETURN_TO_STANCE,
                    confidence = frameConfidence(frame),
                    debugInfo = "kneeAngle=$kneeAngle",
                )
            }
        }
    }

    private fun frameConfidence(frame: PoseFrame): Float {
        val required = listOf(
            BodyLandmarkName.LEFT_HIP,
            BodyLandmarkName.LEFT_KNEE,
            BodyLandmarkName.LEFT_ANKLE,
            BodyLandmarkName.RIGHT_HIP,
            BodyLandmarkName.RIGHT_KNEE,
            BodyLandmarkName.RIGHT_ANKLE,
        ).mapNotNull(frame::landmark)
        return if (required.isEmpty()) 0f else required.map { it.visibility }.average().toFloat()
    }

    private fun averageVisibleKneeAngle(frame: PoseFrame): Float? {
        val left = kneeAngle(
            frame.landmark(BodyLandmarkName.LEFT_HIP),
            frame.landmark(BodyLandmarkName.LEFT_KNEE),
            frame.landmark(BodyLandmarkName.LEFT_ANKLE),
        )
        val right = kneeAngle(
            frame.landmark(BodyLandmarkName.RIGHT_HIP),
            frame.landmark(BodyLandmarkName.RIGHT_KNEE),
            frame.landmark(BodyLandmarkName.RIGHT_ANKLE),
        )
        val angles = listOfNotNull(left, right)
        return if (angles.isEmpty()) null else angles.average().toFloat()
    }

    private fun kneeAngle(hip: BodyLandmark?, knee: BodyLandmark?, ankle: BodyLandmark?): Float? {
        if (hip == null || knee == null || ankle == null) return null
        if (listOf(hip, knee, ankle).any { it.visibility < config.minLandmarkVisibility }) return null

        return angleDegrees(hip, knee, ankle)
    }
}
