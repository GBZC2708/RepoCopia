package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Estudiante(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val edad: Int = 0,
    val grado: String = "",
    val seccion: String = "",
    @get:PropertyName("idTutor")
    val idTutor: String = "",
    @get:PropertyName("idDocente")
    val idDocente: String = "",
    @get:PropertyName("idInstitucion")
    val idInstitucion: String = "",
    @get:PropertyName("fotoPerfil")
    val fotoPerfil: String? = null,
    @get:PropertyName("fechaRegistro") @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
