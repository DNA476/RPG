package com.example.rpg.domain.pose

/**
 * Normalized joint coordinate emitted by the pose layer.
 * Coordinates are in the image coordinate system: x and y are usually 0..1.
 * World coordinates are optional metric-space values used for view-independent geometry.
 */
data class BodyLandmark(
    val name: BodyLandmarkName,
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float,
    val presence: Float,
    val worldX: Float? = null,
    val worldY: Float? = null,
    val worldZ: Float? = null,
)
