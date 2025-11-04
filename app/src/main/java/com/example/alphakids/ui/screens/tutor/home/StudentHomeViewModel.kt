package com.example.alphakids.ui.screens.tutor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.usecases.ObserveStudentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Administra la información mostrada en la pantalla principal del estudiante.
 * Se suscribe al perfil para reflejar cambios de monedas o datos en tiempo real.
 */
@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    private val observeStudentByIdUseCase: ObserveStudentByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentHomeUiState())
    val uiState: StateFlow<StudentHomeUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    /**
     * Inicia la observación del perfil solicitado; si ya estamos escuchando el mismo
     * estudiante evitamos recrear el flujo para conservar recursos.
     */
    fun loadStudent(studentId: String) {
        if (_uiState.value.studentId == studentId && observeJob?.isActive == true) {
            return
        }

        observeJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null, studentId = studentId) }

        observeJob = viewModelScope.launch {
            observeStudentByIdUseCase(studentId).collectLatest { student ->
                if (student == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No pudimos cargar el perfil seleccionado.",
                            studentName = null,
                            coins = null,
                            profileLabel = null
                        )
                    }
                } else {
                    _uiState.update { it.toSuccessState(student) }
                }
            }
        }
    }

    /** Restablece el último intento de carga para que la UI pueda reintentar. */
    fun retry() {
        _uiState.value.studentId?.let { loadStudent(it) }
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }
}

/** Estado inmutable consumido por la pantalla principal del estudiante. */
data class StudentHomeUiState(
    val studentId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val studentName: String? = null,
    val profileLabel: String? = null,
    val coins: Int? = null
) {
    /**
     * Construye un estado exitoso a partir del modelo de datos recibido.
     */
    fun toSuccessState(student: Estudiante): StudentHomeUiState {
        val formattedProfile = listOfNotNull(student.grado, student.seccion)
            .filter { it.isNotBlank() }
            .joinToString(separator = " - ")
            .ifBlank { null }

        return copy(
            isLoading = false,
            error = null,
            studentName = student.nombre,
            profileLabel = formattedProfile,
            coins = student.monedas
        )
    }
}
