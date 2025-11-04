package com.example.alphakids.domain.models

data class Student(
    val id: String,
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val grado: String,
    val seccion: String,
    val idTutor: String,
    val idDocente: String,
    val idInstitucion: String,
    val institucion: String? = null,
    val gradoAcademico: String? = null,
    val seccionAcademica: String? = null,
    val docenteId: String? = null,
    val fotoPerfilUrl: String?,
    val fechaRegistroMillis: Long?
)
