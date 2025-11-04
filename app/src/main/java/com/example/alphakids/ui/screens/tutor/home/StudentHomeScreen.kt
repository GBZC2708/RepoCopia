package com.example.alphakids.ui.screens.tutor.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.DashboardActionCard
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun StudentHomeScreen(
    uiState: StudentHomeUiState,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPlayClick: () -> Unit,
    onDictionaryClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    onRetryLoadProfile: () -> Unit,
    currentRoute: String = "home"
) {
    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Menú Principal",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
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
        bottomBar = {
            MainBottomBar(
                items = studentItems,
                currentRoute = currentRoute,
                onNavigate = onBottomNavClick
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
            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error,
                            fontFamily = dmSansFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Reintentar",
                            onClick = onRetryLoadProfile
                        )
                    }
                }

                else -> {
                    val studentName = uiState.studentName ?: "Estudiante"
                    val coins = uiState.coins ?: 0

                    Text(
                        text = "¡Hola, $studentName!",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    uiState.profileLabel?.let { profile ->
                        Text(
                            text = "Perfil actual: $profile",
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = "Monedas disponibles: $coins",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¿Qué quieres hacer hoy?",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            text = "Jugar",
                            icon = Icons.Rounded.SportsEsports,
                            onClick = onPlayClick
                        )
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            text = "Mi Diccionario",
                            icon = Icons.Rounded.Book,
                            onClick = onDictionaryClick
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DashboardActionCard(
                        text = "Mis Logros",
                        icon = Icons.Rounded.WorkspacePremium,
                        onClick = onAchievementsClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentHomeScreenPreview() {
    AlphakidsTheme {
        StudentHomeScreen(
            uiState = StudentHomeUiState(
                studentName = "Sofía",
                profileLabel = "Inicial 5 años - B",
                coins = 42
            ),
            onBackClick = {},
            onLogoutClick = {},
            onPlayClick = {},
            onDictionaryClick = {},
            onAchievementsClick = {},
            onSettingsClick = {},
            onBottomNavClick = {},
            onRetryLoadProfile = {}
        )
    }
}
