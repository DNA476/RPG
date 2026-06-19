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

/** Shared event and lifecycle plumbing for experimental movement state machines. */
abstract class ExperimentalRepetitionDetector(
    final override val exerciseType: ExerciseType,
    eventBufferCapacity: Int,
) : ExerciseDetector {
    private val mutableEvents = MutableSharedFlow<ExerciseEvent>(
        extraBufferCapacity = eventBufferCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableResult = MutableStateFlow(
        ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME),
    )
    private var isRunning = false
    protected var repetitionCount = 0
        private set

    final override val events: SharedFlow<ExerciseEvent> = mutableEvents.asSharedFlow()
    final override val result: StateFlow<ExerciseDetectionResult> = mutableResult.asStateFlow()

    final override fun start() {
        isRunning = true
    }

    final override fun stop() {
        isRunning = false
        publish(ExerciseFeedback.DETECTOR_STOPPED)
    }

    final override fun reset() {
        repetitionCount = 0
        resetMovementState()
        publish(ExerciseFeedback.STAND_IN_FRAME)
    }

    final override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning) return
        when (frame.trackingState) {
            PoseTrackingState.TRACKING -> processTrackingFrame(frame)
            PoseTrackingState.INITIALIZING -> publish(ExerciseFeedback.TRACKING_INITIALIZING)
            PoseTrackingState.NO_PERSON -> publish(ExerciseFeedback.NO_PERSON)
            PoseTrackingState.ERROR -> publish(ExerciseFeedback.TRACKING_ERROR)
        }
    }

    protected abstract fun processTrackingFrame(frame: PoseFrame)

    protected abstract fun resetMovementState()

    protected fun movementStarted(feedback: ExerciseFeedback, confidence: Float, debugInfo: String) {
        publish(feedback, confidence, debugInfo)
        mutableEvents.tryEmit(ExerciseEvent.ExerciseStarted(exerciseType))
    }

    protected fun repetitionCompleted(confidence: Float, debugInfo: String) {
        repetitionCount += 1
        publish(
            feedback = ExerciseFeedback.REPETITION_COUNTED,
            confidence = confidence,
            debugInfo = "$debugInfo; repetitions=$repetitionCount",
            repetitionCompleted = true,
        )
        mutableEvents.tryEmit(ExerciseEvent.RepetitionCompleted(exerciseType, repetitionCount))
        mutableEvents.tryEmit(ExerciseEvent.ExerciseFinished(exerciseType))
    }

    protected fun publish(
        feedback: ExerciseFeedback,
        confidence: Float = 0f,
        debugInfo: String? = null,
        repetitionCompleted: Boolean = false,
    ) {
        mutableResult.value = ExerciseDetectionResult(
            repetitionCompleted = repetitionCompleted,
            feedback = feedback,
            confidence = confidence,
            debugInfo = debugInfo,
        )
    }
}

class PullUpDetector(
    private val config: PullUpDetectorConfig = PullUpDetectorConfig(),
) : ExperimentalRepetitionDetector(ExerciseType.PULL_UP, config.eventBufferCapacity) {
    private enum class Phase { WAITING_FOR_EXTENDED, EXTENDED, FLEXED }

    private var phase = Phase.WAITING_FOR_EXTENDED

    override fun resetMovementState() {
        phase = Phase.WAITING_FOR_EXTENDED
    }

    override fun processTrackingFrame(frame: PoseFrame) {
        val metrics = ArmMetrics.from(frame, config.minLandmarkVisibility)
        if (metrics == null) {
            publish(ExerciseFeedback.STAND_IN_FRAME, debugInfo = "Shoulders, elbows, or wrists are not visible")
            return
        }
        val isExtended = metrics.elbowAngle >= config.extendedElbowAngleDegrees && metrics.wristsAboveShoulders
        val isFlexed = metrics.elbowAngle <= config.flexedElbowAngleDegrees && metrics.wristsAboveShoulders

        when (phase) {
            Phase.WAITING_FOR_EXTENDED -> {
                publish(
                    if (isExtended) ExerciseFeedback.POSE_TRACKED else ExerciseFeedback.EXTEND_ARMS,
                    metrics.confidence,
                    metrics.debugInfo,
                )
                if (isExtended) phase = Phase.EXTENDED
            }
            Phase.EXTENDED -> if (isFlexed) {
                phase = Phase.FLEXED
                movementStarted(ExerciseFeedback.TARGET_REACHED, metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.PULL_HIGHER, metrics.confidence, metrics.debugInfo)
            }
            Phase.FLEXED -> if (isExtended) {
                phase = Phase.EXTENDED
                repetitionCompleted(metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.EXTEND_ARMS, metrics.confidence, metrics.debugInfo)
            }
        }
    }
}

data class PullUpDetectorConfig(
    val extendedElbowAngleDegrees: Float = 150f,
    val flexedElbowAngleDegrees: Float = 105f,
    val minLandmarkVisibility: Float = 0.5f,
    val eventBufferCapacity: Int = 8,
)

