package com.example.alphakids.ui.screens.tutor.games

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
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.WordListItem
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.word.GameWordsViewModel

private val filters = listOf("Todos", "Pendientes", "Fácil", "Medio", "Difícil")

@Composable
fun GameWordsScreen(
    viewModel: GameWordsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onWordClick: (String) -> Unit
) {
    val assignedWords by viewModel.assignedWords.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.difficultyFilter.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Mis Palabras",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(24.dp)
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
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholderText = "Buscar"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    InfoChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { viewModel.setDifficultyFilter(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(assignedWords, key = { it.id }) { assignment ->
                    WordListItem(
                        title = assignment.palabraTexto,
                        subtitle = "Categoría: ${assignment.palabraDificultad}",
                        icon = Icons.Rounded.Checkroom, // TODO: Reemplazar con el ícono correcto
                        chipText = assignment.estado,
                        isSelected = false,
                        onClick = { onWordClick(assignment.id) },
                        imageUrl = assignment.palabraImagenUrl // Asumiendo que existe esta propiedad
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameWordsScreenPreview() {
    AlphakidsTheme {
        GameWordsScreen(
            onBackClick = {},
            onWordClick = {}
        )
    }
}
