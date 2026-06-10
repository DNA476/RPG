package com.example.rpg.pose

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Adapter around MediaPipe Pose Landmarker.
 * It converts image frames to domain pose frames and keeps MediaPipe details out of game and UI logic.
 */
class PoseAnalyzer(
    context: Context,
    private val configuration: PoseAnalyzerConfiguration = PoseAnalyzerConfiguration(),
) : AutoCloseable {
    private val isProcessingFrame = AtomicBoolean(false)
    private val appContext = context.applicationContext
    private val mutablePoseFrame = MutableStateFlow(
        PoseFrame(
            timestampMs = 0L,
            landmarks = emptyList(),
            trackingState = PoseTrackingState.INITIALIZING,
            imageWidth = 0,
            imageHeight = 0,
        ),
    )
    private val poseLandmarker: PoseLandmarker = createPoseLandmarker()

    val poseFrame: StateFlow<PoseFrame> = mutablePoseFrame.asStateFlow()

    /**
     * Runs asynchronous pose detection for one frame.
     */
    fun analyze(bitmap: Bitmap, timestampMs: Long = SystemClock.uptimeMillis()): Boolean {
        if (!isProcessingFrame.compareAndSet(false, true)) {
            return false
        }

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            poseLandmarker.detectAsync(mpImage, timestampMs)
        } catch (_: Exception) {
            mutablePoseFrame.value = mutablePoseFrame.value.copy(trackingState = PoseTrackingState.ERROR)
            isProcessingFrame.set(false)
            return false
        }
        return true
    }

    override fun close() {
        poseLandmarker.close()
    }

    private fun createPoseLandmarker(): PoseLandmarker {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(configuration.modelAssetPath)
            .build()
        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumPoses(configuration.numPoses)
            .setMinPoseDetectionConfidence(configuration.minPoseDetectionConfidence)
            .setMinPosePresenceConfidence(configuration.minPosePresenceConfidence)
            .setMinTrackingConfidence(configuration.minTrackingConfidence)
            .setResultListener(::onPoseResult)
            .setErrorListener(::onPoseError)
            .build()
        return PoseLandmarker.createFromOptions(appContext, options)
    }

    private fun onPoseResult(result: PoseLandmarkerResult, input: com.google.mediapipe.framework.image.MPImage) {
        val firstPose = result.landmarks().firstOrNull()
        val landmarks = firstPose?.mapIndexedNotNull { index, landmark ->
            val name = BodyLandmarkName.fromMediaPipeIndex(index) ?: return@mapIndexedNotNull null
            BodyLandmark(
                name = name,
                x = landmark.x(),
                y = landmark.y(),
                z = landmark.z(),
                visibility = landmark.visibility().orElse(0f),
                presence = landmark.presence().orElse(0f),
            )
        }.orEmpty()

        mutablePoseFrame.value = PoseFrame(
            timestampMs = result.timestampMs(),
            landmarks = landmarks,
            trackingState = if (landmarks.isEmpty()) PoseTrackingState.NO_PERSON else PoseTrackingState.TRACKING,
            imageWidth = input.width,
            imageHeight = input.height,
        )
        isProcessingFrame.set(false)
    }

    private fun onPoseError(error: RuntimeException) {
        mutablePoseFrame.value = mutablePoseFrame.value.copy(trackingState = PoseTrackingState.ERROR)
        isProcessingFrame.set(false)
    }

}
