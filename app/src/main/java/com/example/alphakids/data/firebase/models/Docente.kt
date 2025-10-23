package com.example.alphakids.data.firebase.models


import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Representa el documento en la colección `docentes/{id}`
 */
data class Docente(

    @DocumentId
    val id: String = "",

    val nombre: String = "",
    val correo: String = "",

    @PropertyName("id_institucion")
    val idInstitucion: String = "",

    val seccion: String = "",
    val grado: String = "",

    @PropertyName("foto_perfil")
    val fotoPerfil: String = "",

    @PropertyName("uid_usuario")
    val uidUsuario: String = "", // referencia al documento usuarios/{uid}

    @PropertyName("fecha_registro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
