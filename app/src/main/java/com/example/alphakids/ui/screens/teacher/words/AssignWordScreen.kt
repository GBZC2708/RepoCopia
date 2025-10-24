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
import kotlinx.coroutines.launch

val assignmentDifficultiesList = listOf("Fácil", "Medio", "Difícil")

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

    var showSuccessDialog by remember { mutableStateOf(false) }
    var wordSearchQuery by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("IA") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
        viewModel.loadWords()
        chatViewModel.fetchUsers()
    }

    val coroutineScope = rememberCoroutineScope()
    val chatListState = rememberLazyListState()

    // Auto-scroll cuando llegan nuevos mensajes
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            coroutineScope.launch {
                chatListState.animateScrollToItem(chatMessages.size - 1)
            }
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
                            chipText = "90%",
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
                            onClickAssign = {
                                viewModel.createAssignment(word)
                            }
                        )
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
