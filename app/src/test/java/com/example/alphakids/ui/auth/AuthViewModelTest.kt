package com.example.alphakids.ui.auth

import com.example.alphakids.fakes.FakeAuthRepository
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.LoginUserUseCase
import com.example.alphakids.domain.usecases.LogoutUserUseCase
import com.example.alphakids.domain.usecases.RegisterUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @Test
    fun registerUpdatesUiState() = runTest {
        val repository = FakeAuthRepository()
        val viewModel = AuthViewModel(
            RegisterUserUseCase(repository),
            LoginUserUseCase(repository),
            LogoutUserUseCase(repository),
            GetCurrentUserUseCase(repository)
        )

        viewModel.register(
            nombre = "Ana",
            apellido = "Lopez",
            email = "ana@test.com",
            clave = "123456",
            telefono = "", 
            rol = "tutor"
        )

        advanceUntilIdle()

        assertIs<AuthUiState.Success>(viewModel.authUiState.value)
        val currentUser = viewModel.currentUser.value
        assertNotNull(currentUser)
    }
}
