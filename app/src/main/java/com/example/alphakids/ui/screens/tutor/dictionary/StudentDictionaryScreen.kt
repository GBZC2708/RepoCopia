package com.example.alphakids.ui.screens.tutor.dictionary

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.WordListItem
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun StudentDictionaryScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "dictionary"
) {
    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedWordId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Mi Diccionario",
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
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholderText = "Buscar en mi diccionario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(text = "Categoría 1", isSelected = false)
                InfoChip(text = "Categoría 2", isSelected = false)
                InfoChip(text = "Categoría 3", isSelected = false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(10) { index ->
                    WordListItem(
                        title = "PALABRA ${index + 1}",
                        subtitle = "Categoría",
                        icon = Icons.Rounded.Checkroom,
                        chipText = "Fácil",
                        isSelected = (selectedWordId == "id_$index"),
                        onClick = { selectedWordId = "id_$index" },
                        imageUrl = null // Aquí se pasaría la URL real de la imagen
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentDictionaryScreenPreview() {
    AlphakidsTheme {
        StudentDictionaryScreen(
            onBackClick = {},
            onLogoutClick = {},
            onWordClick = {},
            onSettingsClick = {},
            onBottomNavClick = {}
        )
    }
}
