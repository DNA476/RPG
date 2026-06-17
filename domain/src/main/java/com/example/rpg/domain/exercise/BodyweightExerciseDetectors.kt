package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class PushUpDetector(
    private val config: PushUpDetectorConfig = PushUpDetectorConfig(),
) : ExerciseDetector {
    private enum class Phase { WAITING_FOR_TOP, TOP, BOTTOM }

    private val mutableEvents = MutableSharedFlow<ExerciseEvent>(
        extraBufferCapacity = config.eventBufferCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableResult = MutableStateFlow(
        ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME),
    )
    private var isRunning = false
    private var phase = Phase.WAITING_FOR_TOP
    private var repetitionCount = 0

    override val exerciseType: ExerciseType = ExerciseType.PUSH_UP
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
        phase = Phase.WAITING_FOR_TOP
        repetitionCount = 0
        mutableResult.value = ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME)
    }

    override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning || frame.trackingState != PoseTrackingState.TRACKING) return

        val metrics = PushUpMetrics.from(frame, config)
        if (metrics == null) {
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.STAND_IN_FRAME,
                debugInfo = "Required shoulder, elbow, wrist, hip, and ankle landmarks are not visible",
            )
            return
        }

        val hasStraightBody = metrics.bodyAngle >= config.minBodyLineAngleDegrees &&
            metrics.bodyVerticalRange <= config.maxBodyVerticalRange
        val isTop = hasStraightBody && metrics.elbowAngle >= config.topElbowAngleDegrees
        val isBottom = hasStraightBody && metrics.elbowAngle <= config.bottomElbowAngleDegrees

        when (phase) {
            Phase.WAITING_FOR_TOP -> {
                mutableResult.value = resultFor(
                    feedback = if (isTop) ExerciseFeedback.POSE_TRACKED else ExerciseFeedback.STAND_IN_FRAME,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
                if (isTop) phase = Phase.TOP
            }
            Phase.TOP -> if (isBottom) {
                phase = Phase.BOTTOM
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.BOTTOM_REACHED,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
                mutableEvents.tryEmit(ExerciseEvent.ExerciseStarted(exerciseType))
            } else {
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.LOWER_MORE,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
            }
            Phase.BOTTOM -> if (isTop) {
                repetitionCount += 1
                phase = Phase.TOP
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.REPETITION_COUNTED,
                    confidence = metrics.confidence,
                    debugInfo = "${metrics.debugInfo}; repetitions=$repetitionCount",
                    repetitionCompleted = true,
                )
                mutableEvents.tryEmit(ExerciseEvent.RepetitionCompleted(exerciseType, repetitionCount))
                mutableEvents.tryEmit(ExerciseEvent.ExerciseFinished(exerciseType))
            } else {
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.RETURN_TO_STANCE,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
            }
        }
    }

    private fun resultFor(
        feedback: ExerciseFeedback,
        confidence: Float,
        debugInfo: String,
        repetitionCompleted: Boolean = false,
    ) = ExerciseDetectionResult(
        repetitionCompleted = repetitionCompleted,
        feedback = feedback,
        confidence = confidence,
        debugInfo = debugInfo,
    )
}

data class PushUpDetectorConfig(
    val topElbowAngleDegrees: Float = 155f,
    val bottomElbowAngleDegrees: Float = 105f,
    val minBodyLineAngleDegrees: Float = 145f,
    val maxBodyVerticalRange: Float = 0.35f,
    val minLandmarkVisibility: Float = 0.55f,
    val eventBufferCapacity: Int = 8,
)

