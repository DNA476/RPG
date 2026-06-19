package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import kotlin.math.acos
import kotlin.math.sqrt

internal fun PoseFrame.visibleLandmark(
    name: BodyLandmarkName,
    minVisibility: Float,
): BodyLandmark? = landmark(name)?.takeIf { it.visibility >= minVisibility }

internal fun PoseFrame.averageVisible(
    first: BodyLandmarkName,
    second: BodyLandmarkName,
    minVisibility: Float,
): BodyLandmark? {
    val visible = listOfNotNull(
        visibleLandmark(first, minVisibility),
        visibleLandmark(second, minVisibility),
    )
    if (visible.isEmpty()) return null
    val hasWorldCoordinates = visible.all {
        it.worldX != null && it.worldY != null && it.worldZ != null
    }
    return BodyLandmark(
        name = visible.first().name,
        x = visible.map { it.x }.average().toFloat(),
        y = visible.map { it.y }.average().toFloat(),
        z = visible.map { it.z }.average().toFloat(),
        visibility = visible.map { it.visibility }.average().toFloat(),
        presence = visible.map { it.presence }.average().toFloat(),
        worldX = if (hasWorldCoordinates) visible.mapNotNull { it.worldX }.average().toFloat() else null,
        worldY = if (hasWorldCoordinates) visible.mapNotNull { it.worldY }.average().toFloat() else null,
        worldZ = if (hasWorldCoordinates) visible.mapNotNull { it.worldZ }.average().toFloat() else null,
    )
}

internal fun angleDegrees(
    first: BodyLandmark,
    middle: BodyLandmark,
    last: BodyLandmark,
): Float? {
    val useWorld = listOf(first, middle, last).all { it.hasWorldCoordinates() }
    val firstX = first.coordinateX(useWorld) - middle.coordinateX(useWorld)
    val firstY = first.coordinateY(useWorld) - middle.coordinateY(useWorld)
    val firstZ = first.coordinateZ(useWorld) - middle.coordinateZ(useWorld)
    val lastX = last.coordinateX(useWorld) - middle.coordinateX(useWorld)
    val lastY = last.coordinateY(useWorld) - middle.coordinateY(useWorld)
    val lastZ = last.coordinateZ(useWorld) - middle.coordinateZ(useWorld)
    val dot = firstX * lastX + firstY * lastY + firstZ * lastZ
    val firstLength = sqrt(firstX * firstX + firstY * firstY + firstZ * firstZ)
    val lastLength = sqrt(lastX * lastX + lastY * lastY + lastZ * lastZ)
    if (firstLength == 0f || lastLength == 0f) return null

    val cosine = (dot / (firstLength * lastLength)).coerceIn(-1f, 1f)
    return Math.toDegrees(acos(cosine).toDouble()).toFloat()
}

internal fun distance3d(first: BodyLandmark, second: BodyLandmark): Float {
    val useWorld = first.hasWorldCoordinates() && second.hasWorldCoordinates()
    val x = first.coordinateX(useWorld) - second.coordinateX(useWorld)
    val y = first.coordinateY(useWorld) - second.coordinateY(useWorld)
    val z = first.coordinateZ(useWorld) - second.coordinateZ(useWorld)
    return sqrt(x * x + y * y + z * z)
}

internal fun List<BodyLandmark>.confidence(): Float =
    if (isEmpty()) 0f else map { it.visibility }.average().toFloat()

private fun BodyLandmark.hasWorldCoordinates(): Boolean =
    worldX != null && worldY != null && worldZ != null

private fun BodyLandmark.coordinateX(world: Boolean): Float = if (world) worldX ?: x else x

private fun BodyLandmark.coordinateY(world: Boolean): Float = if (world) worldY ?: y else y

private fun BodyLandmark.coordinateZ(world: Boolean): Float = if (world) worldZ ?: z else z
