package com.example.alphakids.ui.screens.tutor.games

import ScannerOverlay
import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.alphakids.ui.components.NotificationCard
import com.example.alphakids.ui.components.TimerBar
import com.example.alphakids.ui.screens.tutor.games.components.CameraActionBar
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import com.example.alphakids.ui.screens.tutor.game.components.GameResultDialog
import com.example.alphakids.ui.screens.tutor.game.components.GameResultState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onFlashClick: () -> Unit = {}, // Deprecated external callbacks; wired internally
    onShutterClick: () -> Unit = {},
    onFlipCameraClick: () -> Unit = {},
    onCloseNotificationClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission handling
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status != PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Camera controller
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    DisposableEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
        onDispose { cameraController.unbind() }
    }

    var torchOn by remember { mutableStateOf(false) }
    var lensFacingBack by remember { mutableStateOf(true) }

    // Timer state
    val totalMillis = 60_000L
    var remainingMillis by remember { mutableStateOf(totalMillis) }
    var progress by remember { mutableStateOf(0f) }
    var isWarning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (remainingMillis > 0) {
            delay(1000)
            remainingMillis -= 1000
            progress = 1f - (remainingMillis.toFloat() / totalMillis.toFloat())
            isWarning = remainingMillis <= 10_000L
        }
    }

    fun formatTime(ms: Long): String {
        val totalSec = (ms / 1000).toInt()
        val m = totalSec / 60
        val s = totalSec % 60
        return String.format("%d:%02d", m, s)
    }

    var showNotification by remember { mutableStateOf(true) }

    // Result dialog state
    var resultState by remember { mutableStateOf<GameResultState?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview when permission granted
        if (cameraPermissionState.status == PermissionStatus.Granted) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        controller = cameraController
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                }
            )
        } else {
            // Simple placeholder if permission not granted
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        }

        // Scanner overlay on top of preview
        ScannerOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // Timer at top
        TimerBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            progress = progress,
            timeText = formatTime(remainingMillis),
            isWarning = isWarning
        )

        // Notification card under timer
        if (showNotification) {
            NotificationCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 24.dp, end = 24.dp),
                title = "Une la palabra",
                content = "Apunta a las letras",
                icon = Icons.Rounded.Checkroom,
                onCloseClick = {
                    showNotification = false
                    onCloseNotificationClick()
                }
            )
        }

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        // Action bar
        CameraActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onFlashClick = {
                torchOn = !torchOn
                cameraController.enableTorch(torchOn)
            },
            onShutterClick = {
                // Try to capture without saving (in-memory); show dialog with result
                val executor = ContextCompat.getMainExecutor(context)
                try {
                    cameraController.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                            image.close()
                            resultState = GameResultState.Success(word = "CONEJO", imageIcon = Icons.Rounded.Checkroom)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            resultState = GameResultState.Failure(imageIcon = Icons.Rounded.Checkroom)
                        }
                    })
                } catch (e: Exception) {
                    resultState = GameResultState.Failure(imageIcon = Icons.Rounded.Checkroom)
                }
            },
            onFlipCameraClick = {
                lensFacingBack = !lensFacingBack
                cameraController.cameraSelector = if (lensFacingBack) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
            }
        )

        // Result dialog
        resultState?.let { state: GameResultState ->
            GameResultDialog(
                state = state,
                onDismiss = { resultState = null },
                onPrimaryAction = { resultState = null },
                onSecondaryAction = { resultState = null }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    AlphakidsTheme {
        CameraScreen(
            onBackClick = {}
        )
    }
}
