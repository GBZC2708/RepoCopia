package com.example.alphakids.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.LoginUserUseCase
import com.example.alphakids.domain.usecases.LogoutUserUseCase
import com.example.alphakids.domain.usecases.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun register(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            registerUserUseCase(nombre, apellido, email, clave, telefono, rol)
                .collect { result ->
                    if (result.isSuccess) {
                        _authUiState.value = AuthUiState.Success(result.getOrNull()!!)
                    } else {
                        _authUiState.value = AuthUiState.Error(
                            result.exceptionOrNull()?.message ?: "Error desconocido"
                        )
                    }
                }
        }
    }

    fun login(email: String, clave: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            loginUserUseCase(email, clave)
                .collect { result ->
                    if (result.isSuccess) {
                        _authUiState.value = AuthUiState.Success(result.getOrNull()!!)
                    } else {
                        _authUiState.value = AuthUiState.Error(
                            result.exceptionOrNull()?.message ?: "Correo o contraseña incorrectos"
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUserUseCase()
        }
    }

    fun resetAuthState() {
        _authUiState.value = AuthUiState.Idle
    }
}
