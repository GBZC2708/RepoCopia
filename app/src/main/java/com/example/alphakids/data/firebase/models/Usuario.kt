package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Usuario(

    @DocumentId
    val uid: String = "",

    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",

    @PropertyName("foto_perfil")
    val fotoPerfil: String = "",

    val rol: String = "", // "tutor" | "docente" | "admin"

    val estado: String = "activo", // "activo" | "inactivo" | "pendiente"

    @PropertyName("creado_en")
    @ServerTimestamp
    val creadoEn: Timestamp? = null,

    @PropertyName("actualizado_en")
    @ServerTimestamp
    val actualizadoEn: Timestamp? = null
)
