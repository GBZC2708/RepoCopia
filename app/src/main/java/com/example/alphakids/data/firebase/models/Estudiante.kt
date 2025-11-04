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

    // Campos “planos” (mantén si ya los usas en Firestore)
    val grado: String? = null,
    val seccion: String? = null,

    @get:PropertyName("id_tutor")
    val idTutor: String = "",

    @get:PropertyName("id_docente")
    val idDocente: String? = null,

    @get:PropertyName("id_institucion")
    val idInstitucion: String? = null,

    // Aliases opcionales (si tienes documentos con estos nombres de campo)
    @get:PropertyName("institucion")
    val institucion: String? = null,

    @get:PropertyName("grado")
    val gradoAcademico: String? = null,

    @get:PropertyName("seccion")
    val seccionAcademica: String? = null,

    @get:PropertyName("docenteId")
    val docenteId: String? = null,

    @get:PropertyName("foto_perfil")
    val fotoPerfil: String? = null,

    @get:PropertyName("fecha_registro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null,

    @get:PropertyName("monedas")
    val monedas: Int = 0
)
