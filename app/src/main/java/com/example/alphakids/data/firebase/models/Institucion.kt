package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Institucion(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val correo: String = "",

    @PropertyName("fecha_creacion")
    @ServerTimestamp
    val fechaCreacion: Timestamp? = null
)
