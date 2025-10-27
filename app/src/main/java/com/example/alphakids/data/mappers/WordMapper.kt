package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Palabra as PalabraDto
import com.example.alphakids.domain.models.Word

object WordMapper {

    fun toDomain(dto: PalabraDto): Word {
        return Word(
            id = dto.id,
            texto = dto.texto,
            categoria = dto.categoria,
            nivelDificultad = dto.nivelDificultad,
            imagenUrl = dto.imagen,
            audioUrl = dto.audio,
            fechaCreacionMillis = dto.fechaCreacion?.toDate()?.time,
            creadoPor = dto.creadoPor
        )
    }

    fun fromDomain(domain: Word): PalabraDto {
        return PalabraDto(
            id = domain.id,
            texto = domain.texto,
            categoria = domain.categoria,
            nivelDificultad = domain.nivelDificultad,
            imagen = domain.imagenUrl,
            audio = domain.audioUrl,
            creadoPor = domain.creadoPor
            // fechaCreacion se establece por @ServerTimestamp
        )
    }
}
