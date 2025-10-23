package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, clave: String) = repository.login(email, clave)
}
