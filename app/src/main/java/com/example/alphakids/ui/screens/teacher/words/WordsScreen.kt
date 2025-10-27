package com.example.alphakids.ui.screens.teacher.words

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Spellcheck
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
import androidx.compose.ui.unit.dp
import com.example.alphakids.domain.models.Word
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.InfoCard
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.WordListItem

val difficultiesList = listOf("Fácil", "Medio", "Difícil")

@Composable
fun WordsScreen(
    words: List<Word>,
    currentDifficultyFilter: String?,
    onSetDifficultyFilter: (String) -> Unit,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCreateWordClick: () -> Unit,
    onAssignWordClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "words"
) {
    val teacherBottomNavItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("students", "Alumnos", Icons.Rounded.Groups),
        BottomNavItem("words", "Palabras", Icons.Rounded.Spellcheck)
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedWordId by remember { mutableStateOf<String?>(null) }

    val displayedWords = remember(words, searchQuery) {
        if (searchQuery.isBlank()) {
            words
        } else {
            words.filter { word ->
                word.texto.contains(searchQuery, ignoreCase = true) ||
                    word.categoria.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val totalWords = words.size
    val totalCategories = words.map { it.categoria }.filter { it.isNotBlank() }.distinct().size
    val easyCount = words.count { it.nivelDificultad.equals("Fácil", ignoreCase = true) }
    val hardCount = words.count { it.nivelDificultad.equals("Difícil", ignoreCase = true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Palabras",
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
                items = teacherBottomNavItems,
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    text = "Crear palabra",
                    icon = Icons.Rounded.ListAlt,
                    onClick = onCreateWordClick
                )
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    text = "Asignar palabra",
                    icon = Icons.Rounded.ListAlt,
                    onClick = onAssignWordClick,
                    enabled = words.isNotEmpty()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Palabras",
                    data = totalWords.toString()
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Categorías",
                    data = totalCategories.toString()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Fáciles",
                    data = easyCount.toString()
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Difíciles",
                    data = hardCount.toString()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholderText = "Buscar"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                difficultiesList.forEach { difficulty ->
                    InfoChip(
                        text = difficulty,
                        isSelected = (currentDifficultyFilter == difficulty),
                        onClick = { onSetDifficultyFilter(difficulty) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = displayedWords, key = { it.id }) { word ->
                    WordListItem(
                        title = word.texto,
                        subtitle = word.categoria,
                        icon = Icons.Rounded.Spellcheck,
                        chipText = word.nivelDificultad,
                        isSelected = (selectedWordId == word.id),
                        onClick = {
                            selectedWordId = word.id
                            onWordClick(word.id)
                        },
                        imageUrl = word.imagenUrl
                    )
                }
            }
        }
    }
}