class LungeDetector(
    private val config: LungeDetectorConfig = LungeDetectorConfig(),
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

    override val exerciseType: ExerciseType = ExerciseType.LUNGE
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
        phase = Phase.WAITING_FOR_STAND
        repetitionCount = 0
        mutableResult.value = ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME)
    }

    override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning || frame.trackingState != PoseTrackingState.TRACKING) return

        val metrics = LungeMetrics.from(frame, config)
        if (metrics == null) {
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.KNEES_NOT_VISIBLE,
                debugInfo = "Required hip, knee, and ankle landmarks are not visible",
            )
            return
        }

        val isStanding = metrics.leftKneeAngle >= config.standingKneeAngleDegrees &&
            metrics.rightKneeAngle >= config.standingKneeAngleDegrees
        val isBottom = metrics.minimumKneeAngle <= config.lungeKneeAngleDegrees &&
            metrics.maximumKneeAngle >= config.supportLegKneeAngleDegrees &&
            metrics.ankleSpread >= config.minAnkleSpread

        when (phase) {
            Phase.WAITING_FOR_STAND -> {
                mutableResult.value = resultFor(
                    feedback = if (isStanding) ExerciseFeedback.POSE_TRACKED else ExerciseFeedback.STRAIGHTEN_LEGS,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
                if (isStanding) phase = Phase.STANDING
            }
            Phase.STANDING -> if (isBottom) {
                phase = Phase.BOTTOM
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.BOTTOM_REACHED,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
                mutableEvents.tryEmit(ExerciseEvent.ExerciseStarted(exerciseType))
            } else {
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.LOWER_MORE,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
            }
            Phase.BOTTOM -> if (isStanding) {
                repetitionCount += 1
                phase = Phase.STANDING
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.REPETITION_COUNTED,
                    confidence = metrics.confidence,
                    debugInfo = "${metrics.debugInfo}; repetitions=$repetitionCount",
                    repetitionCompleted = true,
                )
                mutableEvents.tryEmit(ExerciseEvent.RepetitionCompleted(exerciseType, repetitionCount))
                mutableEvents.tryEmit(ExerciseEvent.ExerciseFinished(exerciseType))
            } else {
                mutableResult.value = resultFor(
                    feedback = ExerciseFeedback.RETURN_TO_STANCE,
                    confidence = metrics.confidence,
                    debugInfo = metrics.debugInfo,
                )
            }
        }
    }

    private fun resultFor(
        feedback: ExerciseFeedback,
        confidence: Float,
        debugInfo: String,
        repetitionCompleted: Boolean = false,
    ) = ExerciseDetectionResult(
        repetitionCompleted = repetitionCompleted,
        feedback = feedback,
        confidence = confidence,
        debugInfo = debugInfo,
    )
}

data class LungeDetectorConfig(
    val standingKneeAngleDegrees: Float = 155f,
    val lungeKneeAngleDegrees: Float = 115f,
    val supportLegKneeAngleDegrees: Float = 125f,
    val minAnkleSpread: Float = 0.18f,
    val minLandmarkVisibility: Float = 0.55f,
    val eventBufferCapacity: Int = 8,
)

class PlankDetector(
    private val config: PlankDetectorConfig = PlankDetectorConfig(),
) : ExerciseDetector {
    private val mutableEvents = MutableSharedFlow<ExerciseEvent>(
        extraBufferCapacity = config.eventBufferCapacity,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val mutableResult = MutableStateFlow(
        ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME),
    )
    private var isRunning = false
    private var holdStartMs: Long? = null
    private var awardedIntervals = 0
    private var repetitionCount = 0

    override val exerciseType: ExerciseType = ExerciseType.PLANK
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
        holdStartMs = null
        awardedIntervals = 0
        repetitionCount = 0
        mutableResult.value = ExerciseDetectionResult(feedback = ExerciseFeedback.STAND_IN_FRAME)
    }

    override fun processPoseFrame(frame: PoseFrame) {
        if (!isRunning || frame.trackingState != PoseTrackingState.TRACKING) return

        val metrics = PlankMetrics.from(frame, config)
        if (metrics == null) {
            clearHold()
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.STAND_IN_FRAME,
                debugInfo = "Required shoulder, hip, and ankle landmarks are not visible",
            )
            return
        }

        val isPlank = metrics.bodyAngle >= config.minBodyLineAngleDegrees &&
            metrics.bodyVerticalRange <= config.maxBodyVerticalRange
        if (!isPlank) {
            clearHold()
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.STAND_IN_FRAME,
                confidence = metrics.confidence,
                debugInfo = metrics.debugInfo,
            )
            return
        }

        val startMs = holdStartMs
        if (startMs == null) {
            holdStartMs = frame.timestampMs
            awardedIntervals = 0
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.POSE_TRACKED,
                confidence = metrics.confidence,
                debugInfo = "${metrics.debugInfo}; holdMs=0",
            )
            mutableEvents.tryEmit(ExerciseEvent.ExerciseStarted(exerciseType))
            return
        }

        val heldMs = (frame.timestampMs - startMs).coerceAtLeast(0L)
        val completedIntervals = (heldMs / config.damageIntervalMs).toInt()
        if (completedIntervals > awardedIntervals) {
            awardedIntervals = completedIntervals
            repetitionCount += 1
            mutableResult.value = ExerciseDetectionResult(
                repetitionCompleted = true,
                feedback = ExerciseFeedback.REPETITION_COUNTED,
                confidence = metrics.confidence,
                debugInfo = "${metrics.debugInfo}; holdMs=$heldMs; repetitions=$repetitionCount",
            )
            mutableEvents.tryEmit(
                ExerciseEvent.RepetitionCompleted(
                    exerciseType = exerciseType,
                    repetitionCount = repetitionCount,
                    activeSeconds = config.damageIntervalSeconds,
                ),
            )
        } else {
            mutableResult.value = ExerciseDetectionResult(
                feedback = ExerciseFeedback.POSE_TRACKED,
                confidence = metrics.confidence,
                debugInfo = "${metrics.debugInfo}; holdMs=$heldMs",
            )
        }
    }

    private fun clearHold() {
        if (holdStartMs != null) {
            mutableEvents.tryEmit(ExerciseEvent.ExerciseFinished(exerciseType))
        }
        holdStartMs = null
        awardedIntervals = 0
    }
}

