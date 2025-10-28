package com.example.alphakids.ui.screens.tutor.games

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.MeteringPoint
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.alphakids.ui.components.NotificationCard
import com.example.alphakids.ui.components.TimerBar
import com.example.alphakids.ui.screens.tutor.games.components.CameraActionBar
import com.example.alphakids.ui.screens.tutor.games.components.GameResultDialog
import com.example.alphakids.ui.screens.tutor.games.components.GameResultState
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

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
        if (cameraPermissionState.status !is PermissionStatus.Granted) {
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

    // PreviewView referencia y ROI normalizado (l,t,r,b)
    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }
    var roiRect by remember { mutableStateOf<FloatArray?>(null) }

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
        if (cameraPermissionState.status is PermissionStatus.Granted) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        controller = cameraController
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewViewRef.value = this
                    }
                }
            )
        } else {
            // Simple placeholder if permission not granted
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black))
        }

        // Aplicar enfoque/exposición al centro del ROI cuando cambie
        LaunchedEffect(roiRect, previewViewRef.value) {
            val pv = previewViewRef.value
            val rect = roiRect
            if (pv != null && rect != null && pv.width > 0 && pv.height > 0) {
                val cx = ((rect[0] + rect[2]) / 2f) * pv.width
                val cy = ((rect[1] + rect[3]) / 2f) * pv.height
                val point: MeteringPoint = pv.meteringPointFactory.createPoint(cx, cy)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .addPoint(point, FocusMeteringAction.FLAG_AE)
                    .build()
                try {
                    cameraController.cameraControl?.startFocusAndMetering(action)
                } catch (_: Exception) { }
            }
        }

        // Overlay propio (evita choque con tu ScannerOverlay existente)
        CameraScannerOverlay(
            modifier = Modifier.fillMaxSize(),
            boxWidthPercent = 0.8f,
            boxAspectRatio = 1f,
            onBoxRectChange = { l: Float, t: Float, r: Float, b: Float ->
                roiRect = floatArrayOf(l, t, r, b)
            }
        )

        // Top area: Back button + Timer
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    TimerBar(
                        modifier = Modifier.fillMaxWidth(),
                        progress = progress,
                        timeText = formatTime(remainingMillis),
                        isWarning = isWarning
                    )
                }
            }

            if (showNotification) {
                NotificationCard(
                    modifier = Modifier.padding(top = 12.dp),
                    title = "Une la palabra",
                    content = "Apunta a las letras",
                    icon = Icons.Rounded.Checkroom,
                    onCloseClick = {
                        showNotification = false
                        onCloseNotificationClick()
                    }
                )
            }
        }

        // Action bar
        CameraActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onFlashClick = {
                torchOn = !torchOn
                cameraController.enableTorch(torchOn)
                onFlashClick()
            },
            onShutterClick = {
                // Toma sin guardar (in-memory); muestra diálogo con resultado
                val executor = ContextCompat.getMainExecutor(context)
                try {
                    cameraController.takePicture(
                        executor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                                image.close()
                                resultState = GameResultState.Success(
                                    word = "CONEJO",
                                    imageIcon = Icons.Rounded.Checkroom
                                )
                            }

                            override fun onError(exception: ImageCaptureException) {
                                resultState = GameResultState.Failure(
                                    imageIcon = Icons.Rounded.Checkroom
                                )
                            }
                        }
                    )
                } catch (e: Exception) {
                    resultState = GameResultState.Failure(
                        imageIcon = Icons.Rounded.Checkroom
                    )
                }
                onShutterClick()
            },
            onFlipCameraClick = {
                lensFacingBack = !lensFacingBack
                cameraController.cameraSelector = if (lensFacingBack) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
                onFlipCameraClick()
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

/**
 * Overlay de escáner centrado que dibuja un rectángulo guía y reporta
 * su ROI normalizado [0..1] como (left, top, right, bottom) al contenedor.
 */
@Composable
private fun CameraScannerOverlay(
    modifier: Modifier = Modifier,
    boxWidthPercent: Float = 0.8f,
    boxAspectRatio: Float = 1f,
    onBoxRectChange: (Float, Float, Float, Float) -> Unit = { _, _, _, _ -> }
) {
    BoxWithConstraints(modifier = modifier) {
        val parentWdp = maxWidth
        val parentHdp = maxHeight
        val density = LocalDensity.current

        // Medidas del box en dp
        val boxW = parentWdp * boxWidthPercent
        val boxH = boxW / boxAspectRatio

        // Limitar para no salir del alto disponible
        val finalBoxH = if (boxH > parentHdp) parentHdp else boxH
        val finalBoxW = if (boxH > parentHdp) parentHdp * boxAspectRatio else boxW

        // Posición centrada
        val leftDp = (parentWdp - finalBoxW) / 2
        val topDp = (parentHdp - finalBoxH) / 2
        val rightDp = leftDp + finalBoxW
        val bottomDp = topDp + finalBoxH

        // Reportar ROI normalizado
        LaunchedEffect(parentWdp, parentHdp, finalBoxW, finalBoxH) {
            val l = leftDp.value / parentWdp.value
            val t = topDp.value / parentHdp.value
            val r = rightDp.value / parentWdp.value
            val b = bottomDp.value / parentHdp.value
            onBoxRectChange(l, t, r, b)
        }

        // Dibujo del marco
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = finalBoxW, height = finalBoxH)
                    .border(
                        width = 3.dp,
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    )
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
