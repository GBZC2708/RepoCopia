package com.example.alphakids.domain.models

data class StudentStats(
    val totalPartidas: Long,
    val promedioPuntuacion: Double,
    val promedioIntentosExitosos: Double,
    val promedioTiempoPartida: Double,
    val palabrasAprendidas: Long,
    val ultimaActividadMillis: Long?
)
