package com.example.alphakids.ui.screens.teacher.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeacherHomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Obtener el usuario actual directamente del repositorio
    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Obtener el nombre completo del profesor
    val teacherName: StateFlow<String> = currentUser
        .map { user ->
            if (user != null) {
                "${user.nombre} ${user.apellido}"
            } else {
                "Profesor"
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Profesor"
        )
}