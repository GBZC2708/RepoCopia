package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Partida(
    @DocumentId
    val id: String = "",
    @PropertyName("id_asignacion")
    val idAsignacion: String = "",
    @PropertyName("id_palabra")
    val idPalabra: String = "",
    @PropertyName("palabra_texto")
    val palabraTexto: String = "",
    @PropertyName("nivel_dificultad")
    val nivelDificultad: String = "",
    @PropertyName("fecha_inicio")
    val fechaInicio: Timestamp? = null,
    @PropertyName("fecha_fin")
    val fechaFin: Timestamp? = null,
    @PropertyName("tiempo_transcurrido")
    val tiempoTranscurrido: Long = 0,
    @PropertyName("intentos_fallidos")
    val intentosFallidos: Int = 0,
    @PropertyName("intentos_exitosos")
    val intentosExitosos: Int = 0,
    val resultado: String = "",
    val puntuacion: Int = 0
)
