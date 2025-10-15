package com.example.alphakids.ui.screens.main_menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.MenuActionCard
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun MainMenuScreen(
    profileName: String,
    onPlayClick: () -> Unit, // Parámetro añadido
    onDictionaryClick: () -> Unit, // Parámetro añadido
    onAchievementsClick: () -> Unit, // Parámetro añadido
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Encabezado de Bienvenida ---
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "¡Hola, $profileName!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "¿Qué quieres hacer hoy?",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // --- Tarjetas de Acción ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            MenuActionCard(
                title = "Jugar",
                icon = Icons.Default.SportsEsports,
                onClick = onPlayClick, // Usamos el callback
                modifier = Modifier.size(width = 164.dp, height = 189.dp)
            )
            MenuActionCard(
                title = "Mi\nDiccionario",
                icon = Icons.Default.MenuBook,
                onClick = onDictionaryClick, // Usamos el callback
                modifier = Modifier.size(width = 164.dp, height = 189.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        MenuActionCard(
            title = "Mis Logros",
            icon = Icons.Default.EmojiEvents,
            onClick = onAchievementsClick, // Usamos el callback
            modifier = Modifier.size(width = 348.dp, height = 189.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun MainMenuScreenPreview() {
    AlphakidsTheme {
        MainMenuScreen(
            profileName = "Sofía",
            onPlayClick = {},
            onDictionaryClick = {},
            onAchievementsClick = {}
        )
    }
}