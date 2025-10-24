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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
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
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.WordListItem
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun GameWordsScreen(
    onBackClick: () -> Unit,
    onWordClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedWordId by remember { mutableStateOf<String?>(null) }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filters = listOf("Todos", "Pendientes", "Fácil", "Medio", "Difícil")

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
                filters.forEach { filter ->
                    InfoChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
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
                        chipText = if (index % 3 == 0) "Fácil" else "Medio",
                        isSelected = (selectedWordId == "id_$index"),
                        onClick = {
                            selectedWordId = "id_$index"
                            onWordClick("id_$index")
                        }
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
