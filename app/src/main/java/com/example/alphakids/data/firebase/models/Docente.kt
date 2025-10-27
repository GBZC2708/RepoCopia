package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Docente(
    @DocumentId
    val uid: String = "",

    @PropertyName("idInstitucion")
    val idInstitucion: String = "",

    val seccion: String = "",
    val grado: String = "",

    @PropertyName("fechaRegistro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
