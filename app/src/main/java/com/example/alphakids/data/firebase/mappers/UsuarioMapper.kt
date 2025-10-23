package com.example.alphakids.data.firebase.mappers

import com.example.alphakids.data.firebase.models.Usuario
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus

object UsuarioMapper {

    /**
     * Convierte Usuario de Firestore a modelo de dominio User
     */
    fun toDomain(dto: Usuario): User {
        return User(
            uid = dto.uid,
            nombre = dto.nombre,
            apellido = dto.apellido,
            email = dto.email,
            telefono = dto.telefono.takeIf { it.isNotBlank() },
            fotoPerfil = dto.fotoPerfil.takeIf { it.isNotBlank() },
            rol = UserRole.valueOf(dto.rol.uppercase()),
            estado = UserStatus.valueOf(dto.estado.uppercase()),
            creadoEn = dto.creadoEn?.toDate()?.time,
            actualizadoEn = dto.actualizadoEn?.toDate()?.time
        )
    }

    /**
     * Convierte modelo de dominio User a Usuario para Firestore
     */
    fun fromDomain(user: User): Usuario {
        return Usuario(
            uid = user.uid,
            nombre = user.nombre,
            apellido = user.apellido,
            email = user.email,
            telefono = user.telefono ?: "",
            fotoPerfil = user.fotoPerfil ?: "",
            rol = user.rol.name.lowercase(),
            estado = user.estado.name.lowercase()
        )
    }
}
