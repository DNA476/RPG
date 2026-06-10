package com.example.rpg.pose

/**
 * Runtime configuration for MediaPipe Pose Landmarker.
 */
data class PoseAnalyzerConfiguration(
    val modelAssetPath: String = "pose_landmarker_lite.task",
    val minPoseDetectionConfidence: Float = 0.5f,
    val minPosePresenceConfidence: Float = 0.5f,
    val minTrackingConfidence: Float = 0.5f,
    val numPoses: Int = 1,
)
