package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class EstadisticasEstudiante(
    @PropertyName("total_partidas")
    val totalPartidas: Long = 0,
    @PropertyName("promedio_puntuacion")
    val promedioPuntuacion: Double = 0.0,
    @PropertyName("promedio_intentos_exitosos")
    val promedioIntentosExitosos: Double = 0.0,
    @PropertyName("promedio_tiempo_partida")
    val promedioTiempoPartida: Double = 0.0,
    @PropertyName("palabras_aprendidas")
    val palabrasAprendidas: Long = 0,
    @PropertyName("ultima_actividad")
    val ultimaActividad: Timestamp? = null
)
