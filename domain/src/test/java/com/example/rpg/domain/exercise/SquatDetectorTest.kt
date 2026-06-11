package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

class SquatDetectorTest {
    @Test
    fun countsStandingBottomStandingAsOneRepetition() = runBlocking {
        val detector = SquatDetector()
        detector.start()
        val repetition = async {
            detector.events
                .filterIsInstance<ExerciseEvent.RepetitionCompleted>()
                .first()
        }
        yield()

        detector.processPoseFrame(frame(ankleX = 0f, ankleY = 2f))
        detector.processPoseFrame(frame(ankleX = 1f, ankleY = 1f))
        detector.processPoseFrame(frame(ankleX = 0f, ankleY = 2f))

        assertEquals(1, repetition.await().repetitionCount)
    }

    private fun frame(ankleX: Float, ankleY: Float): PoseFrame {
        val landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_HIP, 0f, 0f),
            landmark(BodyLandmarkName.LEFT_KNEE, 0f, 1f),
            landmark(BodyLandmarkName.LEFT_ANKLE, ankleX, ankleY),
            landmark(BodyLandmarkName.RIGHT_HIP, 0f, 0f),
            landmark(BodyLandmarkName.RIGHT_KNEE, 0f, 1f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, ankleX, ankleY),
        )
        return PoseFrame(
            timestampMs = 0L,
            landmarks = landmarks,
            trackingState = PoseTrackingState.TRACKING,
            imageWidth = 100,
            imageHeight = 100,
        )
    }

    private fun landmark(name: BodyLandmarkName, x: Float, y: Float) = BodyLandmark(
        name = name,
        x = x,
        y = y,
        z = 0f,
        visibility = 1f,
        presence = 1f,
    )
}
