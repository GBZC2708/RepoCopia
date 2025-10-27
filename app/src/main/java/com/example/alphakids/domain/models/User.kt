package com.example.alphakids.domain.models

data class User(
    val uid: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val fotoPerfil: String? = null,
    val rol: UserRole,
    val estado: UserStatus,
    val creadoEn: Long? = null,
    val actualizadoEn: Long? = null
)
