package com.example.alphakids.ui.screens.teacher.words

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.LabeledDropdownField
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.LetterBox
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.PrimaryIconButton
import com.example.alphakids.ui.components.SecondaryTonalButton
import com.example.alphakids.ui.screens.teacher.words.components.ImageUploadBox
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun WordEditScreen(
    onCloseClick: () -> Unit,
    onPrimaryActionClick: () -> Unit,
    onCancelClick: () -> Unit,
    isEditing: Boolean = false
) {
    var palabra by remember { mutableStateOf(if (isEditing) "Palabra Existente" else "") }
    var categoria by remember { mutableStateOf(if (isEditing) "Categoría X" else "") }
    var dificultad by remember { mutableStateOf(if (isEditing) "Fácil" else "") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = if (isEditing) "Editar palabra" else "Crear palabra",
                actionIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LabeledTextField(
                label = "Palabra",
                value = palabra,
                onValueChange = { palabra = it },
                placeholderText = if (isEditing) palabra else "Escribe la palabra"
            )

            Text(
                text = "Imagen",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            ImageUploadBox { /* TODO: Lógica subir imagen */ }

            LabeledDropdownField(
                label = "Categoría",
                selectedOption = categoria,
                placeholderText = "Select option",
                onClick = { /* TODO: Mostrar dropdown */ }
            )

            LabeledDropdownField(
                label = "Dificultad",
                selectedOption = dificultad,
                placeholderText = "Select option",
                onClick = { /* TODO: Mostrar dropdown */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                SecondaryTonalButton(text = "Cancelar", onClick = onCancelClick)
                PrimaryButton(
                    text = if (isEditing) "Guardar" else "Crear palabra",
                    icon = if (!isEditing) Icons.Rounded.Add else null,
                    onClick = onPrimaryActionClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Vista Previa ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Vista previa",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Asumiendo tamaño
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WordPreview(
                        word = palabra,
                        icon = Icons.Rounded.Checkroom,
                        difficulty = dificultad.ifEmpty { "Fácil" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WordPreview(
    word: String,
    icon: ImageVector,
    difficulty: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(84.dp) // Tamaño similar al IconContainer
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "¿Qué es esto?",
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(word.length.coerceAtLeast(4)) { index ->
                LetterBox(
                    letter = null, // En preview no mostramos letras
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        PrimaryIconButton(
            icon = Icons.Rounded.CameraAlt,
            contentDescription = null, // Es decorativo en preview
            onClick = {}, // No hace nada en preview
            enabled = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoChip(
            text = difficulty,
            isSelected = true
        )
    }
}


@Preview(showBackground = true, name = "Crear Palabra")
@Composable
fun CreateWordScreenPreview() {
    AlphakidsTheme {
        WordEditScreen(
            onCloseClick = {},
            onPrimaryActionClick = {},
            onCancelClick = {},
            isEditing = false
        )
    }
}

@Preview(showBackground = true, name = "Editar Palabra")
@Composable
fun EditWordScreenPreview() {
    AlphakidsTheme {
        WordEditScreen(
            onCloseClick = {},
            onPrimaryActionClick = {},
            onCancelClick = {},
            isEditing = true
        )
    }
}
