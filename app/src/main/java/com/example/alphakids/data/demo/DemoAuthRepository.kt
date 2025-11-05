package com.example.alphakids.data.demo

import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.flow

/**
 * Implementación en memoria de [AuthRepository] que delega en [DemoDataStore]
 * para proveer una experiencia de autenticación instantánea en modo demo.
 */
class DemoAuthRepository : AuthRepository {

    private val currentUserFlow: MutableStateFlow<User?> = MutableStateFlow(null)

    init {
        currentUserFlow.value = DemoDataStore.currentUser.value?.toDomain()
    }

    override fun getCurrentUser(): Flow<User?> =
        DemoDataStore.currentUser
            .map { it?.toDomain() }
            .onStart { emit(currentUserFlow.value) }

    override fun register(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ): Flow<Result<User>> = flow {
        val displayName = listOf(nombre, apellido).filter { it.isNotBlank() }.joinToString(" ")
        val userRole = if (rol.equals("docente", ignoreCase = true)) {
            UserRole.DOCENTE
        } else {
            UserRole.TUTOR
        }
        val domainUser = DemoDataStore.registerUser(displayName, email, clave, userRole)
        currentUserFlow.value = domainUser
        emit(Result.success(domainUser))
    }

    override fun login(email: String, clave: String): Flow<Result<User>> = flow {
        val result = DemoDataStore.login(email, clave)
        if (result.isSuccess) {
            currentUserFlow.value = result.getOrNull()
        }
        emit(result)
    }

    override suspend fun logout() {
        DemoDataStore.logout()
        currentUserFlow.value = null
    }
}

