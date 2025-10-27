package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Palabra(
    @DocumentId
    var id: String = "",

    var texto: String = "",
    var categoria: String = "",

    @get:PropertyName("nivelDificultad")
    @set:PropertyName("nivelDificultad")
    var nivelDificultad: String = "",

    @get:PropertyName("imagen")
    @set:PropertyName("imagen")
    var imagen: String = "",

    var audio: String = "",

    @get:PropertyName("fechaCreacion")
    @set:PropertyName("fechaCreacion")
    @ServerTimestamp
    var fechaCreacion: Timestamp? = null,

    @get:PropertyName("creadoPor")
    @set:PropertyName("creadoPor")
    var creadoPor: String? = null
)
