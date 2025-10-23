package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ) = repository.register(nombre, apellido, email, clave, telefono, rol)
}
