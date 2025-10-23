package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Palabra(
    @DocumentId
    val id: String = "",
    val texto: String = "",
    val categoria: String = "",
    @PropertyName("nivel_dificultad")
    val nivelDificultad: String = "",
    val imagen: String = "",
    val audio: String = "",
    @PropertyName("fecha_creacion") @ServerTimestamp
    val fechaCreacion: Timestamp? = null,
    @PropertyName("creado_por")
    val creadoPor: String? = null
)
