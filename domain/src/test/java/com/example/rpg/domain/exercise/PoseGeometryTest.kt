package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import org.junit.Assert.assertEquals
import org.junit.Test

class PoseGeometryTest {
    @Test
    fun angleUsesDepthForFrontFacingMovement() {
        val middle = landmark(0f, 0f, 0f)
        val first = landmark(0f, -1f, 0f)
        val last = landmark(0f, 0f, 1f)

        assertEquals(90f, angleDegrees(first, middle, last) ?: 0f, 0.01f)
    }

    @Test
    fun anglePrefersWorldCoordinatesWhenMediaPipeProvidesThem() {
        val middle = landmark(0f, 0f, 0f, worldX = 0f, worldY = 0f, worldZ = 0f)
        val first = landmark(0f, -1f, 0f, worldX = 0f, worldY = -1f, worldZ = 0f)
        val last = landmark(0f, 1f, 0f, worldX = 0f, worldY = 0f, worldZ = 1f)

        assertEquals(90f, angleDegrees(first, middle, last) ?: 0f, 0.01f)
    }

    private fun landmark(
        x: Float,
        y: Float,
        z: Float,
        worldX: Float? = null,
        worldY: Float? = null,
        worldZ: Float? = null,
    ) = BodyLandmark(
        name = BodyLandmarkName.LEFT_ELBOW,
        x = x,
        y = y,
        z = z,
        visibility = 1f,
        presence = 1f,
        worldX = worldX,
        worldY = worldY,
        worldZ = worldZ,
    )
}
