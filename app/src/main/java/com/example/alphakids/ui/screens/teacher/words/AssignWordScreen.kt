package com.example.alphakids.ui.screens.teacher.words

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.ui.components.*
import com.example.alphakids.ui.theme.dmSansFamily
import com.example.alphakids.ui.word.assign.AssignmentUiState
import com.example.alphakids.ui.word.assign.AssignWordViewModel
import com.example.alphakids.ui.chat.ChatViewModel
import com.example.alphakids.ui.chat.ChatMessage
import com.example.alphakids.ui.chat.ChatUiState
import kotlinx.coroutines.launch

val assignmentDifficultiesList = listOf("Fácil", "Medio", "Difícil")

// Función para determinar el desempeño del estudiante
private fun determineStudentPerformance(student: Estudiante): String {
    return when {
        student.edad <= 4 -> "Principiante"
        student.edad <= 6 -> "Intermedio"
        else -> "Avanzado"
    }
}

@Composable 
fun MessageBubble(message: ChatMessage, onAssignClick: (String, String) -> Unit, onAnalyzeClick: () -> Unit = {}, onAssignMultiple: (List<com.example.alphakids.domain.models.Word>) -> Unit = {}) { 
    val align = if (message.isFromUser) Alignment.End else Alignment.Start 
    val color = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer 
        else MaterialTheme.colorScheme.secondaryContainer 
 
    Column(Modifier.fillMaxWidth(), horizontalAlignment = align) { 
        Card(
            shape = RoundedCornerShape(16.dp), 
            colors = CardDefaults.cardColors(containerColor = color)
        ) { 
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontFamily = dmSansFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Botón de análisis si está habilitado
                if (message.showAnalyzeButton) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAnalyzeClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SmartToy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Analizar estudiante")
                    }
                }
                
                // Palabra individual recomendada
                message.recommendedWord?.let { word -> 
                    Spacer(modifier = Modifier.height(8.dp))
                    Button( 
                        onClick = { onAssignClick("", word.texto) }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) { 
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Asignar \"${word.texto}\"") 
                    } 
                }
                
                // Múltiples palabras recomendadas del análisis
                if (message.recommendedWords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Mostrar las palabras recomendadas
                    message.recommendedWords.forEach { word ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = word.texto,
                                        fontFamily = dmSansFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${word.categoria} • ${word.nivelDificultad}",
                                        fontFamily = dmSansFamily,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                IconButton(
                                    onClick = { onAssignClick("", word.texto) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Asignar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Botón para asignar todas las palabras
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAssignMultiple(message.recommendedWords) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Asignar todas las palabras (${message.recommendedWords.size})")
                    }
                }
            }
        } 
    } 
} 
 
