package com.example.alphakids.ui.screens.tutor.games

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.alphakids.domain.usecases.CompleteAssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

data class CameraOCRUiState(
    val targetWord: String = "",
    val detectedText: String = "",
    val isWordDetected: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CameraOCRViewModel @Inject constructor(
    private val completeAssignmentUseCase: CompleteAssignmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraOCRUiState())
    val uiState: StateFlow<CameraOCRUiState> = _uiState.asStateFlow()

    private var textToSpeech: TextToSpeech? = null
    private var lastSpokenText = ""
    private var wordDetectedTime = 0L

    fun initializeTTS(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale("es", "ES"))
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Spanish language not supported")
                        tts.setLanguage(Locale.getDefault())
                    }
                    tts.setSpeechRate(0.9f)
                }
            } else {
                Log.e("TTS", "TTS initialization failed")
            }
        }
    }

    fun setTargetWord(word: String) {
        _uiState.value = _uiState.value.copy(targetWord = word.uppercase())
    }

    fun processDetectedText(detectedText: String) {
        // Normalizamos ambos textos para comparación robusta.
        val normalizedDetected = normalizeTextForComparison(detectedText)
        val currentTarget = _uiState.value.targetWord
        val normalizedTarget = normalizeTextForComparison(currentTarget)

        _uiState.value = _uiState.value.copy(detectedText = detectedText)

        // ¿La palabra objetivo aparece en el OCR?
        val isWordFound = normalizedTarget.isNotEmpty() && normalizedDetected.contains(normalizedTarget)

        if (isWordFound && !_uiState.value.isWordDetected) {
            // Detectada por primera vez
            wordDetectedTime = System.currentTimeMillis()
            _uiState.value = _uiState.value.copy(isWordDetected = true)

            val successMessage = "¡Bien hecho! La palabra es $currentTarget"
            speakText(successMessage)
            Log.d("CameraOCR", "Word '$currentTarget' detected successfully!")
        } else if (!isWordFound && _uiState.value.isWordDetected) {
            // Ya no se detecta: resetea después de 3s de gracia
            val currentTime = System.currentTimeMillis()
            if (currentTime - wordDetectedTime > 3000) {
                _uiState.value = _uiState.value.copy(isWordDetected = false)
            }
        }
    }

    private fun speakText(text: String) {
        if (text != lastSpokenText) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            lastSpokenText = text
            Log.d("TTS", "Speaking: $text")
        }
    }

    // Normaliza: minúsculas, sin acentos, solo letras/números/espacios.
    private fun normalizeTextForComparison(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        val noDiacritics = decomposed.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        return noDiacritics.replace(Regex("[^a-z0-9 ]"), " ").replace(Regex("\\s+"), " ").trim()
    }

    suspend fun markAssignmentAsCompleted(assignmentId: String): Result<Unit> {
        return try {
            completeAssignmentUseCase(assignmentId)
        } catch (e: Exception) {
            Log.e("CameraOCRViewModel", "Error completing assignment", e)
            Result.failure(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
