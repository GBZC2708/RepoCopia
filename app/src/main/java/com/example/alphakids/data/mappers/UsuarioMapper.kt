package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Usuario
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus
import java.util.Date

object UsuarioMapper {

    fun toDomain(dto: Usuario): User {
        return User(
            uid = dto.uid,
            nombre = dto.nombre,
            apellido = dto.apellido,
            email = dto.email,
            telefono = dto.telefono.takeIf { it.isNotBlank() },
            fotoPerfil = dto.fotoPerfil.takeIf { it.isNotBlank() },
            rol = when (dto.rol) {
                "docente" -> UserRole.DOCENTE
                "tutor" -> UserRole.TUTOR
                "admin" -> UserRole.ADMIN
                else -> UserRole.UNKNOWN
            },
            estado = when (dto.estado) {
                "activo" -> UserStatus.ACTIVO
                "inactivo" -> UserStatus.INACTIVO
                "pendiente" -> UserStatus.PENDIENTE
                else -> UserStatus.UNKNOWN
            },
            creadoEn = dto.creadoEn?.toDate()?.time,
            actualizadoEn = dto.actualizadoEn?.toDate()?.time
        )
    }
}
