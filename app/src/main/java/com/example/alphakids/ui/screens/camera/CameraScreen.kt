package com.example.alphakids.ui.screens.camera

import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun CameraScreen() {
    // --- Variables de Estado para controlar la cámara ---
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    val hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        // CAPA 1: El visor de la cámara ahora recibe el estado
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            lensFacing = lensFacing,
            onCameraBound = { cameraInstance ->
                camera = cameraInstance
            }
        )

        // CAPA 2: La superposición con el marco y la animación
        ScannerOverlay(modifier = Modifier.fillMaxSize())

        // CAPA 3: Los controles y consejos
        TopHintChip(
            icon = Icons.Outlined.Checkroom,
            title = "Une la palabra",
            subtitle = "Apunta a las letras",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        CameraControls(
            onTakePhoto = { Log.d("Camera", "Take Photo") },
            onToggleFlash = {
                if (hasFlashUnit) {
                    isFlashOn = !isFlashOn
                    camera?.cameraControl?.enableTorch(isFlashOn)
                }
            },
            onSwitchCamera = {
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                    CameraSelector.LENS_FACING_FRONT
                } else {
                    CameraSelector.LENS_FACING_BACK
                }
                // Reseteamos el flash al cambiar de cámara
                isFlashOn = false
            },
            // *** ARREGLO VISUAL: AÑADIMOS PADDING A LOS CONTROLES ***
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp) // <-- Aumentamos el padding inferior
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    AlphakidsTheme {
        // En la preview no podemos mostrar la cámara, así que solo vemos la UI
        Box(modifier = Modifier.fillMaxSize()) {
            ScannerOverlay(modifier = Modifier.fillMaxSize())
            TopHintChip(
                icon = Icons.Outlined.Checkroom,
                title = "Une la palabra",
                subtitle = "Apunta a las letras",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            )
            CameraControls(
                onTakePhoto = {},
                onToggleFlash = {},
                onSwitchCamera = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}