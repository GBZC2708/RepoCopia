package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.ui.screens.tutor.games.components.WordPuzzleCard
import com.example.alphakids.ui.theme.dmSansFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPuzzleScreen(
    assignmentId: String,
    onBackClick: () -> Unit,
    onTakePhotoClick: (targetWord: String) -> Unit,
    viewModel: WordPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(assignmentId) {
        viewModel.loadWordData(assignmentId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Puzzle de Palabra",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = dmSansFamily
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val targetWord = uiState.assignment?.palabraTexto
                    WordPuzzleCard(
                        wordLength = targetWord?.length ?: 4,
                        wordImage = uiState.assignment?.palabraImagen,
                        difficulty = uiState.assignment?.palabraDificultad ?: "Normal",
                        onTakePhotoClick = {
                            targetWord?.takeIf { it.isNotBlank() }?.let(onTakePhotoClick)
                        },
                        isTakePhotoEnabled = !targetWord.isNullOrBlank()
                    )
                }
            }
        }
    }
}