package com.example.rpg.ui.components

import android.Manifest
import android.graphics.Bitmap
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.rpg.BuildConfig
import com.example.rpg.frame.CameraFrameSource
import com.example.rpg.frame.FrameInputMode
import com.example.rpg.frame.VideoFileFrameSource
import com.example.rpg.pose.PoseAnalyzer

/**
 * Preview that forwards frames from CameraX or a debug video file to PoseAnalyzer.
 */
@Composable
fun CameraPreview(
    poseAnalyzer: PoseAnalyzer,
    modifier: Modifier = Modifier,
) {
    var inputMode by remember { mutableStateOf(FrameInputMode.Camera) }

    Box(modifier = modifier) {
        when (inputMode) {
            FrameInputMode.Camera -> CameraFrameSourcePreview(
                poseAnalyzer = poseAnalyzer,
                modifier = Modifier.fillMaxSize(),
            )
            FrameInputMode.VideoTest -> VideoFileFrameSourcePreview(
                poseAnalyzer = poseAnalyzer,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (BuildConfig.DEBUG) {
            DebugFrameInputSwitcher(
                inputMode = inputMode,
                onInputModeChange = { inputMode = it },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun CameraFrameSourcePreview(
    poseAnalyzer: PoseAnalyzer,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (!hasCameraPermission) {
        CameraPermissionPlaceholder(onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }, modifier = modifier)
        return
    }

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner, previewView, poseAnalyzer) {
        val frameSource = CameraFrameSource(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            poseAnalyzer = poseAnalyzer,
        )
        frameSource.start()

        onDispose {
            frameSource.close()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { previewView },
    )
}

@Composable
private fun VideoFileFrameSourcePreview(
    poseAnalyzer: PoseAnalyzer,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentFrame by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context, poseAnalyzer) {
        val frameSource = VideoFileFrameSource(
            context = context,
            poseAnalyzer = poseAnalyzer,
            coroutineScope = coroutineScope,
            onFrame = { frame ->
                currentFrame = frame
                errorMessage = null
            },
            onError = { message ->
                errorMessage = message
            },
        )
        frameSource.start()

        onDispose {
            frameSource.close()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111)),
        contentAlignment = Alignment.Center,
    ) {
        val frame = currentFrame
        if (frame != null) {
            Image(
                bitmap = frame.asImageBitmap(),
                contentDescription = "Debug video input",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            DebugVideoPlaceholder(
                message = errorMessage ?: "Loading debug video from assets/raw/test_video.mp4",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DebugFrameInputSwitcher(
    inputMode: FrameInputMode,
    onInputModeChange: (FrameInputMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nextMode = when (inputMode) {
        FrameInputMode.Camera -> FrameInputMode.VideoTest
        FrameInputMode.VideoTest -> FrameInputMode.Camera
    }
    Button(
        onClick = { onInputModeChange(nextMode) },
        modifier = modifier,
    ) {
        Text(
            text = when (inputMode) {
                FrameInputMode.Camera -> "CAM"
                FrameInputMode.VideoTest -> "VIDEO"
            },
        )
    }
}

@Composable
private fun DebugVideoPlaceholder(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = message,
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(24.dp),
    )
}

/**
 * Placeholder displayed when camera permission has not been granted.
 */
@Composable
fun CameraPermissionPlaceholder(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111111)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Нужно разрешение камеры для локального трекинга позы",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = onRequestPermission) {
                Text("Разрешить камеру")
            }
        }
    }
}
