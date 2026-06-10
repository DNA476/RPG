package com.example.rpg.frame

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.SystemClock
import com.example.rpg.pose.PoseAnalyzer
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Debug frame source that loops an mp4 from app assets.
 */
class VideoFileFrameSource(
    context: Context,
    private val poseAnalyzer: PoseAnalyzer,
    private val coroutineScope: CoroutineScope,
    private val assetPath: String = DEFAULT_ASSET_PATH,
    private val targetFps: Int = DEFAULT_TARGET_FPS,
    private val onFrame: (Bitmap) -> Unit = {},
    private val onError: (String) -> Unit = {},
) : FrameSource {
    private val appContext = context.applicationContext
    private var decodeJob: Job? = null

    override fun start() {
        if (decodeJob?.isActive == true) return

        decodeJob = coroutineScope.launch(Dispatchers.Default) {
            val frameIntervalMs = (1000L / targetFps).coerceAtLeast(1L)
            while (isActive) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setAssetDataSource(assetPath)
                    val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull()
                        ?: frameIntervalMs
                    var frameTimeMs = 0L

                    while (isActive && frameTimeMs <= durationMs) {
                        val bitmap = retriever.getFrameAtTime(
                            frameTimeMs * 1000L,
                            MediaMetadataRetriever.OPTION_CLOSEST,
                        )
                        if (bitmap != null) {
                            poseAnalyzer.analyze(bitmap, timestampMs = SystemClock.uptimeMillis())
                            withContext(Dispatchers.Main) {
                                onFrame(bitmap)
                            }
                        }
                        delay(frameIntervalMs)
                        frameTimeMs += frameIntervalMs
                    }
                } catch (_: FileNotFoundException) {
                    publishError("Debug video asset not found: assets/$assetPath")
                    break
                } catch (error: Exception) {
                    publishError("Unable to read debug video: ${error.message ?: error.javaClass.simpleName}")
                    break
                } finally {
                    runCatching { retriever.release() }
                }
            }
        }
    }

    override fun stop() {
        decodeJob?.cancel()
        decodeJob = null
    }

    private suspend fun publishError(message: String) {
        withContext(Dispatchers.Main) {
            onError(message)
        }
    }

    private fun MediaMetadataRetriever.setAssetDataSource(path: String) {
        try {
            appContext.assets.openFd(path).use { descriptor ->
                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            }
        } catch (_: IOException) {
            val cacheFile = File(appContext.cacheDir, "debug_video_${path.hashCode()}.mp4")
            appContext.assets.open(path).use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            setDataSource(cacheFile.absolutePath)
        }
    }

    companion object {
        const val DEFAULT_ASSET_PATH = "raw/test_video.mp4"
        private const val DEFAULT_TARGET_FPS = 15
    }
}
