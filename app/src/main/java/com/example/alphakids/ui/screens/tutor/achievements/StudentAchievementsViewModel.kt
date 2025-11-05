package com.example.alphakids.ui.screens.tutor.achievements

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.Achievement
import com.example.alphakids.domain.usecases.ObserveAchievementsUseCase
import com.example.alphakids.domain.usecases.ObserveStudentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StudentAchievementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeAchievementsUseCase: ObserveAchievementsUseCase,
    observeStudentByIdUseCase: ObserveStudentByIdUseCase
) : ViewModel() {

    private val studentId: String = savedStateHandle.get<String>("studentId")
        ?: throw IllegalArgumentException("Student ID is required")

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val student: StateFlow<Estudiante?> = observeStudentByIdUseCase(studentId)
        .catch { throwable ->
            _error.value = throwable.localizedMessage
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val achievements: StateFlow<List<Achievement>> = observeAchievementsUseCase(studentId)
        .map { items ->
            _isLoading.value = false
            _error.value = null
            items
        }
        .catch { throwable ->
            _isLoading.value = false
            _error.value = throwable.localizedMessage ?: "Error al cargar logros"
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

}

