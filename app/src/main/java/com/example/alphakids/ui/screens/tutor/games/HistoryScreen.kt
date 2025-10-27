package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.dmSansFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val completedWords = remember { WordHistoryStorage.getCompletedWords(context) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Historial de Palabras",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
        
        if (completedWords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay palabras completadas",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = dmSansFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Completa algunas palabras para verlas aquí!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = dmSansFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(completedWords.sortedByDescending { it.timestamp }) { completedWord ->
                    CompletedWordCard(completedWord = completedWord)
                }
            }
        }
    }
}

@Composable
private fun CompletedWordCard(
    completedWord: CompletedWord
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completada",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = completedWord.word,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Completada el ${completedWord.getFormattedDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = dmSansFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}