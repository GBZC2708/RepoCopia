package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa el documento en la colección `tutores/{id}`
 */
data class Tutor(

    @DocumentId
    val id: String = "",

    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",

    @PropertyName("uid_usuario")
    val uidUsuario: String = "", // referencia al documento usuarios/{uid}

    @PropertyName("fecha_registro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
