package com.example.alphakids.ui.screens.tutor.games

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.usecases.AdjustStudentCoinsUseCase
import com.example.alphakids.domain.usecases.FindWordByTextUseCase
import com.example.alphakids.domain.usecases.HasDiscoveredWordUseCase
import com.example.alphakids.domain.usecases.ObserveStudentByIdUseCase
import com.example.alphakids.domain.usecases.ObserveDiscoveredWordsUseCase
import com.example.alphakids.domain.usecases.SaveDiscoveredWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val ATTEMPTS_LIMIT = 3
private const val SUCCESS_REWARD = 10
// Penalización ligera cuando la palabra no pertenece al diccionario.
private const val FAILURE_PENALTY = 0
private const val INITIAL_STATUS = "Ubica la palabra en el recuadro y presiona Escanear"

/**
 * Representa el estado de la dinámica "Descubre" en tiempo real.
 */
data class DiscoverCameraUiState(
    val attemptsLeft: Int = ATTEMPTS_LIMIT,
    val lastDetectedWord: String = "",
    val statusMessage: String = INITIAL_STATUS,
    val lastCoinsDelta: Int = 0,
    val totalCoins: Int? = null,
    val isProcessing: Boolean = false,
    val gameFinished: Boolean = false,
    val error: String? = null,
    val discoveredWordIds: Set<String> = emptySet(),
    val studentName: String = "",
    val targetWordId: String? = null,
    val targetWordText: String = "",
    val targetWordImageUrl: String? = null,
    val targetWordLength: Int = 0
)

