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
    val grado: String? = null,
    val seccion: String? = null,
    @get:PropertyName("id_tutor") @set:PropertyName("id_tutor")
    val idTutor: String = "",
    @get:PropertyName("id_docente") @set:PropertyName("id_docente")
    val idDocente: String? = null,
    @get:PropertyName("id_institucion") @set:PropertyName("id_institucion")
    val idInstitucion: String? = null,
    @get:PropertyName("institucion") @set:PropertyName("institucion")
    val institucion: String? = null,
    @get:PropertyName("grado") @set:PropertyName("grado")
    val gradoAcademico: String? = null,
    @get:PropertyName("seccion") @set:PropertyName("seccion")
    val seccionAcademica: String? = null,
    @get:PropertyName("docenteId") @set:PropertyName("docenteId")
    val docenteId: String? = null,
    @get:PropertyName("foto_perfil") @set:PropertyName("foto_perfil")
    val fotoPerfil: String? = null,
    @get:PropertyName("fecha_registro") @set:PropertyName("fecha_registro") @ServerTimestamp
    val fechaRegistro: Timestamp? = null,
    @get:PropertyName("monedas") @set:PropertyName("monedas")
    val monedas: Int = 0
)
