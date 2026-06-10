package com.example.rpg.domain.pose

/**
 * Current pose tracking quality from the pose module.
 */
enum class PoseTrackingState {
    INITIALIZING,
    TRACKING,
    NO_PERSON,
    ERROR,
}
