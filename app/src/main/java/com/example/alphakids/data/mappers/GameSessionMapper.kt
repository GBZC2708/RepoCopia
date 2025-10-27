package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Partida as PartidaDto
import com.example.alphakids.domain.models.GameSession

object GameSessionMapper {

    fun toDomain(dto: PartidaDto): GameSession {
        return GameSession(
            id = dto.id,
            idAsignacion = dto.idAsignacion,
            idPalabra = dto.idPalabra,
            palabraTexto = dto.palabraTexto,
            nivelDificultad = dto.nivelDificultad,
            fechaInicioMillis = dto.fechaInicio?.toDate()?.time,
            fechaFinMillis = dto.fechaFin?.toDate()?.time,
            tiempoTranscurrido = dto.tiempoTranscurrido,
            intentosFallidos = dto.intentosFallidos,
            intentosExitosos = dto.intentosExitosos,
            resultado = dto.resultado,
            puntuacion = dto.puntuacion
        )
    }

    fun fromDomain(domain: GameSession): PartidaDto {
        return PartidaDto(
            id = domain.id,
            idAsignacion = domain.idAsignacion,
            idPalabra = domain.idPalabra,
            palabraTexto = domain.palabraTexto,
            nivelDificultad = domain.nivelDificultad,
            tiempoTranscurrido = domain.tiempoTranscurrido,
            intentosFallidos = domain.intentosFallidos,
            intentosExitosos = domain.intentosExitosos,
            resultado = domain.resultado,
            puntuacion = domain.puntuacion
        )
    }
}
