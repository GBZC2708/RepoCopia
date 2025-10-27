package com.example.alphakids.domain.models

data class GameSession(
    val id: String,
    val idAsignacion: String,
    val idPalabra: String,
    val palabraTexto: String,
    val nivelDificultad: String,
    val fechaInicioMillis: Long?,
    val fechaFinMillis: Long?,
    val tiempoTranscurrido: Long,
    val intentosFallidos: Int,
    val intentosExitosos: Int,
    val resultado: String,
    val puntuacion: Int
)
