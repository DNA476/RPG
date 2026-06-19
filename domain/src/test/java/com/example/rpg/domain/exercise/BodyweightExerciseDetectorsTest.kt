package com.example.rpg.domain.exercise

import com.example.rpg.domain.pose.BodyLandmark
import com.example.rpg.domain.pose.BodyLandmarkName
import com.example.rpg.domain.pose.PoseFrame
import com.example.rpg.domain.pose.PoseTrackingState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

class BodyweightExerciseDetectorsTest {
    @Test
    fun pushUpCountsTopBottomTopAsOneRepetition() = runBlocking {
        val detector = PushUpDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events
                    .filterIsInstance<ExerciseEvent.RepetitionCompleted>()
                    .first()
            }
        }
        yield()

        detector.processPoseFrame(pushUpFrame(elbowX = 0.2f))
        detector.processPoseFrame(pushUpFrame(elbowX = 0.35f))
        detector.processPoseFrame(pushUpFrame(elbowX = 0.2f))

        assertEquals(ExerciseType.PUSH_UP, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
    }

    @Test
    fun lungeCountsStandingBottomStandingAsOneRepetition() = runBlocking {
        val detector = LungeDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events
                    .filterIsInstance<ExerciseEvent.RepetitionCompleted>()
                    .first()
            }
        }
        yield()

        detector.processPoseFrame(lungeStandingFrame())
        detector.processPoseFrame(lungeBottomFrame())
        detector.processPoseFrame(lungeStandingFrame())

        assertEquals(ExerciseType.LUNGE, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
    }

    @Test
    fun plankCountsOneRepetitionForEachHeldInterval() = runBlocking {
        val detector = PlankDetector(PlankDetectorConfig(damageIntervalMs = 1_000L))
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events
                    .filterIsInstance<ExerciseEvent.RepetitionCompleted>()
                    .first()
            }
        }
        yield()

        detector.processPoseFrame(plankFrame(timestampMs = 0L))
        detector.processPoseFrame(plankFrame(timestampMs = 1_000L))

        assertEquals(ExerciseType.PLANK, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
        assertEquals(1, repetition.await().activeSeconds)
    }

    @Test
    fun pullUpCountsExtendedFlexedExtendedAsOneRepetition() = runBlocking {
        val detector = PullUpDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events.filterIsInstance<ExerciseEvent.RepetitionCompleted>().first()
            }
        }
        yield()

        detector.processPoseFrame(pullUpFrame(flexed = false))
        detector.processPoseFrame(pullUpFrame(flexed = true))
        detector.processPoseFrame(pullUpFrame(flexed = false))

        assertEquals(ExerciseType.PULL_UP, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
    }

    @Test
    fun pushUpCountsKneeSupportedMovementFacingCamera() = runBlocking {
        val detector = PushUpDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events.filterIsInstance<ExerciseEvent.RepetitionCompleted>().first()
            }
        }
        yield()

        detector.processPoseFrame(frontPushUpFrame(elbowXOffset = 0f))
        detector.processPoseFrame(frontPushUpFrame(elbowXOffset = 0.15f))
        detector.processPoseFrame(frontPushUpFrame(elbowXOffset = 0f))

        assertEquals(1, repetition.await().repetitionCount)
    }

    @Test
    fun crunchUsesComfortableStartingAngleAndCountsCurlReturn() = runBlocking {
        val detector = CrunchDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events.filterIsInstance<ExerciseEvent.RepetitionCompleted>().first()
            }
        }
        yield()

        detector.processPoseFrame(crunchFrame(contracted = false))
        detector.processPoseFrame(crunchFrame(contracted = true))
        detector.processPoseFrame(crunchFrame(contracted = false))

        assertEquals(ExerciseType.CRUNCH, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
    }

    @Test
    fun jumpingJackCountsClosedOpenClosedAsOneRepetition() = runBlocking {
        val detector = JumpingJackDetector()
        detector.start()
        val repetition = async {
            withTimeout(1_000L) {
                detector.events.filterIsInstance<ExerciseEvent.RepetitionCompleted>().first()
            }
        }
        yield()

        detector.processPoseFrame(jumpingJackFrame(open = false))
        detector.processPoseFrame(jumpingJackFrame(open = true))
        detector.processPoseFrame(jumpingJackFrame(open = false))

        assertEquals(ExerciseType.JUMPING_JACK, repetition.await().exerciseType)
        assertEquals(1, repetition.await().repetitionCount)
    }

    private fun pushUpFrame(elbowX: Float): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, 0.2f, 0.4f),
            landmark(BodyLandmarkName.LEFT_ELBOW, elbowX, 0.55f),
            landmark(BodyLandmarkName.LEFT_WRIST, 0.2f, 0.7f),
            landmark(BodyLandmarkName.LEFT_HIP, 0.5f, 0.4f),
            landmark(BodyLandmarkName.LEFT_ANKLE, 0.8f, 0.4f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, 0.2f, 0.42f),
            landmark(BodyLandmarkName.RIGHT_ELBOW, elbowX, 0.57f),
            landmark(BodyLandmarkName.RIGHT_WRIST, 0.2f, 0.72f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.5f, 0.42f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, 0.8f, 0.42f),
        ),
    )

    private fun lungeStandingFrame(): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_HIP, 0.4f, 0.1f),
            landmark(BodyLandmarkName.LEFT_KNEE, 0.4f, 0.5f),
            landmark(BodyLandmarkName.LEFT_ANKLE, 0.4f, 0.9f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.6f, 0.1f),
            landmark(BodyLandmarkName.RIGHT_KNEE, 0.6f, 0.5f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, 0.6f, 0.9f),
        ),
    )

    private fun lungeBottomFrame(): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_HIP, 0.25f, 0.45f),
            landmark(BodyLandmarkName.LEFT_KNEE, 0.45f, 0.45f),
            landmark(BodyLandmarkName.LEFT_ANKLE, 0.45f, 0.75f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.55f, 0.25f),
            landmark(BodyLandmarkName.RIGHT_KNEE, 0.65f, 0.55f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, 0.85f, 0.75f),
        ),
    )

    private fun plankFrame(timestampMs: Long): PoseFrame = frame(
        timestampMs = timestampMs,
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, 0.2f, 0.4f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, 0.2f, 0.42f),
            landmark(BodyLandmarkName.LEFT_HIP, 0.5f, 0.4f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.5f, 0.42f),
            landmark(BodyLandmarkName.LEFT_ANKLE, 0.8f, 0.4f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, 0.8f, 0.42f),
        ),
    )

    private fun frontPushUpFrame(elbowXOffset: Float): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, 0.45f, 0.4f, z = 0f),
            landmark(BodyLandmarkName.LEFT_ELBOW, 0.45f + elbowXOffset, 0.55f, z = 0f),
            landmark(BodyLandmarkName.LEFT_WRIST, 0.45f, 0.7f, z = 0f),
            landmark(BodyLandmarkName.LEFT_HIP, 0.45f, 0.4f, z = -0.3f),
            landmark(BodyLandmarkName.LEFT_KNEE, 0.45f, 0.4f, z = -0.6f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, 0.55f, 0.4f, z = 0f),
            landmark(BodyLandmarkName.RIGHT_ELBOW, 0.55f - elbowXOffset, 0.55f, z = 0f),
            landmark(BodyLandmarkName.RIGHT_WRIST, 0.55f, 0.7f, z = 0f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.55f, 0.4f, z = -0.3f),
            landmark(BodyLandmarkName.RIGHT_KNEE, 0.55f, 0.4f, z = -0.6f),
        ),
    )

    private fun pullUpFrame(flexed: Boolean): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, 0.4f, 0.45f),
            landmark(BodyLandmarkName.LEFT_ELBOW, if (flexed) 0.25f else 0.4f, 0.3f),
            landmark(BodyLandmarkName.LEFT_WRIST, 0.4f, 0.1f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, 0.6f, 0.45f),
            landmark(BodyLandmarkName.RIGHT_ELBOW, if (flexed) 0.75f else 0.6f, 0.3f),
            landmark(BodyLandmarkName.RIGHT_WRIST, 0.6f, 0.1f),
        ),
    )

    private fun crunchFrame(contracted: Boolean): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, if (contracted) 0.35f else 0.2f, if (contracted) 0.35f else 0.5f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, if (contracted) 0.37f else 0.22f, if (contracted) 0.35f else 0.5f),
            landmark(BodyLandmarkName.LEFT_HIP, 0.5f, 0.5f),
            landmark(BodyLandmarkName.RIGHT_HIP, 0.52f, 0.5f),
            landmark(BodyLandmarkName.LEFT_KNEE, 0.5f, 0.2f),
            landmark(BodyLandmarkName.RIGHT_KNEE, 0.52f, 0.2f),
            landmark(BodyLandmarkName.LEFT_ANKLE, 0.7f, 0.3f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, 0.72f, 0.3f),
        ),
    )

    private fun jumpingJackFrame(open: Boolean): PoseFrame = frame(
        landmarks = listOf(
            landmark(BodyLandmarkName.LEFT_SHOULDER, 0.4f, 0.4f),
            landmark(BodyLandmarkName.RIGHT_SHOULDER, 0.6f, 0.4f),
            landmark(BodyLandmarkName.LEFT_WRIST, if (open) 0.3f else 0.4f, if (open) 0.2f else 0.75f),
            landmark(BodyLandmarkName.RIGHT_WRIST, if (open) 0.7f else 0.6f, if (open) 0.2f else 0.75f),
            landmark(BodyLandmarkName.LEFT_ANKLE, if (open) 0.2f else 0.46f, 0.9f),
            landmark(BodyLandmarkName.RIGHT_ANKLE, if (open) 0.8f else 0.54f, 0.9f),
        ),
    )

    private fun frame(
        timestampMs: Long = 0L,
        landmarks: List<BodyLandmark>,
    ): PoseFrame = PoseFrame(
        timestampMs = timestampMs,
        landmarks = landmarks,
        trackingState = PoseTrackingState.TRACKING,
        imageWidth = 100,
        imageHeight = 100,
    )

    private fun landmark(name: BodyLandmarkName, x: Float, y: Float, z: Float = 0f) = BodyLandmark(
        name = name,
        x = x,
        y = y,
        z = z,
        visibility = 1f,
        presence = 1f,
    )
}
