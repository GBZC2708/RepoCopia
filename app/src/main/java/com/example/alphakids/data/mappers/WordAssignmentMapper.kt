package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.domain.models.WordAssignment

object WordAssignmentMapper {

    fun toDomain(dto: AsignacionPalabra): WordAssignment {
        return WordAssignment(
            id = dto.id,
            idDocente = dto.idDocente,
            idEstudiante = dto.idEstudiante,
            idPalabra = dto.idPalabra,
            palabraTexto = dto.palabraTexto,
            palabraImagenUrl = dto.palabraImagen,
            palabraAudioUrl = dto.palabraAudio,
            palabraDificultad = dto.palabraDificultad,
            estudianteNombre = dto.estudianteNombre,
            fechaAsignacionMillis = dto.fechaAsignacion?.toDate()?.time,
            fechaLimiteMillis = dto.fechaLimite?.toDate()?.time,
            estado = dto.estado
        )
    }

    fun fromDomain(model: WordAssignment): AsignacionPalabra {
        return AsignacionPalabra(
            id = model.id,
            idDocente = model.idDocente,
            idEstudiante = model.idEstudiante,
            idPalabra = model.idPalabra,
            palabraTexto = model.palabraTexto,
            palabraImagen = model.palabraImagenUrl,
            palabraAudio = model.palabraAudioUrl,
            palabraDificultad = model.palabraDificultad,
            estudianteNombre = model.estudianteNombre,
            fechaAsignacion = null,
            fechaLimite = null,
            estado = model.estado
        )
    }
}
