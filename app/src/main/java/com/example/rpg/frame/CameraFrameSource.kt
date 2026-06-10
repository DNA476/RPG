package com.example.rpg.frame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.example.rpg.pose.PoseAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CameraX-backed frame source.
 */
class CameraFrameSource(
    context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val poseAnalyzer: PoseAnalyzer,
    private val mirrorForFrontCamera: Boolean = true,
) : FrameSource {
    private val cameraController = LifecycleCameraController(context)
    private val analyzerExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var isStarted = false

    override fun start() {
        if (isStarted) return

        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        cameraController.setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        cameraController.setImageAnalysisAnalyzer(analyzerExecutor) { imageProxy ->
            try {
                val bitmap = imageProxy.toBitmap()
                    .rotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                    .maybeMirror(mirrorForFrontCamera)
                poseAnalyzer.analyze(bitmap, timestampMs = SystemClock.uptimeMillis())
            } finally {
                imageProxy.close()
            }
        }
        previewView.controller = cameraController
        cameraController.bindToLifecycle(lifecycleOwner)
        isStarted = true
    }

    override fun stop() {
        if (!isStarted) return

        cameraController.clearImageAnalysisAnalyzer()
        cameraController.unbind()
        previewView.controller = null
        isStarted = false
    }

    override fun close() {
        stop()
        analyzerExecutor.shutdown()
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        if (degrees == 0f) return this
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.maybeMirror(enabled: Boolean): Bitmap {
        if (!enabled) return this
        val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, height / 2f) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}
