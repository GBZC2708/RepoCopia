package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Docente(
    @DocumentId
    val uid: String = "",

    @PropertyName("id_institucion")
    val idInstitucion: String = "",

    val seccion: String = "",
    val grado: String = "",

    @PropertyName("fecha_registro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