data class PlankDetectorConfig(
    val damageIntervalMs: Long = 3_000L,
    val minBodyLineAngleDegrees: Float = 160f,
    val maxBodyVerticalRange: Float = 0.25f,
    val minLandmarkVisibility: Float = 0.55f,
    val eventBufferCapacity: Int = 8,
) {
    val damageIntervalSeconds: Int = (damageIntervalMs / 1_000L).toInt().coerceAtLeast(1)
}

private data class PushUpMetrics(
    val elbowAngle: Float,
    val bodyAngle: Float,
    val bodyVerticalRange: Float,
    val confidence: Float,
) {
    val debugInfo: String =
        "elbowAngle=$elbowAngle; bodyAngle=$bodyAngle; bodyVerticalRange=$bodyVerticalRange"

    companion object {
        fun from(frame: PoseFrame, config: PushUpDetectorConfig): PushUpMetrics? {
            val left = frame.sideMetrics(
                shoulder = BodyLandmarkName.LEFT_SHOULDER,
                elbow = BodyLandmarkName.LEFT_ELBOW,
                wrist = BodyLandmarkName.LEFT_WRIST,
                hip = BodyLandmarkName.LEFT_HIP,
                ankle = BodyLandmarkName.LEFT_ANKLE,
                minVisibility = config.minLandmarkVisibility,
            )
            val right = frame.sideMetrics(
                shoulder = BodyLandmarkName.RIGHT_SHOULDER,
                elbow = BodyLandmarkName.RIGHT_ELBOW,
                wrist = BodyLandmarkName.RIGHT_WRIST,
                hip = BodyLandmarkName.RIGHT_HIP,
                ankle = BodyLandmarkName.RIGHT_ANKLE,
                minVisibility = config.minLandmarkVisibility,
            )
            val sides = listOfNotNull(left, right)
            if (sides.isEmpty()) return null
            return PushUpMetrics(
                elbowAngle = sides.map { it.elbowAngle }.average().toFloat(),
                bodyAngle = sides.map { it.bodyAngle }.average().toFloat(),
                bodyVerticalRange = sides.minOf { it.bodyVerticalRange },
                confidence = sides.flatMap { it.landmarks }.confidence(),
            )
        }
    }
}

private data class LungeMetrics(
    val leftKneeAngle: Float,
    val rightKneeAngle: Float,
    val ankleSpread: Float,
    val confidence: Float,
) {
    val minimumKneeAngle: Float = minOf(leftKneeAngle, rightKneeAngle)
    val maximumKneeAngle: Float = maxOf(leftKneeAngle, rightKneeAngle)
    val debugInfo: String =
        "leftKneeAngle=$leftKneeAngle; rightKneeAngle=$rightKneeAngle; ankleSpread=$ankleSpread"

    companion object {
        fun from(frame: PoseFrame, config: LungeDetectorConfig): LungeMetrics? {
            val leftHip = frame.visibleLandmark(BodyLandmarkName.LEFT_HIP, config.minLandmarkVisibility)
            val leftKnee = frame.visibleLandmark(BodyLandmarkName.LEFT_KNEE, config.minLandmarkVisibility)
            val leftAnkle = frame.visibleLandmark(BodyLandmarkName.LEFT_ANKLE, config.minLandmarkVisibility)
            val rightHip = frame.visibleLandmark(BodyLandmarkName.RIGHT_HIP, config.minLandmarkVisibility)
            val rightKnee = frame.visibleLandmark(BodyLandmarkName.RIGHT_KNEE, config.minLandmarkVisibility)
            val rightAnkle = frame.visibleLandmark(BodyLandmarkName.RIGHT_ANKLE, config.minLandmarkVisibility)
            if (
                leftHip == null || leftKnee == null || leftAnkle == null ||
                rightHip == null || rightKnee == null || rightAnkle == null
            ) {
                return null
            }
            return LungeMetrics(
                leftKneeAngle = angleDegrees(leftHip, leftKnee, leftAnkle) ?: return null,
                rightKneeAngle = angleDegrees(rightHip, rightKnee, rightAnkle) ?: return null,
                ankleSpread = maxOf(abs(leftAnkle.x - rightAnkle.x), abs(leftAnkle.y - rightAnkle.y)),
                confidence = listOf(leftHip, leftKnee, leftAnkle, rightHip, rightKnee, rightAnkle).confidence(),
            )
        }
    }
}