@Composable 
fun MessageInput(message: String, onMessageChange: (String) -> Unit, onSendClick: () -> Unit) { 
    Row( 
        Modifier 
            .fillMaxWidth() 
            .padding(8.dp) 
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)) 
            .padding(8.dp), 
        verticalAlignment = Alignment.CenterVertically 
    ) { 
        TextField( 
            value = message, 
            onValueChange = onMessageChange, 
            modifier = Modifier.weight(1f), 
            placeholder = { Text("Escribe un mensaje...") }, 
            maxLines = 3, 
            colors = TextFieldDefaults.colors( 
                focusedContainerColor = Color.Transparent, 
                unfocusedContainerColor = Color.Transparent, 
                focusedIndicatorColor = Color.Transparent, 
                unfocusedIndicatorColor = Color.Transparent 
            ) 
        ) 
        IconButton(onClick = onSendClick, enabled = message.isNotBlank()) { 
            Icon(
                Icons.Default.Send, 
                contentDescription = "Enviar", 
                tint = MaterialTheme.colorScheme.primary
            ) 
        } 
    } 
}

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
    viewModel: AssignWordViewModel = hiltViewModel(),
    chatViewModel: com.example.alphakids.ui.chat.ChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onStudentClick: (studentId: String) -> Unit
) {
    val students by viewModel.students.collectAsState()
    val selectedStudentId by viewModel.selectedStudentId.collectAsState()
    val availableWords by viewModel.availableWords.collectAsState()

    val currentFilter by viewModel.wordFilterDifficulty.collectAsState()

    val assignmentUiState by viewModel.uiState.collectAsState()
    
    // Estados del chat
    val chatMessages by chatViewModel.messages.collectAsState()
    val chatIsLoading by chatViewModel.isLoading.collectAsState()
    val selectedUser by chatViewModel.selectedUser.collectAsState()
    val chatUiState by chatViewModel.uiState.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var wordSearchQuery by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("IA") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()

    // Los datos se cargan automáticamente a través de StateFlow en el ViewModel
    // No se necesitan llamadas explícitas a loadStudents() y loadWords()

    // Auto-scroll cuando llegan nuevos mensajes
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    LaunchedEffect(assignmentUiState) {
        when (assignmentUiState) {
            is AssignmentUiState.Success -> {
                showSuccessDialog = true
            }
            is AssignmentUiState.Error -> {
                Toast.makeText(context, (assignmentUiState as AssignmentUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetUiState()
            }
            AssignmentUiState.Loading -> {}
            AssignmentUiState.Idle -> {}
        }
    }

    // Manejar estados del chat (asignaciones desde IA)
    LaunchedEffect(chatUiState) {
        when (val currentState = chatUiState) {
            is ChatUiState.Success -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                chatViewModel.resetUiState()
            }
            is ChatUiState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                chatViewModel.resetUiState()
            }
            ChatUiState.Loading -> {}
            ChatUiState.Idle -> {}
        }
    }

    // Configurar el estudiante seleccionado en el ChatViewModel
    LaunchedEffect(selectedStudentId) {
        val selectedStudent = students.find { it.id == selectedStudentId }
        selectedStudent?.let { student ->
            chatViewModel.onUserSelected(student)
        }
    }

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // Selector de Modo
                item {
                    Text(
                        text = "Modo",
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    ModeSelector(selectedMode = selectedMode, onModeSelected = { selectedMode = it })
                }

                item {
                    Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    SectionTitle(text = "Selecciona a un estudiante")
                }

                // Selección de Estudiante
                items(
                    items = students.filter { selectedStudentId == null || it.id == selectedStudentId },
                    key = { it.id }
                ) { student: Estudiante ->
                    val isSelected = student.id == selectedStudentId
                    Surface(
                        onClick = { viewModel.selectStudent(student.id) },
                        shape = RoundedCornerShape(28.dp),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        StudentListItem(
                            fullname = student.nombre,
                            age = "${student.edad} años",
                            numWords = "N/A",
                            icon = Icons.Rounded.Face,
                            chipText = determineStudentPerformance(student),
                            onClickNavigation = { onStudentClick(student.id) }
                        )
                    }
                }

                // SECCIÓN DE ASIGNACIÓN DE PALABRAS (Solo si se selecciona un estudiante y está en modo Manual)
                if (selectedStudentId != null && selectedMode == "Manual") {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle(text = "Elige una palabra")
                    }

                    item {
                        SearchBar(
                            value = wordSearchQuery,
                            onValueChange = {
                                wordSearchQuery = it
                                viewModel.setWordSearchQuery(it)
                            },
                            placeholderText = "Buscar"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            assignmentDifficultiesList.forEach { difficulty ->
                                InfoChip(
                                    text = difficulty,
                                    isSelected = (currentFilter == difficulty),
                                    onClick = { viewModel.setWordFilterDifficulty(difficulty) }
                                )
                            }
                        }
                    }

                    item {
                        SectionTitle(text = "Palabras disponibles")
                    }

                    items(availableWords, key = { it.id }) { word ->
                        AssignmentCard(
                            wordTitle = word.texto,
                            wordSubtitle = word.categoria,
                            chipText = word.nivelDificultad,
                            imageUrl = word.imagenUrl,
                            onClickAssign = {
                                viewModel.createAssignment(word)
                            }
                        )
                    }
                }

                // SECCIÓN DE CHAT IA (Solo si se selecciona un estudiante y está en modo IA)
                if (selectedStudentId != null && selectedMode == "IA") {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle(text = "Asistente IA")
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                // Encabezado del chat
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SmartToy,
                                        contentDescription = "IA",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Asistente IA para asignación de palabras",
                                        fontFamily = dmSansFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Lista de mensajes del chat
                                LazyColumn(
                                    state = chatListState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    items(chatMessages) { message ->
                                        MessageBubble(
                                            message = message,
                                            onAssignClick = { userId, word ->
                                                selectedUser?.let { student ->
                                                    // Buscar la palabra en la lista de palabras disponibles
                                                    val wordToAssign = availableWords.find { it.texto == word }
                                                    wordToAssign?.let { 
                                                        chatViewModel.assignWordToUser(student.id, it)
                                                    }
                                                }
                                            },
                                            onAnalyzeClick = {
                                                chatViewModel.analyzeStudent()
                                            },
                                            onAssignMultiple = { words ->
                                                chatViewModel.assignMultipleWords(words)
                                            }
                                        )
                                    }

                                    if (chatIsLoading) {
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    strokeWidth = 2.dp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "IA está pensando...",
                                                    fontSize = 12.sp,
                                                    fontFamily = dmSansFamily,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Campo de entrada de mensaje usando el componente MessageInput
                                var messageText by remember { mutableStateOf("") }
                                
                                MessageInput(
                                    message = messageText,
                                    onMessageChange = { messageText = it },
                                    onSendClick = {
                                        if (messageText.isNotBlank()) {
                                            chatViewModel.sendStudentMessage(messageText)
                                            messageText = ""
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        ActionDialog(
            icon = Icons.Rounded.CheckCircle,
            message = (assignmentUiState as AssignmentUiState.Success).message,
            primaryButtonText = "Aceptar",
            onPrimaryButtonClick = {
                showSuccessDialog = false
                viewModel.resetUiState()
                viewModel.selectStudent(null)
            },
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetUiState()
            }
        )
    }
}
