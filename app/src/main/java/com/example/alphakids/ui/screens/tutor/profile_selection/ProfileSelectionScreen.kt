package com.example.alphakids.ui.screens.tutor.profile_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.auth.AuthViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.screens.tutor.profile_selection.components.StudentProfileCard
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun ProfileSelectionScreen(
    onProfileClick: (profileId: String) -> Unit,
    onAddProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel() // Inyecta el ViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val tutorName = currentUser?.nombre

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Perfiles",
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            CustomFAB(
                icon = Icons.Rounded.Settings,
                contentDescription = "Configuración",
                onClick = onSettingsClick
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

            tutorName?.let { name ->
                Text(
                    text = "¡Hola, $name!",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

            Text(
                text = "¿Quién jugará hoy?",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Selecciona un perfil para comenzar",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            StudentProfileCard(
                title = "Sofía",
                description = "Institución Educativa Santa Sofía",
                icon = Icons.Rounded.Face,
                onClick = { onProfileClick("sofia_id") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StudentProfileCard(
                title = "Agregar perfil",
                description = "Crea el perfil de tu hijo",
                icon = Icons.Rounded.Add,
                onClick = onAddProfileClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSelectionScreenPreview() {
    AlphakidsTheme {
        ProfileSelectionScreen(
            onProfileClick = {},
            onAddProfileClick = {},
            onSettingsClick = {},
            onLogoutClick = {}
        )
    }
}
