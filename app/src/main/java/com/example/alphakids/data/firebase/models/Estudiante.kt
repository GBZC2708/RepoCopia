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
    @get:PropertyName("id_tutor")
    val idTutor: String = "",
    @get:PropertyName("id_docente")
    val idDocente: String = "",
    @get:PropertyName("id_institucion")
    val idInstitucion: String = "",
    @get:PropertyName("foto_perfil")
    val fotoPerfil: String? = null,
    @get:PropertyName("fecha_registro") @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