private data class PlankMetrics(
    val bodyAngle: Float,
    val bodyVerticalRange: Float,
    val confidence: Float,
) {
    val debugInfo: String = "bodyAngle=$bodyAngle; bodyVerticalRange=$bodyVerticalRange"

    companion object {
        fun from(frame: PoseFrame, config: PlankDetectorConfig): PlankMetrics? {
            val shoulder = frame.averageVisible(
                BodyLandmarkName.LEFT_SHOULDER,
                BodyLandmarkName.RIGHT_SHOULDER,
                config.minLandmarkVisibility,
            ) ?: return null
            val hip = frame.averageVisible(
                BodyLandmarkName.LEFT_HIP,
                BodyLandmarkName.RIGHT_HIP,
                config.minLandmarkVisibility,
            ) ?: return null
            val ankle = frame.averageVisible(
                BodyLandmarkName.LEFT_ANKLE,
                BodyLandmarkName.RIGHT_ANKLE,
                config.minLandmarkVisibility,
            ) ?: return null
            return PlankMetrics(
                bodyAngle = angleDegrees(shoulder, hip, ankle) ?: return null,
                bodyVerticalRange = maxOf(shoulder.y, hip.y, ankle.y) - minOf(shoulder.y, hip.y, ankle.y),
                confidence = listOf(shoulder, hip, ankle).confidence(),
            )
        }
    }
}

private data class SideMetrics(
    val elbowAngle: Float,
    val bodyAngle: Float,
    val bodyVerticalRange: Float,
    val landmarks: List<BodyLandmark>,
)

private fun PoseFrame.sideMetrics(
    shoulder: BodyLandmarkName,
    elbow: BodyLandmarkName,
    wrist: BodyLandmarkName,
    hip: BodyLandmarkName,
    ankle: BodyLandmarkName,
    minVisibility: Float,
): SideMetrics? {
    val shoulderPoint = visibleLandmark(shoulder, minVisibility)
    val elbowPoint = visibleLandmark(elbow, minVisibility)
    val wristPoint = visibleLandmark(wrist, minVisibility)
    val hipPoint = visibleLandmark(hip, minVisibility)
    val anklePoint = visibleLandmark(ankle, minVisibility)
    if (
        shoulderPoint == null || elbowPoint == null || wristPoint == null ||
        hipPoint == null || anklePoint == null
    ) {
        return null
    }
    val bodyYValues = listOf(shoulderPoint.y, hipPoint.y, anklePoint.y)
    return SideMetrics(
        elbowAngle = angleDegrees(shoulderPoint, elbowPoint, wristPoint) ?: return null,
        bodyAngle = angleDegrees(shoulderPoint, hipPoint, anklePoint) ?: return null,
        bodyVerticalRange = bodyYValues.max() - bodyYValues.min(),
        landmarks = listOf(shoulderPoint, elbowPoint, wristPoint, hipPoint, anklePoint),
    )
}

private fun PoseFrame.visibleLandmark(name: BodyLandmarkName, minVisibility: Float): BodyLandmark? =
    landmark(name)?.takeIf { it.visibility >= minVisibility }

private fun PoseFrame.averageVisible(
    first: BodyLandmarkName,
    second: BodyLandmarkName,
    minVisibility: Float,
): BodyLandmark? {
    val firstLandmark = visibleLandmark(first, minVisibility)
    val secondLandmark = visibleLandmark(second, minVisibility)
    val visible = listOfNotNull(firstLandmark, secondLandmark)
    if (visible.isEmpty()) return null
    return BodyLandmark(
        name = visible.first().name,
        x = visible.map { it.x }.average().toFloat(),
        y = visible.map { it.y }.average().toFloat(),
        z = visible.map { it.z }.average().toFloat(),
        visibility = visible.map { it.visibility }.average().toFloat(),
        presence = visible.map { it.presence }.average().toFloat(),
    )
}

private fun angleDegrees(first: BodyLandmark, middle: BodyLandmark, last: BodyLandmark): Float? {
    val firstX = first.x - middle.x
    val firstY = first.y - middle.y
    val lastX = last.x - middle.x
    val lastY = last.y - middle.y
    val dot = firstX * lastX + firstY * lastY
    val firstLength = sqrt(firstX * firstX + firstY * firstY)
    val lastLength = sqrt(lastX * lastX + lastY * lastY)
    if (firstLength == 0f || lastLength == 0f) return null

    val cosine = (dot / (firstLength * lastLength)).coerceIn(-1f, 1f)
    return Math.toDegrees(acos(cosine).toDouble()).toFloat()
}

private fun List<BodyLandmark>.confidence(): Float =
    if (isEmpty()) 0f else map { it.visibility }.average().toFloat()
