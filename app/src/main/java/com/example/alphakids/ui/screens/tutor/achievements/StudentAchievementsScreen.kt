package com.example.alphakids.ui.screens.tutor.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alphakids.domain.models.Achievement
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.InfoCard
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.theme.AlphakidsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentAchievementsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "achievements",
    viewModel: StudentAchievementsViewModel = hiltViewModel()
) {
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val student by viewModel.student.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Mis logros",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.height(24.dp)
                        )
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.height(24.dp)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            StudentSummary(
                studentName = listOfNotNull(student?.nombre, student?.apellido)
                    .joinToString(" ")
                    .ifBlank { "Estudiante" },
                coins = student?.monedas ?: 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> LoadingCard()
                error != null -> ErrorCard(error = error!!)
                achievements.isEmpty() -> EmptyAchievementsCard()
                else -> AchievementList(achievements = achievements)
            }
        }
    }
}

@Composable
private fun StudentSummary(studentName: String, coins: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = studentName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Monedas actuales: $coins",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun LoadingCard() {
    InfoCard(
        title = "Cargando",
        data = "Obteniendo logros...",
        icon = Icons.Rounded.Android,
        dataTextColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ErrorCard(error: String) {
    InfoCard(
        title = "Error",
        data = error,
        icon = Icons.Rounded.Android,
        dataTextColor = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun EmptyAchievementsCard() {
    InfoCard(
        title = "Sin logros",
        data = "Juega y completa palabras para desbloquear logros",
        icon = Icons.Rounded.WorkspacePremium,
        dataTextColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun AchievementList(achievements: List<Achievement>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(achievements, key = { it.id }) { achievement ->
            AchievementCard(achievement = achievement)
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = achievement.wordText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Monedas ganadas: ${achievement.earnedCoins}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDate(achievement.earnedAtMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
private fun AchievementCardPreview() {
    AlphakidsTheme {
        AchievementCard(
            achievement = Achievement(
                id = "preview",
                studentId = "demo",
                wordId = "word",
                wordText = "Sol",
                earnedCoins = 5,
                earnedAtMillis = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentSummaryPreview() {
    AlphakidsTheme {
        StudentSummary(studentName = "Estudiante Demo", coins = 12)
    }
}
