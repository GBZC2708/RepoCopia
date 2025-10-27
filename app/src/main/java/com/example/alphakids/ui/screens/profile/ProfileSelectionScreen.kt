package com.example.alphakids.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.ProfileCard
import com.example.alphakids.ui.theme.AlphakidsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    onLogoutClick: () -> Unit,
    onProfileClick: (profileName: String) -> Unit,
    onAddProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "¿Quién jugará hoy?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Selecciona un perfil para comenzar",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            ProfileCard(
                title = "Sofía",
                subtitle = "Institución Educativa Santa Sofía",
                icon = Icons.Rounded.Pets, // Placeholder para el ícono de conejo
                onClick = { onProfileClick("Sofía") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProfileCard(
                title = "Agregar perfil",
                subtitle = "Crea el perfil de tu hijo",
                icon = Icons.Default.Add,
                onClick = onAddProfileClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSelectionScreenPreview() {
    AlphakidsTheme {
        ProfileSelectionScreen(
            onLogoutClick = {},
            onProfileClick = {},
            onAddProfileClick = {}
        )
    }
}