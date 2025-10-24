package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.AsignacionPalabra as AsignacionDto
import com.example.alphakids.domain.models.WordAssignment
import com.google.firebase.Timestamp

object AssignmentMapper {

    fun toDomain(dto: AsignacionDto): WordAssignment {
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

    fun fromDomain(domain: WordAssignment): AsignacionDto {
        return AsignacionDto(
            id = domain.id,
            idDocente = domain.idDocente,
            idEstudiante = domain.idEstudiante,
            idPalabra = domain.idPalabra,
            palabraTexto = domain.palabraTexto,
            palabraImagen = domain.palabraImagenUrl,
            palabraAudio = domain.palabraAudioUrl,
            palabraDificultad = domain.palabraDificultad,
            estudianteNombre = domain.estudianteNombre,
            // fechaAsignacion se maneja con @ServerTimestamp
            fechaLimite = domain.fechaLimiteMillis?.let { Timestamp(it / 1000, 0) },
            estado = domain.estado
        )
    }
}
