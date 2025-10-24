package com.example.alphakids.ui.screens.tutor.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordPuzzleUiState(
    val isLoading: Boolean = false,
    val wordIcon: ImageVector = Icons.Rounded.School,
    val error: String? = null
)

@HiltViewModel
class WordPuzzleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WordPuzzleUiState())
    val uiState: StateFlow<WordPuzzleUiState> = _uiState.asStateFlow()

    fun loadWordData(assignment: AsignacionPalabra) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Aquí podrías cargar la imagen desde Firebase Storage si es necesario
                // Por ahora usamos un icono por defecto
                val icon = getIconForWord(assignment.palabraTexto)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wordIcon = icon
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    private fun getIconForWord(word: String): ImageVector {
        // Aquí puedes mapear palabras específicas a iconos específicos
        return when (word.lowercase()) {
            "casa" -> Icons.Rounded.School // Placeholder
            else -> Icons.Rounded.School
        }
    }
}