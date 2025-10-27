package com.example.alphakids.ui.screens.game

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.*
import com.example.alphakids.ui.theme.AlphakidsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juego") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround // Distribuye el espacio
        ) {
            GameImageContainer(icon = Icons.Default.Checkroom)

            Text(
                text = "¿Qué es esto?",
                style = MaterialTheme.typography.headlineMedium
            )

            // Fila para las letras de la respuesta
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Aquí iría la lógica para llenar las letras, por ahora mostramos cajas vacías
                repeat(4) {
                    DashedLetterInputBox()
                }
            }

            CameraButton(onClick = { Log.d("GameScreen", "Camera opened") })

            DifficultyChip(text = "Fácil")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    AlphakidsTheme {
        GameScreen(onBackClick = {}, onCloseClick = {})
    }
}