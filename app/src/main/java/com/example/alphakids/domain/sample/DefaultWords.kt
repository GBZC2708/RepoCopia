package com.example.alphakids.domain.sample

import com.example.alphakids.domain.models.Word
import java.text.Normalizer

/**
 * Default dictionary entries that are bundled with the app so every profile
 * can practice the Discover game without depending on remote data.
 */
object DefaultWords {
    val discoverWords: List<Word> = listOf(
        Word(
            id = "sample_word_sol",
            texto = "Sol",
            categoria = "Naturaleza",
            nivelDificultad = "Fácil",
            imagenUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=400",
            audioUrl = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3",
            recompensaMonedas = 5,
            fechaCreacionMillis = System.currentTimeMillis(),
            creadoPor = "demo"
        ),
        Word(
            id = "sample_word_luna",
            texto = "Luna",
            categoria = "Naturaleza",
            nivelDificultad = "Fácil",
            imagenUrl = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=400",
            audioUrl = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3",
            recompensaMonedas = 5,
            fechaCreacionMillis = System.currentTimeMillis(),
            creadoPor = "demo"
        ),
        Word(
            id = "sample_word_gato",
            texto = "Gato",
            categoria = "Animales",
            nivelDificultad = "Intermedio",
            imagenUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400",
            audioUrl = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3",
            recompensaMonedas = 5,
            fechaCreacionMillis = System.currentTimeMillis(),
            creadoPor = "demo"
        ),
        Word(
            id = "sample_word_flor",
            texto = "Flor",
            categoria = "Naturaleza",
            nivelDificultad = "Intermedio",
            imagenUrl = "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?w=400",
            audioUrl = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3",
            recompensaMonedas = 5,
            fechaCreacionMillis = System.currentTimeMillis(),
            creadoPor = "demo"
        ),
        Word(
            id = "sample_word_libro",
            texto = "Libro",
            categoria = "Objetos",
            nivelDificultad = "Difícil",
            imagenUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
            audioUrl = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3",
            recompensaMonedas = 5,
            fechaCreacionMillis = System.currentTimeMillis(),
            creadoPor = "demo"
        )
    )

    fun findById(id: String?): Word? = discoverWords.firstOrNull { it.id == id }

    fun findByText(text: String): Word? {
        if (text.isBlank()) return null
        val normalized = normalize(text)
        return discoverWords.firstOrNull { normalize(it.texto) == normalized }
    }

    private fun normalize(value: String): String {
        val lower = value.trim().lowercase()
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}
