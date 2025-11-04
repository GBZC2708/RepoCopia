package com.example.alphakids.ui.screens.tutor.games

import android.Manifest
import android.speech.tts.TextToSpeech
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.BoxScope
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.dmSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Cámara especializada para el modo "Descubre". Lee palabras y delega la lógica al ViewModel.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiscoverCameraScreen(
    studentId: String,
    onBackClick: () -> Unit,
    viewModel: DiscoverCameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val coroutineScope = rememberCoroutineScope()

    // El estado representa intentos restantes, moneda ganada y mensajes hablados.
    val uiState by viewModel.uiState.collectAsState()
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    // Text to Speech para guiar al niño en cada resultado.
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        viewModel.initialize(studentId)
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                tts?.setSpeechRate(0.95f)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            tts?.shutdown()
        }
    }

    // Cada vez que cambia el mensaje lo narramos.
    LaunchedEffect(uiState.statusMessage, uiState.lastDetectedWord) {
        if (uiState.statusMessage.isNotBlank()) {
            val phrase = buildString {
                if (uiState.lastDetectedWord.isNotBlank()) {
                    append("Palabra detectada: ${uiState.lastDetectedWord}. ")
                }
                append(uiState.statusMessage)
            }
            tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    if (!cameraPermission.status.isGranted) {
        LaunchedEffect(Unit) { cameraPermission.launchPermissionRequest() }
    }

    if (!cameraPermission.status.isGranted) {
        PermissionFallback(onBackClick = onBackClick)
        return
    }

    Scaffold(
        topBar = {
            DiscoverCameraTopBar(
                attemptsLeft = uiState.attemptsLeft,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Vista tradicional de CameraX embebida en Compose.
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        previewView = this
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            DisposableEffect(previewView, cameraProvider) {
                if (previewView != null && cameraProvider == null) {
                    val provider = ProcessCameraProvider.getInstance(context).get()
                    cameraProvider = provider
                    startCamera(
                        provider,
                        previewView!!,
                        executor,
                        lifecycleOwner,
                        coroutineScope
                    ) { imageProxy ->
                        processImage(
                            imageProxy,
                            recognizer,
                            viewModel::handleDetectedText
                        )
                    }
                }

                onDispose { cameraProvider?.unbindAll() }
            }

            // Panel inferior que resume el resultado actual.
            DiscoverStatusPanel(uiState = uiState)
        }
    }

    if (uiState.gameFinished) {
        // Mostramos un resumen rápido antes de regresar al menú.
        DiscoverResultDialog(
            statusMessage = uiState.statusMessage,
            coinsDelta = uiState.lastCoinsDelta,
            onRetry = viewModel::resetGame,
            onExit = onBackClick
        )
    }
}

@Composable
private fun DiscoverCameraTopBar(
    attemptsLeft: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }
        Text(
            text = "Intentos restantes: $attemptsLeft",
            color = Color.White,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun BoxScope.DiscoverStatusPanel(uiState: DiscoverCameraUiState) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState.statusMessage,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (uiState.lastDetectedWord.isNotBlank()) {
            Text(
                text = "Última palabra: ${uiState.lastDetectedWord}",
                fontFamily = dmSansFamily
            )
        }
        if (uiState.lastCoinsDelta != 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (uiState.lastCoinsDelta > 0) {
                    "+${uiState.lastCoinsDelta} monedas"
                } else {
                    "${uiState.lastCoinsDelta} monedas"
                },
                color = if (uiState.lastCoinsDelta > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                fontWeight = FontWeight.SemiBold
            )
        }
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DiscoverResultDialog(
    statusMessage: String,
    coinsDelta: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onExit,
        confirmButton = {
            // Permite reiniciar los intentos sin abandonar la cámara.
            PrimaryButton(
                text = "Volver a intentar",
                onClick = {
                    onRetry()
                }
            )
        },
        dismissButton = {
            Button(onClick = onExit) { Text("Salir") }
        },
        title = { Text(statusMessage, fontFamily = dmSansFamily) },
        text = {
            Text(
                text = if (coinsDelta > 0) {
                    "Ganaste $coinsDelta monedas"
                } else if (coinsDelta < 0) {
                    "Perdiste ${coinsDelta * -1} monedas"
                } else {
                    "Sigue intentando para conseguir monedas"
                }
            )
        }
    )
}

@Composable
private fun PermissionFallback(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Necesitamos acceso a la cámara para jugar", fontFamily = dmSansFamily)
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Volver", onClick = onBackClick)
        }
    }
}

private fun startCamera(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    executor: ExecutorService,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    scope: CoroutineScope,
    onImageAvailable: (ImageProxy) -> Unit
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor) { imageProxy ->
                scope.launch { onImageAvailable(imageProxy) }
            }
        }

    val selector = CameraSelector.DEFAULT_BACK_CAMERA

    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
}

private fun processImage(
    imageProxy: ImageProxy,
    recognizer: TextRecognizer,
    onTextDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val detected = result.text
                if (detected.isNotBlank()) {
                    onTextDetected(detected)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}
