package com.example.alphakids.ui.student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.usecases.CreateStudentUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val createStudentUseCase: CreateStudentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStudentsUseCase: GetStudentsUseCase
) : ViewModel() {

    private val _createUiState = MutableStateFlow<StudentUiState>(StudentUiState.Idle)
    val createUiState: StateFlow<StudentUiState> = _createUiState.asStateFlow()

    private val tutorIdFlow: Flow<String?> = getCurrentUserUseCase()
        .map { it?.uid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Estudiante>> = tutorIdFlow
        .flatMapLatest { tutorId ->
            if (tutorId != null) {
                getStudentsUseCase(tutorId)
                    .catch { e ->
                        Log.e("StudentViewModel", "Error fetching students", e)
                        emit(emptyList())
                    }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun createStudent(
        nombre: String,
        apellido: String,
        edad: Int,
        grado: String,
        seccion: String,
        idInstitucion: String
    ) {
        viewModelScope.launch {
            _createUiState.value = StudentUiState.Loading

            val currentUser = getCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _createUiState.value = StudentUiState.Error("No se pudo obtener el usuario actual.")
                return@launch
            }
            val tutorId = currentUser.uid

            val nuevoEstudiante = Estudiante(
                nombre = nombre,
                apellido = apellido,
                edad = edad,
                grado = grado,
                seccion = seccion,
                idTutor = tutorId,
                idInstitucion = idInstitucion,
                idDocente = "",
                fotoPerfil = null
            )

            Log.d("StudentViewModel", "Intentando crear estudiante: ${nuevoEstudiante.nombre} con idTutor: $tutorId")

            val result = createStudentUseCase(nuevoEstudiante)

            if (result.isSuccess) {
                _createUiState.value = StudentUiState.Success(result.getOrNull() ?: "unknown_id")
            } else {
                _createUiState.value = StudentUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido al crear perfil."
                )
            }
        }
    }

    fun resetCreateState() {
        _createUiState.value = StudentUiState.Idle
    }
}
