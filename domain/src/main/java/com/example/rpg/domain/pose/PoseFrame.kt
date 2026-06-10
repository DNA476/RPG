package com.example.rpg.domain.pose

/**
 * Domain-level pose snapshot consumed by exercise detectors.
 */
data class PoseFrame(
    val timestampMs: Long,
    val landmarks: List<BodyLandmark>,
    val trackingState: PoseTrackingState,
    val imageWidth: Int,
    val imageHeight: Int,
) {
    /**
     * Returns a landmark by semantic name or null when MediaPipe did not provide it.
     */
    fun landmark(name: BodyLandmarkName): BodyLandmark? = landmarks.firstOrNull { it.name == name }
}
