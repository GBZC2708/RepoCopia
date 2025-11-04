package com.example.alphakids.ui.screens.tutor.games

import android.Manifest
import android.speech.tts.TextToSpeech
import android.util.Log
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
import androidx.core.content.ContextCompat
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.screens.camera.ScannerOverlay
import com.example.alphakids.ui.theme.dmSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

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
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    // Flag atómico que consumen los analizadores para procesar un único fotograma por petición.
    val scanRequestFlag = remember { AtomicBoolean(false) }

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
                studentName = uiState.studentName,
                attemptsLeft = uiState.attemptsLeft,
                totalCoins = uiState.totalCoins,
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

            ScannerOverlay(modifier = Modifier.fillMaxSize())

            // Iniciamos CameraX de forma asíncrona cada vez que la vista o los permisos cambian.
            DisposableEffect(previewView, cameraPermission.status.isGranted) {
                if (previewView != null && cameraPermission.status.isGranted) {
                    val providerFuture = ProcessCameraProvider.getInstance(context)
                    providerFuture.addListener({
                        try {
                            val provider = providerFuture.get()
                            cameraProvider = provider
                            startCamera(
                                cameraProvider = provider,
                                previewView = previewView!!,
                                executor = executor,
                                lifecycleOwner = lifecycleOwner,
                                scope = coroutineScope,
                                recognizer = recognizer,
                                shouldAnalyze = { scanRequestFlag.compareAndSet(true, false) },
                                onWordDetected = { word ->
                                    coroutineScope.launch { viewModel.handleDetectedWord(word) }
                                },
                                onEmptyResult = {
                                    coroutineScope.launch { viewModel.onEmptyScanResult() }
                                }
                            )
                        } catch (e: Exception) {
                            Log.e("DiscoverCameraScreen", "No se pudo iniciar la cámara", e)
                        }
                    }, mainExecutor)
                }

                onDispose {
                    cameraProvider?.unbindAll()
                }
            }

            // Panel inferior que resume el resultado actual.
            DiscoverStatusPanel(
                uiState = uiState,
                onScanClick = {
                    if (viewModel.onScanRequested()) {
                        scanRequestFlag.set(true)
                    }
                }
            )
        }
    }

    if (uiState.gameFinished) {
        // Mostramos un resumen rápido antes de regresar al menú.
        DiscoverResultDialog(
            statusMessage = uiState.statusMessage,
            coinsDelta = uiState.lastCoinsDelta,
            currentCoins = uiState.totalCoins,
            onRetry = viewModel::resetGame,
            onExit = onBackClick
        )
    }
}

@Composable
private fun DiscoverCameraTopBar(
    studentName: String,
    attemptsLeft: Int,
    totalCoins: Int?,
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Perfil: ${studentName.ifBlank { "Sin nombre" }}",
                color = Color.White,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Intentos restantes: $attemptsLeft",
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Monedas",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
            Text(
                text = totalCoins?.toString() ?: "--",
                color = Color.White,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun BoxScope.DiscoverStatusPanel(
    uiState: DiscoverCameraUiState,
    onScanClick: () -> Unit
) {
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
        Spacer(modifier = Modifier.height(12.dp))
        // Botón principal que dispara el análisis controlado del fotograma actual.
        PrimaryButton(
            text = when {
                uiState.attemptsLeft <= 0 -> "Sin intentos"
                uiState.isProcessing -> "Escaneando..."
                else -> "Escanear"
            },
            enabled = !uiState.isProcessing && uiState.attemptsLeft > 0,
            onClick = onScanClick
        )
    }
}

@Composable
private fun DiscoverResultDialog(
    statusMessage: String,
    coinsDelta: Int,
    currentCoins: Int?,
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
                text = buildString {
                    when {
                        coinsDelta > 0 -> append("Ganaste $coinsDelta monedas")
                        coinsDelta < 0 -> append("Perdiste ${coinsDelta * -1} monedas")
                        else -> append("Sigue intentando para conseguir monedas")
                    }
                    currentCoins?.let { total ->
                        append("\nMonedas actuales: $total")
                    }
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
    recognizer: TextRecognizer,
    shouldAnalyze: () -> Boolean,
    onWordDetected: (String) -> Unit,
    onEmptyResult: () -> Unit
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor) { imageProxy ->
                if (!shouldAnalyze()) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                processImage(
                    imageProxy = imageProxy,
                    recognizer = recognizer,
                    onWordDetected = { word ->
                        scope.launch { onWordDetected(word) }
                    },
                    onEmptyResult = {
                        scope.launch { onEmptyResult() }
                    }
                )
            }
        }

    val selector = CameraSelector.DEFAULT_BACK_CAMERA

    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
}

private fun processImage(
    imageProxy: ImageProxy,
    recognizer: TextRecognizer,
    onWordDetected: (String) -> Unit,
    onEmptyResult: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        recognizer.process(image)
            // Reconocemos el texto y filtramos únicamente lo que cae dentro del recuadro visible.
            .addOnSuccessListener { result ->
                val candidate = extractWordFromRegion(result, imageProxy, rotation, SCAN_ROI)
                if (candidate != null) {
                    onWordDetected(candidate)
                } else {
                    onEmptyResult()
                }
            }
            .addOnFailureListener { error ->
                Log.e("DiscoverCameraScreen", "Error procesando imagen", error)
                onEmptyResult()
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        onEmptyResult()
        imageProxy.close()
    }
}

private data class NormalizedRect(val left: Float, val top: Float, val right: Float, val bottom: Float)

// Región de interés centrada que coincide con la superposición mostrada al usuario.
private val SCAN_ROI = NormalizedRect(
    left = (1f - 0.7f) / 2f,
    top = (1f - 0.5f) / 2f,
    right = 1f - (1f - 0.7f) / 2f,
    bottom = 1f - (1f - 0.5f) / 2f
)

private fun extractWordFromRegion(
    textResult: Text,
    imageProxy: ImageProxy,
    rotationDegrees: Int,
    region: NormalizedRect
): String? {
    val imageWidth = if (rotationDegrees % 180 == 0) imageProxy.width else imageProxy.height
    val imageHeight = if (rotationDegrees % 180 == 0) imageProxy.height else imageProxy.width

    textResult.textBlocks.forEach { block ->
        block.lines.forEach { line ->
            line.elements.forEach { element ->
                val boundingBox = element.boundingBox ?: return@forEach
                val centerX = boundingBox.centerX().toFloat() / imageWidth
                val centerY = boundingBox.centerY().toFloat() / imageHeight

                if (centerX in region.left..region.right && centerY in region.top..region.bottom) {
                    val normalized = element.text.filter { it.isLetter() }
                    if (normalized.isNotBlank()) {
                        return normalized.lowercase()
                    }
                }
            }
        }
    }
    return null
}
