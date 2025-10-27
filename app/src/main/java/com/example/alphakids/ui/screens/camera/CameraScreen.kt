package com.example.alphakids.ui.screens.camera

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.screens.game.FailureResultScreen
import com.example.alphakids.ui.screens.game.SuccessResultScreen
import kotlin.random.Random

// Definimos los posibles estados del juego
sealed class GameState {
    object Scanning : GameState()
    object Success : GameState()
    object Failure : GameState()
}

@Composable
fun CameraScreen(
    onExitGame: () -> Unit
) {
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var gameState by remember { mutableStateOf<GameState>(GameState.Scanning) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = gameState) {
            is GameState.Scanning -> {

                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    lensFacing = lensFacing,
                    onCameraBound = {  }
                )
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
                    onTakePhoto = {
                        gameState = if (Random.nextBoolean()) GameState.Success else GameState.Failure
                    },
                    onToggleFlash = { /* ... */ },
                    onSwitchCamera = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                )
            }
            is GameState.Success -> {
                SuccessResultScreen(
                    word = "POLO",
                    onContinue = { gameState = GameState.Scanning },
                    onBack = onExitGame
                )
            }
            is GameState.Failure -> {
                FailureResultScreen(
                    onRetry = { gameState = GameState.Scanning },
                    onExit = onExitGame
                )
            }
        }
    }
}