package com.example.alphakids.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.usecases.CreateAssignmentUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetFilteredWordsUseCase
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val id: String = "",
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val recommendedWord: Word? = null
)

sealed interface ChatUiState {
    object Idle : ChatUiState
    object Loading : ChatUiState
    data class Success(val message: String) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFilteredWordsUseCase: GetFilteredWordsUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedUser = MutableStateFlow<Estudiante?>(null)
    val selectedUser: StateFlow<Estudiante?> = _selectedUser.asStateFlow()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val generativeModel = Firebase.ai(
        backend = GenerativeBackend.vertexAI()
    ).generativeModel(
        "gemini-2.5-flash"
    )

    fun onUserSelected(user: Estudiante) {
        _selectedUser.value = user
        // Limpiar mensajes anteriores al cambiar de usuario
        _messages.value = emptyList()
        
        // Mensaje de bienvenida contextual
        val welcomeMessage = ChatMessage(
            id = "welcome_${System.currentTimeMillis()}",
            text = "¡Hola! Soy tu asistente de IA. He seleccionado a ${user.nombre} (${user.edad} años). Puedo ayudarte a recomendar palabras adecuadas según su nivel de aprendizaje. ¿En qué puedo ayudarte?",
            isFromUser = false
        )
        _messages.value = listOf(welcomeMessage)
    }

    fun sendStudentMessage(text: String) {
        val currentUser = _selectedUser.value ?: return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Agregar mensaje del usuario
                val userMessage = ChatMessage(
                    id = "user_${System.currentTimeMillis()}",
                    text = text,
                    isFromUser = true
                )
                _messages.value = _messages.value + userMessage

                // Obtener datos contextuales
                val docente = getCurrentUserUseCase().firstOrNull()
                val availableWords = if (docente != null) {
                    getFilteredWordsUseCase(
                        docenteId = docente.uid,
                        dificultad = null,
                        sortBy = com.example.alphakids.domain.repository.WordSortOrder.TEXT_ASC
                    ).first()
                } else {
                    emptyList()
                }

                // Construir prompt contextual
                val contextualPrompt = buildContextualPrompt(currentUser, text, availableWords)
                
                // Generar respuesta con IA
                val response = generativeModel.generateContent(contextualPrompt)
                val aiResponseText = response.text ?: "Lo siento, no pude generar una respuesta."

                // Procesar respuesta para extraer recomendación de palabra
                val (responseText, recommendedWord) = processAIResponse(aiResponseText, availableWords)

                // Agregar mensaje de la IA
                val aiMessage = ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    text = responseText,
                    isFromUser = false,
                    recommendedWord = recommendedWord
                )
                _messages.value = _messages.value + aiMessage

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error al generar respuesta de IA", e)
                val errorMessage = ChatMessage(
                    id = "error_${System.currentTimeMillis()}",
                    text = "Lo siento, hubo un error al procesar tu solicitud. Por favor, intenta de nuevo.",
                    isFromUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildContextualPrompt(
        student: Estudiante,
        userMessage: String,
        availableWords: List<Word>
    ): String {
        val studentPerformance = determineStudentPerformance(student)
        val wordsContext = availableWords.take(20).joinToString(", ") { 
            "${it.texto} (${it.nivelDificultad})" 
        }

        return """
        Eres un asistente pedagógico especializado en educación infantil. Tu tarea es ayudar a docentes a asignar palabras apropiadas a sus estudiantes.

        CONTEXTO DEL ESTUDIANTE:
        - Nombre: ${student.nombre}
        - Edad: ${student.edad} años
        - Grado: ${student.grado}
        - Sección: ${student.seccion}
        - Nivel de desempeño estimado: $studentPerformance

        PALABRAS DISPONIBLES:
        $wordsContext

        NIVELES DE DIFICULTAD:
        - Fácil: Para estudiantes con desempeño bajo o que están comenzando
        - Medio: Para estudiantes con desempeño intermedio
        - Difícil: Para estudiantes con desempeño alto que necesitan desafíos

        PREGUNTA DEL DOCENTE: $userMessage

        INSTRUCCIONES:
        1. Responde SIEMPRE en español
        2. Sé breve y pedagógico (máximo 150 palabras)
        3. Si recomiendas una palabra específica, menciona su nombre exacto de la lista disponible
        4. Justifica tu recomendación basándote en la edad y nivel del estudiante
        5. Si la pregunta no es sobre recomendaciones de palabras, ofrece consejos pedagógicos relevantes
        6. Mantén un tono profesional pero amigable

        Respuesta:
        """.trimIndent()
    }

    private fun determineStudentPerformance(student: Estudiante): String {
        // Lógica simple basada en edad y grado
        // En una implementación real, esto vendría de datos de desempeño reales
        return when {
            student.edad <= 4 -> "Principiante - necesita palabras fáciles y apoyo visual"
            student.edad <= 6 -> "Intermedio - puede manejar palabras de dificultad media"
            else -> "Avanzado - puede enfrentar desafíos con palabras difíciles"
        }
    }

    private fun processAIResponse(aiResponse: String, availableWords: List<Word>): Pair<String, Word?> {
        // Buscar si la IA mencionó alguna palabra específica de la lista disponible
        val recommendedWord = availableWords.find { word ->
            aiResponse.contains(word.texto, ignoreCase = true)
        }

        return Pair(aiResponse, recommendedWord)
    }

    fun assignWordToUser(userId: String, word: Word) {
        viewModelScope.launch {
            try {
                _uiState.value = ChatUiState.Loading

                val docente = getCurrentUserUseCase().firstOrNull()
                val student = _selectedUser.value

                if (docente == null || student == null) {
                    _uiState.value = ChatUiState.Error("Faltan datos de usuario o estudiante.")
                    return@launch
                }

                val newAssignment = WordAssignment(
                    id = "",
                    idDocente = docente.uid,
                    idEstudiante = student.id,
                    idPalabra = word.id,
                    palabraTexto = word.texto,
                    palabraImagenUrl = word.imagenUrl,
                    palabraAudioUrl = word.audioUrl,
                    palabraDificultad = word.nivelDificultad,
                    estudianteNombre = student.nombre,
                    fechaAsignacionMillis = null,
                    fechaLimiteMillis = null,
                    estado = "PENDIENTE"
                )

                val result = createAssignmentUseCase(newAssignment)
                _uiState.value = if (result.isSuccess) {
                    ChatUiState.Success("Palabra '${word.texto}' asignada correctamente a ${student.nombre}.")
                } else {
                    ChatUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido al asignar.")
                }

                // Agregar mensaje de confirmación al chat
                val confirmationMessage = ChatMessage(
                    id = "confirmation_${System.currentTimeMillis()}",
                    text = "✅ Perfecto! He asignado la palabra '${word.texto}' a ${student.nombre}. ¿Hay algo más en lo que pueda ayudarte?",
                    isFromUser = false
                )
                _messages.value = _messages.value + confirmationMessage

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error al asignar palabra", e)
                _uiState.value = ChatUiState.Error("Error al asignar la palabra: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = ChatUiState.Idle
    }
}