package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class AsignacionPalabra(
    @DocumentId
    val id: String = "",
    @PropertyName("id_docente")
    val idDocente: String = "",
    @PropertyName("id_estudiante")
    val idEstudiante: String = "",
    @PropertyName("id_palabra")
    val idPalabra: String = "",
    @PropertyName("palabra_texto")
    val palabraTexto: String = "",
    @PropertyName("palabra_imagen")
    val palabraImagen: String? = null,
    @PropertyName("palabra_audio")
    val palabraAudio: String? = null,
    @PropertyName("palabra_dificultad")
    val palabraDificultad: String = "",
    @PropertyName("estudiante_nombre")
    val estudianteNombre: String? = null,
    @PropertyName("fecha_asignacion") @ServerTimestamp
    val fechaAsignacion: Timestamp? = null,
    @PropertyName("fecha_limite")
    val fechaLimite: Timestamp? = null,
    val estado: String = ""
)
