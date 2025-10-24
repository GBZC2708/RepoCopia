package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.ui.screens.tutor.games.components.WordPuzzleCard
import com.example.alphakids.ui.theme.AlphakidsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordPuzzleScreen(
    assignment: AsignacionPalabra,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    viewModel: WordPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(assignment) {
        viewModel.loadWordData(assignment)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    WordPuzzleCard(
                        modifier = Modifier.padding(16.dp),
                        wordLength = assignment.palabraTexto.length,
                        icon = uiState.wordIcon,
                        difficulty = assignment.palabraDificultad,
                        onTakePhotoClick = onTakePhotoClick,
                        wordImage = assignment.palabraImagen
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordPuzzleScreenPreview() {
    AlphakidsTheme {
        WordPuzzleScreen(
            assignment = AsignacionPalabra(
                palabraTexto = "CASA",
                palabraDificultad = "Fácil"
            ),
            onBackClick = {},
            onCloseClick = {},
            onTakePhotoClick = {}
        )
    }
}