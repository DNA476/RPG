package com.example.rpg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame

private val skeletonEdges = listOf(
    BodyLandmarkName.LEFT_SHOULDER to BodyLandmarkName.RIGHT_SHOULDER,
    BodyLandmarkName.LEFT_SHOULDER to BodyLandmarkName.LEFT_ELBOW,
    BodyLandmarkName.LEFT_ELBOW to BodyLandmarkName.LEFT_WRIST,
    BodyLandmarkName.RIGHT_SHOULDER to BodyLandmarkName.RIGHT_ELBOW,
    BodyLandmarkName.RIGHT_ELBOW to BodyLandmarkName.RIGHT_WRIST,
    BodyLandmarkName.LEFT_SHOULDER to BodyLandmarkName.LEFT_HIP,
    BodyLandmarkName.RIGHT_SHOULDER to BodyLandmarkName.RIGHT_HIP,
    BodyLandmarkName.LEFT_HIP to BodyLandmarkName.RIGHT_HIP,
    BodyLandmarkName.LEFT_HIP to BodyLandmarkName.LEFT_KNEE,
    BodyLandmarkName.LEFT_KNEE to BodyLandmarkName.LEFT_ANKLE,
    BodyLandmarkName.RIGHT_HIP to BodyLandmarkName.RIGHT_KNEE,
    BodyLandmarkName.RIGHT_KNEE to BodyLandmarkName.RIGHT_ANKLE,
)

/**
 * Draws a lightweight pose skeleton over the camera image using normalized landmarks.
 */
@Composable
fun SkeletonOverlay(
    poseFrame: PoseFrame?,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val landmarks = poseFrame?.landmarks.orEmpty()
        if (landmarks.isEmpty()) return@Canvas
        val lookup = landmarks.associateBy { it.name }
        skeletonEdges.forEach { (from, to) ->
            drawSkeletonLine(lookup[from], lookup[to])
        }
        landmarks.forEach { landmark ->
            if (landmark.visibility >= 0.45f) {
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 5f,
                    center = landmark.toOffset(size.width, size.height),
                )
            }
        }
    }
}

private fun DrawScope.drawSkeletonLine(from: BodyLandmark?, to: BodyLandmark?) {
    if (from == null || to == null) return
    if (from.visibility < 0.45f || to.visibility < 0.45f) return
    drawLine(
        color = Color(0xFF66E3FF),
        start = from.toOffset(size.width, size.height),
        end = to.toOffset(size.width, size.height),
        strokeWidth = 6f,
        cap = StrokeCap.Round,
    )
}

private fun BodyLandmark.toOffset(width: Float, height: Float): Offset = Offset(x * width, y * height)
