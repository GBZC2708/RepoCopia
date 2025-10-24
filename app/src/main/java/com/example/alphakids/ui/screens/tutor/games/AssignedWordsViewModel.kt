package com.example.alphakids.ui.screens.tutor.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AssignedWordsUiState(
    val isLoading: Boolean = false,
    val assignedWords: List<AsignacionPalabra> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AssignedWordsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignedWordsUiState())
    val uiState: StateFlow<AssignedWordsUiState> = _uiState.asStateFlow()

    fun loadAssignedWords(studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val assignments = firestore.collection("asignaciones_palabras")
                    .whereEqualTo("id_estudiante", studentId)
                    .whereEqualTo("estado", "activa") // Solo palabras activas
                    .get()
                    .await()
                    .toObjects(AsignacionPalabra::class.java)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignedWords = assignments
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}