@HiltViewModel
class DiscoverCameraViewModel @Inject constructor(
    private val findWordByText: FindWordByTextUseCase,
    private val observeDiscoveredWords: ObserveDiscoveredWordsUseCase,
    private val hasDiscoveredWord: HasDiscoveredWordUseCase,
    private val saveDiscoveredWord: SaveDiscoveredWordUseCase,
    private val adjustStudentCoins: AdjustStudentCoinsUseCase,
    private val observeStudentById: ObserveStudentByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverCameraUiState())
    val uiState: StateFlow<DiscoverCameraUiState> = _uiState.asStateFlow()

    private var studentId: String? = null
    private var discoveredWordsJob: Job? = null
    private var studentObserverJob: Job? = null
    private var lastProcessTime = 0L
    private var lastProcessedWord = ""

    fun initialize(targetStudentId: String) {
        if (studentId == targetStudentId) return
        studentId = targetStudentId

        resetGame()

        // Observamos las palabras ya descubiertas para evitar duplicados.
        discoveredWordsJob?.cancel()
        discoveredWordsJob = viewModelScope.launch {
            observeDiscoveredWords(targetStudentId).collect { discovered ->
                _uiState.update { state ->
                    state.copy(discoveredWordIds = discovered.map { it.wordId }.toSet())
                }
            }
        }

        // Escuchamos los cambios del perfil para mostrar nombre y monedas actualizadas.
        studentObserverJob?.cancel()
        studentObserverJob = viewModelScope.launch {
            observeStudentById(targetStudentId).collectLatest { student ->
                _uiState.update { state ->
                    if (student == null) {
                        state.copy(
                            studentName = "",
                            totalCoins = null,
                            error = "No pudimos cargar la información del perfil.",
                            statusMessage = "Intenta nuevamente en unos segundos"
                        )
                    } else {
                        state.copy(
                            studentName = student.nombre,
                            totalCoins = student.monedas,
                            error = null,
                            statusMessage = if (state.statusMessage.isBlank()) INITIAL_STATUS else state.statusMessage
                        )
                    }
                }
            }
        }
    }

    fun resetGame() {
        val current = _uiState.value
        _uiState.value = DiscoverCameraUiState(
            studentName = current.studentName,
            totalCoins = current.totalCoins,
            discoveredWordIds = current.discoveredWordIds,
            targetWordId = current.targetWordId,
            targetWordText = current.targetWordText,
            targetWordImageUrl = current.targetWordImageUrl,
            targetWordLength = current.targetWordLength
        )
        lastProcessedWord = ""
        lastProcessTime = 0L
    }

    fun setTargetWord(word: Word?) {
        _uiState.update { state ->
            state.copy(
                targetWordId = word?.id,
                targetWordText = word?.texto ?: "",
                targetWordImageUrl = word?.imagenUrl,
                targetWordLength = word?.texto?.length ?: 0,
                statusMessage = if (state.statusMessage.isBlank()) INITIAL_STATUS else state.statusMessage,
                error = null
            )
        }
    }

    fun handleDetectedWord(normalizedWord: String) {
        val student = studentId ?: return
        val now = System.currentTimeMillis()

        if (_uiState.value.gameFinished) return
        if (now - lastProcessTime < TimeUnit.SECONDS.toMillis(2)) return

        if (normalizedWord == lastProcessedWord) return

        lastProcessedWord = normalizedWord
        lastProcessTime = now

        if (_uiState.value.targetWordId.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    statusMessage = "Selecciona una palabra antes de escanear",
                    error = "Sin palabra objetivo"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            try {
                val word = findWordByText(normalizedWord)
                if (word == null) {
                    // La palabra no existe en la base: penalizamos.
                    applyPenalty(student, normalizedWord)
                } else {
                    if (!isMatchingTarget(word)) {
                        handleMismatchedWord(word)
                        return@launch
                    }
                    // Existe en el diccionario: verificamos si es nueva.
                    processFoundWord(student, word)
                }
            } catch (e: Exception) {
                Log.e("DiscoverVM", "Error procesando palabra", e)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = e.localizedMessage ?: "Error inesperado",
                        statusMessage = "Ocurrió un error, intenta de nuevo"
                    )
                }
            }
        }
    }

    private suspend fun processFoundWord(student: String, word: Word) {
        val alreadyFound = uiState.value.discoveredWordIds.contains(word.id)
            || hasDiscoveredWord(student, word.id)

        val attemptsLeft = (_uiState.value.attemptsLeft - 1).coerceAtLeast(0)

        if (alreadyFound) {
            // Si ya la registramos, solo informamos sin otorgar monedas.
            _uiState.update {
                it.copy(
                    attemptsLeft = attemptsLeft,
                    lastDetectedWord = word.texto,
                    statusMessage = "Palabra ya encontrada",
                    lastCoinsDelta = 0,
                    isProcessing = false,
                    gameFinished = attemptsLeft == 0
                )
            }
            return
        }

        val saveResult = saveDiscoveredWord(
            DiscoveredWord(
                id = "",
                studentId = student,
                wordId = word.id,
                wordText = word.texto,
                discoveredAtMillis = null
            )
        )

        if (saveResult.isFailure) {
            throw saveResult.exceptionOrNull() ?: IllegalStateException("Error desconocido")
        }

        // Bonificamos con monedas al descubrir una palabra nueva.
        val coinsResult = adjustStudentCoins(student, SUCCESS_REWARD)
        val updatedCoins = coinsResult.getOrNull()

        _uiState.update {
            it.copy(
                attemptsLeft = attemptsLeft,
                lastDetectedWord = word.texto,
                statusMessage = "Nueva palabra encontrada",
                lastCoinsDelta = SUCCESS_REWARD,
                totalCoins = updatedCoins,
                isProcessing = false,
                gameFinished = attemptsLeft == 0
            )
        }
    }

    private suspend fun applyPenalty(student: String, detectedWord: String) {
        val attemptsLeft = (_uiState.value.attemptsLeft - 1).coerceAtLeast(0)

        _uiState.update {
            it.copy(
                attemptsLeft = attemptsLeft,
                lastDetectedWord = detectedWord,
                statusMessage = "No encontramos esa palabra en tu diccionario",
                lastCoinsDelta = FAILURE_PENALTY,
                isProcessing = false,
                gameFinished = attemptsLeft == 0
            )
        }
    }

    private fun handleMismatchedWord(word: Word) {
        val attemptsLeft = (_uiState.value.attemptsLeft - 1).coerceAtLeast(0)
        _uiState.update {
            it.copy(
                attemptsLeft = attemptsLeft,
                lastDetectedWord = word.texto,
                statusMessage = "Esa no es la palabra objetivo (${it.targetWordText})",
                lastCoinsDelta = 0,
                isProcessing = false,
                error = null,
                gameFinished = attemptsLeft == 0
            )
        }
    }

    private fun isMatchingTarget(word: Word): Boolean {
        val targetId = _uiState.value.targetWordId
        if (targetId != null && word.id == targetId) {
            return true
        }
        val targetText = _uiState.value.targetWordText
        if (targetText.isBlank()) return false
        val normalizedWord = word.texto.lowercase().normalizeForComparison()
        val normalizedTarget = targetText.lowercase().normalizeForComparison()
        return normalizedWord == normalizedTarget
    }

    private fun String.normalizeForComparison(): String {
        val decomposed = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        return decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    /**
     * Valida si todavía se puede escanear y actualiza el estado para informar a la UI.
     */
    fun onScanRequested(): Boolean {
        val current = _uiState.value
        if (current.isProcessing) {
            return false
        }
        if (current.gameFinished) {
            return false
        }

        if (current.targetWordId.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    statusMessage = "Selecciona una palabra antes de escanear",
                    error = "Sin palabra objetivo"
                )
            }
            return false
        }

        if (current.attemptsLeft <= 0) {
            _uiState.update {
                it.copy(
                    statusMessage = "Se acabaron los intentos",
                    gameFinished = true,
                    error = null
                )
            }
            return false
        }

        _uiState.update {
            it.copy(
                statusMessage = "Buscando palabra...",
                error = null,
                lastCoinsDelta = 0,
                isProcessing = true
            )
        }
        return true
    }

    /** Notifica que no se detectó texto válido en el último intento. */
    fun onEmptyScanResult() {
        _uiState.update {
            it.copy(
                isProcessing = false,
                lastDetectedWord = "",
                statusMessage = "No pudimos leer la palabra, ajusta tu cámara",
                lastCoinsDelta = 0
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        discoveredWordsJob?.cancel()
        studentObserverJob?.cancel()
    }

}
