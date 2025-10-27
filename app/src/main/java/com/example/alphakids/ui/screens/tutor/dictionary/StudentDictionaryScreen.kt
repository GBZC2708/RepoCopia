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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.InfoCard
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.WordListItem
import com.example.alphakids.ui.theme.AlphakidsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentDictionaryScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWordClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "dictionary",
    viewModel: StudentDictionaryViewModel = hiltViewModel()
) {
    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    val searchQuery by viewModel.searchQuery.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedWordId by remember { mutableStateOf<String?>(null) }

    val categories = remember(allItems) {
        allItems.mapNotNull { item ->
            item.texto.firstOrNull()?.uppercaseChar()?.toString()
        }.distinct()
    }

    val displayedItems = remember(filteredItems, selectedCategory) {
        filteredItems.filter { item ->
            selectedCategory == null || item.texto.startsWith(selectedCategory!!, ignoreCase = true)
        }
    }

    val completedThisWeek = remember(allItems) { countCompletedThisWeek(allItems) }

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
                onValueChange = viewModel::setSearchQuery,
                placeholderText = "Buscar en mi diccionario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    text = "Todas",
                    isSelected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
                categories.forEach { category ->
                    InfoChip(
                        text = category,
                        isSelected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Palabras aprendidas",
                    data = allItems.size.toString()
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Esta semana",
                    data = completedThisWeek.toString()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (displayedItems.isEmpty()) {
                EmptyDictionaryState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayedItems.size) { index ->
                        val item = displayedItems[index]
                        WordListItem(
                            title = item.texto,
                            subtitle = formatAddedDate(item.fechaAgregadoMillis),
                            icon = Icons.Rounded.Checkroom,
                            chipText = "Aprendida",
                            isSelected = (selectedWordId == item.idPalabra),
                            onClick = {
                                selectedWordId = item.idPalabra
                                onWordClick(item.idPalabra)
                            },
                            imageUrl = item.imagenUrl
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDictionaryState() {
    androidx.compose.material3.Text(
        text = "Aún no tienes palabras en tu diccionario.",
        modifier = Modifier.padding(vertical = 48.dp),
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun formatAddedDate(timestamp: Long?): String {
    if (timestamp == null) return "Agregado recientemente"
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return "Agregada el ${formatter.format(Date(timestamp))}"
}

private fun countCompletedThisWeek(items: List<PersonalDictionaryItem>): Int {
    if (items.isEmpty()) return 0
    val now = System.currentTimeMillis()
    val weekAgo = now - 7 * 24 * 60 * 60 * 1000L
    return items.count { it.fechaAgregadoMillis != null && it.fechaAgregadoMillis >= weekAgo }
}

@Preview(showBackground = true)
@Composable
fun StudentDictionaryScreenPreview() {
    AlphakidsTheme {
        // Vista previa estática con datos simulados.
        StudentDictionaryContentPreview()
    }
}

@Composable
private fun StudentDictionaryContentPreview() {
    val fakeItems = listOf(
        PersonalDictionaryItem(
            idPalabra = "1",
            texto = "Manzana",
            imagenUrl = "",
            audioUrl = "",
            fechaAgregadoMillis = System.currentTimeMillis(),
            ultimoRepasoMillis = null,
            vecesJugado = 2,
            vecesAcertado = 2
        )
    )

    Column(modifier = Modifier.padding(24.dp)) {
        InfoCard(
            modifier = Modifier.fillMaxWidth(),
            title = "Palabras aprendidas",
            data = fakeItems.size.toString()
        )
        Spacer(modifier = Modifier.height(16.dp))
        WordListItem(
            title = fakeItems.first().texto,
            subtitle = "Agregada el hoy",
            icon = Icons.Rounded.Checkroom,
            chipText = "Aprendida",
            isSelected = false,
            onClick = {},
            imageUrl = null
        )
    }
}
