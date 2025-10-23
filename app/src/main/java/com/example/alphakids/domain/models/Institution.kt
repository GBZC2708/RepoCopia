package com.example.alphakids.domain.models

data class Institution(
    val id: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String,
    val fechaCreacionMillis: Long?
)