class CrunchDetector(
    private val config: CrunchDetectorConfig = CrunchDetectorConfig(),
) : ExperimentalRepetitionDetector(ExerciseType.CRUNCH, config.eventBufferCapacity) {
    private enum class Phase { CALIBRATING, EXTENDED, CONTRACTED }

    private var phase = Phase.CALIBRATING
    private var baselineTorsoAngle: Float? = null

    override fun resetMovementState() {
        phase = Phase.CALIBRATING
        baselineTorsoAngle = null
    }

    override fun processTrackingFrame(frame: PoseFrame) {
        val metrics = CrunchMetrics.from(frame, config.minLandmarkVisibility)
        if (metrics == null || metrics.kneeAngle > config.maximumSetupKneeAngleDegrees) {
            publish(ExerciseFeedback.STAND_IN_FRAME, debugInfo = metrics?.debugInfo ?: "Torso and legs are not visible")
            return
        }

        val baseline = maxOf(baselineTorsoAngle ?: metrics.torsoThighAngle, metrics.torsoThighAngle)
        baselineTorsoAngle = baseline
        val isContracted = metrics.torsoThighAngle <= baseline - config.minimumCurlDegrees
        val isReturned = metrics.torsoThighAngle >= baseline - config.returnToleranceDegrees

        when (phase) {
            Phase.CALIBRATING -> {
                phase = Phase.EXTENDED
                publish(ExerciseFeedback.POSE_TRACKED, metrics.confidence, metrics.debugInfo)
            }
            Phase.EXTENDED -> if (isContracted) {
                phase = Phase.CONTRACTED
                movementStarted(ExerciseFeedback.TARGET_REACHED, metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.CURL_UP, metrics.confidence, metrics.debugInfo)
            }
            Phase.CONTRACTED -> if (isReturned) {
                phase = Phase.EXTENDED
                repetitionCompleted(metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.RETURN_TO_STANCE, metrics.confidence, metrics.debugInfo)
            }
        }
    }
}

data class CrunchDetectorConfig(
    val minimumCurlDegrees: Float = 22f,
    val returnToleranceDegrees: Float = 8f,
    val maximumSetupKneeAngleDegrees: Float = 155f,
    val minLandmarkVisibility: Float = 0.5f,
    val eventBufferCapacity: Int = 8,
)

class JumpingJackDetector(
    private val config: JumpingJackDetectorConfig = JumpingJackDetectorConfig(),
) : ExperimentalRepetitionDetector(ExerciseType.JUMPING_JACK, config.eventBufferCapacity) {
    private enum class Phase { WAITING_FOR_CLOSED, CLOSED, OPEN }

    private var phase = Phase.WAITING_FOR_CLOSED

    override fun resetMovementState() {
        phase = Phase.WAITING_FOR_CLOSED
    }

    override fun processTrackingFrame(frame: PoseFrame) {
        val metrics = JumpingJackMetrics.from(frame, config.minLandmarkVisibility)
        if (metrics == null) {
            publish(ExerciseFeedback.STAND_IN_FRAME, debugInfo = "Shoulders, wrists, and ankles are not visible")
            return
        }
        val isClosed = metrics.ankleToShoulderWidthRatio <= config.closedFeetRatio &&
            metrics.wristsBelowShoulders
        val isOpen = metrics.ankleToShoulderWidthRatio >= config.openFeetRatio &&
            metrics.wristsAboveShoulders

        when (phase) {
            Phase.WAITING_FOR_CLOSED -> {
                publish(
                    if (isClosed) ExerciseFeedback.POSE_TRACKED else ExerciseFeedback.CLOSE_ARMS_AND_LEGS,
                    metrics.confidence,
                    metrics.debugInfo,
                )
                if (isClosed) phase = Phase.CLOSED
            }
            Phase.CLOSED -> if (isOpen) {
                phase = Phase.OPEN
                movementStarted(ExerciseFeedback.TARGET_REACHED, metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.OPEN_ARMS_AND_LEGS, metrics.confidence, metrics.debugInfo)
            }
            Phase.OPEN -> if (isClosed) {
                phase = Phase.CLOSED
                repetitionCompleted(metrics.confidence, metrics.debugInfo)
            } else {
                publish(ExerciseFeedback.CLOSE_ARMS_AND_LEGS, metrics.confidence, metrics.debugInfo)
            }
        }
    }
}

data class JumpingJackDetectorConfig(
    val closedFeetRatio: Float = 1.35f,
    val openFeetRatio: Float = 1.8f,
    val minLandmarkVisibility: Float = 0.5f,
    val eventBufferCapacity: Int = 8,
)

private data class ArmMetrics(
    val elbowAngle: Float,
    val wristsAboveShoulders: Boolean,
    val confidence: Float,
) {
    val debugInfo = "elbowAngle=$elbowAngle; wristsAboveShoulders=$wristsAboveShoulders"

    companion object {
        fun from(frame: PoseFrame, minVisibility: Float): ArmMetrics? {
            val sides = listOfNotNull(
                armSide(frame, true, minVisibility),
                armSide(frame, false, minVisibility),
            )
            if (sides.isEmpty()) return null
            return ArmMetrics(
                elbowAngle = sides.map { it.angle }.average().toFloat(),
                wristsAboveShoulders = sides.count { it.wrist.y < it.shoulder.y } >= (sides.size + 1) / 2,
                confidence = sides.flatMap { listOf(it.shoulder, it.elbow, it.wrist) }.confidence(),
            )
        }
    }
}

