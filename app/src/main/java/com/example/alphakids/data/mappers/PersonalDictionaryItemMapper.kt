package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.DiccionarioPersonalItem as DictionaryItemDto
import com.example.alphakids.domain.models.PersonalDictionaryItem

object PersonalDictionaryItemMapper {

    fun toDomain(dto: DictionaryItemDto): PersonalDictionaryItem {
        return PersonalDictionaryItem(
            idPalabra = dto.idPalabra,
            texto = dto.texto,
            imagenUrl = dto.imagen,
            audioUrl = dto.audio,
            fechaAgregadoMillis = dto.fechaAgregado?.toDate()?.time,
            ultimoRepasoMillis = dto.ultimoRepaso?.toDate()?.time,
            vecesJugado = dto.veces_jugado,
            vecesAcertado = dto.veces_acertado
        )
    }

    fun fromDomain(domain: PersonalDictionaryItem): DictionaryItemDto {
        return DictionaryItemDto(
            idPalabra = domain.idPalabra,
            texto = domain.texto,
            imagen = domain.imagenUrl,
            audio = domain.audioUrl,
            veces_jugado = domain.vecesJugado,
            veces_acertado = domain.vecesAcertado
        )
    }
}

