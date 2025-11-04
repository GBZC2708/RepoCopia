package com.example.alphakids.domain.models

data class Word(
    val id: String,
    val texto: String,
    val categoria: String,
    val nivelDificultad: String,
    val imagenUrl: String,
    val audioUrl: String,
    val recompensaMonedas: Int = 5,
    val fechaCreacionMillis: Long?,
    val creadoPor: String?
)