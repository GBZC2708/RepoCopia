package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.User
import kotlinx.coroutines.flow.Flow

typealias AuthResult = Flow<Result<User>>

interface AuthRepository {

    fun getCurrentUser(): Flow<User?>

    fun register(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ): AuthResult

    fun login(email: String, clave: String): AuthResult

    suspend fun logout()
}
