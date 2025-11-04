package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.Student

object StudentMapper {

    fun toDomain(dto: Estudiante): Student {
        return Student(
            id = dto.id,
            nombre = dto.nombre,
            apellido = dto.apellido,
            edad = dto.edad,
            // Compatibilidad con documentos antiguos donde grado/sección se almacenaron con otras claves.
            grado = dto.grado ?: dto.gradoAcademico ?: "",
            seccion = dto.seccion ?: dto.seccionAcademica ?: "",
            idTutor = dto.idTutor,
            idDocente = dto.idDocente ?: dto.docenteId ?: "",
            idInstitucion = dto.idInstitucion ?: "",
            institucion = dto.institucion,
            gradoAcademico = dto.gradoAcademico ?: dto.grado,
            seccionAcademica = dto.seccionAcademica ?: dto.seccion,
            docenteId = dto.docenteId ?: dto.idDocente,
            fotoPerfilUrl = dto.fotoPerfil,
            fechaRegistroMillis = dto.fechaRegistro?.toDate()?.time,
            monedas = dto.monedas
        )
    }

    fun fromDomain(model: Student): Estudiante {
        return Estudiante(
            id = model.id,
            nombre = model.nombre,
            apellido = model.apellido,
            edad = model.edad,
            grado = model.grado,
            seccion = model.seccion,
            idTutor = model.idTutor,
            idDocente = when {
                model.idDocente.isNotBlank() -> model.idDocente
                !model.docenteId.isNullOrBlank() -> model.docenteId
                else -> null
            },
            idInstitucion = model.idInstitucion.takeIf { it.isNotBlank() },
            institucion = model.institucion,
            gradoAcademico = (model.gradoAcademico ?: model.grado).takeIf { it.isNotBlank() },
            seccionAcademica = (model.seccionAcademica ?: model.seccion).takeIf { it.isNotBlank() },
            docenteId = when {
                !model.docenteId.isNullOrBlank() -> model.docenteId
                model.idDocente.isNotBlank() -> model.idDocente
                else -> null
            },
            fotoPerfil = model.fotoPerfilUrl,
            fechaRegistro = null,
            monedas = model.monedas
        )
    }
}
