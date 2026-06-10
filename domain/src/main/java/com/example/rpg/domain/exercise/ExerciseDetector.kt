package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.PoseFrame
import kotlinx.coroutines.flow.SharedFlow

/**
 * Generic contract for real-time exercise detectors.
 * Implementations consume pose frames and expose exercise events without depending on UI or game classes.
 */
interface ExerciseDetector {
    val exerciseType: ExerciseType
    val events: SharedFlow<ExerciseEvent>

    /**
     * Enables frame processing.
     */
    fun start()

    /**
     * Pauses frame processing without clearing accumulated counters.
     */
    fun stop()

    /**
     * Clears counters and returns detector to its initial state.
     */
    fun reset()

    /**
     * Processes a single pose frame from the pose module.
     */
    fun processPoseFrame(frame: PoseFrame)
}
