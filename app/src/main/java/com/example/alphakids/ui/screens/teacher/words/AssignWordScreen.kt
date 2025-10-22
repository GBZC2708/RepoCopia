package com.example.alphakids.ui.screens.teacher.words

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.AssignmentCard
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.StudentListItem
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

// Importaciones necesarias para el modo IA
import com.example.alphakids.ui.components.AiMessageBubble
import com.example.alphakids.ui.components.ChatInputBar
import com.example.alphakids.ui.components.ChatHeader
import com.example.alphakids.ui.screens.chat.ChatMessage // Asumiendo que ChatMessage está accesible

@Composable
fun ModeSelector(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf("Manual", "IA")
    val shape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = shape
            )
            .height(56.dp)
            .padding(4.dp)
    ) {
        modes.forEach { mode ->
            val isSelected = mode == selectedMode
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = shape,
                // Color primario para el modo seleccionado (IA)
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                onClick = { onModeSelected(mode) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = mode,
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        fontFamily = dmSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun AssignWordScreen(
    onBackClick: () -> Unit,
    onAssignWordClick: (studentId: String, wordId: String) -> Unit,
    onStudentClick: (studentId: String) -> Unit
) {
    var selectedMode by remember { mutableStateOf("IA") } // Modo "IA" por defecto
    var selectedStudentId by remember { mutableStateOf("sofia_id") }
    var searchQuery by remember { mutableStateOf("") }

    val students = listOf(
        Pair("sofia_id", Triple("Sofia Arenas", "3 años", "18 palabras")),
        Pair("id_2", Triple("Fullname", "Age", "Num words")),
        Pair("id_3", Triple("Fullname", "Age", "Num words")),
        Pair("id_4", Triple("Fullname", "Age", "Num words")),
        Pair("id_5", Triple("Fullname", "Age", "Num words"))
    )
    val words = listOf(
        "word_1" to Triple("WORD", "Categoría", "Chip"),
        "word_2" to Triple("WORD", "Categoría", "Chip"),
        "word_3" to Triple("WORD", "Categoría", "Chip")
    )

    // Contenido simulado del chat para el modo IA
    val aiMessages = listOf(
        ChatMessage.Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce blandit luctus egestas. Fusce neque mauris."),
        ChatMessage.Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce blandit luctus egestas. Fusce neque mauris."),
        ChatMessage.Recommendation("WORD", "Categoría", "Chip")
    )


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Asignar palabra",
                actionIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onBackground
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
        ) {
            // Contenedor fijo para el selector de modo y estudiantes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de Modo
                Text(
                    text = "Modo",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                ModeSelector(selectedMode = selectedMode, onModeSelected = { selectedMode = it })

                Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

                // Selección de Estudiante
                SectionTitle(text = "Selecciona a un estudiante")

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    students.take(5).forEach { (id, data) ->
                        val (fullname, age, numWords) = data
                        val isSelected = id == selectedStudentId

                        Surface(
                            onClick = { selectedStudentId = id },
                            shape = RoundedCornerShape(28.dp),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            StudentListItem(
                                fullname = fullname,
                                age = age,
                                numWords = numWords,
                                icon = Icons.Rounded.Face,
                                chipText = "90%",
                                onClick = { onStudentClick(id) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Título de la sección variable
                if (selectedMode == "IA") {
                    SectionTitle(text = "Recomendación")
                } else {
                    SectionTitle(text = "Elige una palabra")
                }
            }

            // Contenido Variable (Manual vs. IA)
            if (selectedMode == "IA") {
                // Modo IA: Muestra el ChatHeader, LazyColumn de mensajes y ChatInputBar
                Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    ChatHeader()

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface) // Fondo de chat
                            .padding(horizontal = 10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(aiMessages.size) { index ->
                            val message = aiMessages[index]
                            when (message) {
                                is ChatMessage.Text -> {
                                    AiMessageBubble(message = message.content)
                                }
                                is ChatMessage.Recommendation -> {
                                    AiMessageBubble(message = "") {
                                        AssignmentCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            wordTitle = message.wordTitle,
                                            wordSubtitle = message.wordSubtitle,
                                            chipText = message.chipText,
                                            onClickAssign = { onAssignWordClick(selectedStudentId, "ai_word_id") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ChatInputBar(onSendClick = { /* Lógica de chat */ })
                }
            } else {
                // Modo Manual: Muestra la SearchBar y la lista de AssignmentCard
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp), // Padding horizontal para el contenido manual
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholderText = "Buscar"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoChip(text = "Chip", isSelected = false)
                        InfoChip(text = "Chip", isSelected = false)
                        InfoChip(text = "Chip", isSelected = false)
                        InfoChip(text = "Chip", isSelected = false)
                    }

                    SectionTitle(text = "Palabras disponibles")

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        words.forEach { (id, data) ->
                            val (title, subtitle, chip) = data
                            AssignmentCard(
                                wordTitle = title,
                                wordSubtitle = subtitle,
                                chipText = chip,
                                onClickAssign = { onAssignWordClick(selectedStudentId, id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AssignWordScreenPreview() {
    AlphakidsTheme {
        AssignWordScreen(
            onBackClick = {},
            onAssignWordClick = { _, _ -> },
            onStudentClick = {}
        )
    }
}
