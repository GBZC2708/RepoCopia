package com.example.alphakids.ui.screens.tutor.games

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.usecases.AdjustStudentCoinsUseCase
import com.example.alphakids.domain.usecases.FindWordByTextUseCase
import com.example.alphakids.domain.usecases.HasDiscoveredWordUseCase
import com.example.alphakids.domain.usecases.ObserveDiscoveredWordsUseCase
import com.example.alphakids.domain.usecases.SaveDiscoveredWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val ATTEMPTS_LIMIT = 3
private const val SUCCESS_REWARD = 10
// Penalización ligera cuando la palabra no pertenece al diccionario.
private const val FAILURE_PENALTY = -5

/**
 * Representa el estado de la dinámica "Descubre" en tiempo real.
 */
data class DiscoverCameraUiState(
    val attemptsLeft: Int = ATTEMPTS_LIMIT,
    val lastDetectedWord: String = "",
    val statusMessage: String = "Escanea una palabra para comenzar",
    val lastCoinsDelta: Int = 0,
    val totalCoins: Int? = null,
    val isProcessing: Boolean = false,
    val gameFinished: Boolean = false,
    val error: String? = null,
    val discoveredWordIds: Set<String> = emptySet()
)

@HiltViewModel
class DiscoverCameraViewModel @Inject constructor(
    private val findWordByText: FindWordByTextUseCase,
    private val observeDiscoveredWords: ObserveDiscoveredWordsUseCase,
    private val hasDiscoveredWord: HasDiscoveredWordUseCase,
    private val saveDiscoveredWord: SaveDiscoveredWordUseCase,
    private val adjustStudentCoins: AdjustStudentCoinsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverCameraUiState())
    val uiState: StateFlow<DiscoverCameraUiState> = _uiState.asStateFlow()

    private var studentId: String? = null
    private var watchJob: Job? = null
    private var lastProcessTime = 0L
    private var lastProcessedWord = ""

    fun initialize(targetStudentId: String) {
        if (studentId == targetStudentId) return
        studentId = targetStudentId

        // Observamos las palabras ya descubiertas para evitar duplicados.
        watchJob?.cancel()
        watchJob = viewModelScope.launch {
            observeDiscoveredWords(targetStudentId).collect { discovered ->
                _uiState.update { state ->
                    state.copy(discoveredWordIds = discovered.map { it.wordId }.toSet())
                }
            }
        }
    }

    fun resetGame() {
        _uiState.value = DiscoverCameraUiState()
        lastProcessedWord = ""
        lastProcessTime = 0L
    }

    fun handleDetectedText(rawText: String) {
        val student = studentId ?: return
        val now = System.currentTimeMillis()

        if (_uiState.value.isProcessing || _uiState.value.gameFinished) return
        if (now - lastProcessTime < TimeUnit.SECONDS.toMillis(2)) return

        // Tomamos la primera palabra relevante detectada por OCR.
        val normalizedWord = rawText
            .split("\n")
            .flatMap { it.split(" ") }
            .firstOrNull { candidate -> candidate.any { it.isLetter() } }
            ?.filter { it.isLetter() }
            ?.lowercase()
            ?: return

        if (normalizedWord == lastProcessedWord) return

        lastProcessedWord = normalizedWord
        lastProcessTime = now

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            try {
                val word = findWordByText(normalizedWord)
                if (word == null) {
                    // La palabra no existe en la base: penalizamos.
                    applyPenalty(student, normalizedWord)
                } else {
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
        val coinsResult = adjustStudentCoins(student, FAILURE_PENALTY)
        val updatedCoins = coinsResult.getOrNull()

        _uiState.update {
            it.copy(
                attemptsLeft = attemptsLeft,
                lastDetectedWord = detectedWord,
                statusMessage = "No existe",
                lastCoinsDelta = FAILURE_PENALTY,
                totalCoins = updatedCoins,
                isProcessing = false,
                gameFinished = attemptsLeft == 0
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        watchJob?.cancel()
    }
}
