package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.PalabraEncontrada
import com.example.alphakids.domain.models.DiscoveredWord

object DiscoveredWordMapper {
    fun toDomain(dto: PalabraEncontrada): DiscoveredWord {
        return DiscoveredWord(
            id = dto.id,
            studentId = dto.studentId,
            wordId = dto.wordId,
            wordText = dto.wordText,
            discoveredAtMillis = dto.timestamp?.toDate()?.time
        )
    }

    fun fromDomain(model: DiscoveredWord): PalabraEncontrada {
        return PalabraEncontrada(
            id = model.id,
            studentId = model.studentId,
            wordId = model.wordId,
            wordText = model.wordText
        )
    }
}
