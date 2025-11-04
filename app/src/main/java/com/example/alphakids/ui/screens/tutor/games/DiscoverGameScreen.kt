package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.domain.models.Word
import com.example.alphakids.ui.screens.tutor.games.components.WordPuzzleCard
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Pantalla introductoria para el modo "Descubre".
 * Reutiliza la misma tarjeta visual que el puzzle tradicional para mantener consistencia.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverGameScreen(
    isLoading: Boolean,
    currentWord: Word?,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit,
    onNextWord: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Descubre",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = "Regresar"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡A explorar nuevas palabras!",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val wordLength = currentWord?.texto?.length ?: 0
                    WordPuzzleCard(
                        wordLength = wordLength.coerceAtLeast(1),
                        icon = Icons.Rounded.Explore,
                        wordImage = currentWord?.imagenUrl,
                        difficulty = currentWord?.nivelDificultad ?: "Libre",
                        onTakePhotoClick = onScanClick,
                        isTakePhotoEnabled = currentWord != null,
                        questionText = currentWord?.let { "Encuentra esta palabra" } ?: "Selecciona una palabra",
                        showLetterPlaceholders = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentWord != null) {
                        Text(
                            text = "Número de letras: $wordLength",
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text(
                            text = "Necesitamos una palabra para comenzar",
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNextWord) {
                        Text(
                            text = "Cambiar palabra",
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