private data class ArmSide(
    val shoulder: BodyLandmark,
    val elbow: BodyLandmark,
    val wrist: BodyLandmark,
    val angle: Float,
)

private fun armSide(frame: PoseFrame, left: Boolean, minVisibility: Float): ArmSide? {
    val shoulder = frame.visibleLandmark(
        if (left) BodyLandmarkName.LEFT_SHOULDER else BodyLandmarkName.RIGHT_SHOULDER,
        minVisibility,
    ) ?: return null
    val elbow = frame.visibleLandmark(
        if (left) BodyLandmarkName.LEFT_ELBOW else BodyLandmarkName.RIGHT_ELBOW,
        minVisibility,
    ) ?: return null
    val wrist = frame.visibleLandmark(
        if (left) BodyLandmarkName.LEFT_WRIST else BodyLandmarkName.RIGHT_WRIST,
        minVisibility,
    ) ?: return null
    return ArmSide(
        shoulder = shoulder,
        elbow = elbow,
        wrist = wrist,
        angle = angleDegrees(shoulder, elbow, wrist) ?: return null,
    )
}

private data class CrunchMetrics(
    val torsoThighAngle: Float,
    val kneeAngle: Float,
    val confidence: Float,
) {
    val debugInfo = "torsoThighAngle=$torsoThighAngle; kneeAngle=$kneeAngle"

    companion object {
        fun from(frame: PoseFrame, minVisibility: Float): CrunchMetrics? {
            val shoulder = frame.averageVisible(
                BodyLandmarkName.LEFT_SHOULDER,
                BodyLandmarkName.RIGHT_SHOULDER,
                minVisibility,
            ) ?: return null
            val hip = frame.averageVisible(
                BodyLandmarkName.LEFT_HIP,
                BodyLandmarkName.RIGHT_HIP,
                minVisibility,
            ) ?: return null
            val knee = frame.averageVisible(
                BodyLandmarkName.LEFT_KNEE,
                BodyLandmarkName.RIGHT_KNEE,
                minVisibility,
            ) ?: return null
            val ankle = frame.averageVisible(
                BodyLandmarkName.LEFT_ANKLE,
                BodyLandmarkName.RIGHT_ANKLE,
                minVisibility,
            ) ?: return null
            return CrunchMetrics(
                torsoThighAngle = angleDegrees(shoulder, hip, knee) ?: return null,
                kneeAngle = angleDegrees(hip, knee, ankle) ?: return null,
                confidence = listOf(shoulder, hip, knee, ankle).confidence(),
            )
        }
    }
}

private data class JumpingJackMetrics(
    val ankleToShoulderWidthRatio: Float,
    val wristsAboveShoulders: Boolean,
    val wristsBelowShoulders: Boolean,
    val confidence: Float,
) {
    val debugInfo =
        "ankleToShoulderWidthRatio=$ankleToShoulderWidthRatio; wristsAbove=$wristsAboveShoulders; wristsBelow=$wristsBelowShoulders"

    companion object {
        fun from(frame: PoseFrame, minVisibility: Float): JumpingJackMetrics? {
            val leftShoulder = frame.visibleLandmark(BodyLandmarkName.LEFT_SHOULDER, minVisibility) ?: return null
            val rightShoulder = frame.visibleLandmark(BodyLandmarkName.RIGHT_SHOULDER, minVisibility) ?: return null
            val leftWrist = frame.visibleLandmark(BodyLandmarkName.LEFT_WRIST, minVisibility) ?: return null
            val rightWrist = frame.visibleLandmark(BodyLandmarkName.RIGHT_WRIST, minVisibility) ?: return null
            val leftAnkle = frame.visibleLandmark(BodyLandmarkName.LEFT_ANKLE, minVisibility) ?: return null
            val rightAnkle = frame.visibleLandmark(BodyLandmarkName.RIGHT_ANKLE, minVisibility) ?: return null
            val shoulderWidth = kotlin.math.abs(leftShoulder.x - rightShoulder.x)
            if (shoulderWidth < 0.01f) return null
            val shoulderY = (leftShoulder.y + rightShoulder.y) / 2f
            val landmarks = listOf(leftShoulder, rightShoulder, leftWrist, rightWrist, leftAnkle, rightAnkle)
            return JumpingJackMetrics(
                ankleToShoulderWidthRatio = kotlin.math.abs(leftAnkle.x - rightAnkle.x) / shoulderWidth,
                wristsAboveShoulders = leftWrist.y < shoulderY && rightWrist.y < shoulderY,
                wristsBelowShoulders = leftWrist.y > shoulderY + 0.1f && rightWrist.y > shoulderY + 0.1f,
                confidence = landmarks.confidence(),
            )
        }
    }
}
