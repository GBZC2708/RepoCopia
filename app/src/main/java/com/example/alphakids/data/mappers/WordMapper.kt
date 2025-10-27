package com.example.alphakids.data.mappers

import com.example.alphakids.data.firebase.models.Palabra as PalabraDto
import com.example.alphakids.domain.models.Word
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WordMapper {

    fun toDomain(dto: PalabraDto): Word {
        val sanitizedImageUrl = sanitizeFirebaseUrl(dto.imagen)
        return Word(
            id = dto.id,
            texto = dto.texto,
            categoria = dto.categoria,
            nivelDificultad = dto.nivelDificultad,
            imagenUrl = sanitizedImageUrl,
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

    private const val STORAGE_BUCKET = "alphakids-tecsup.firebasestorage.app"

    private fun sanitizeFirebaseUrl(rawUrl: String?): String {
        if (rawUrl.isNullOrBlank()) {
            // Sin URL almacenada devolvemos cadena vacía para que la UI muestre el ícono por defecto.
            return ""
        }

        val trimmed = rawUrl.trim()
        if (trimmed.startsWith("http", ignoreCase = true)) {
            // Si ya viene una URL completa simplemente la reutilizamos.
            return trimmed
        }

        val normalizedPath = trimmed.removePrefix("/")
        val encodedPath = URLEncoder.encode(normalizedPath, StandardCharsets.UTF_8.toString())
        return "https://firebasestorage.googleapis.com/v0/b/$STORAGE_BUCKET/o/$encodedPath?alt=media"
    }
}
