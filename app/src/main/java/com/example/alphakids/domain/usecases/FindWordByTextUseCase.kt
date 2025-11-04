package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordRepository
import java.text.Normalizer
import javax.inject.Inject

class FindWordByTextUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(text: String): Word? {
        if (text.isBlank()) return null

        val normalized = normalize(text)
        val options = buildList {
            add(text.trim())
            add(normalized)
            add(normalized.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
            add(normalized.uppercase())
        }

        return repository.findWordByTextOptions(options)
    }

    private fun normalize(text: String): String {
        val lower = text.lowercase()
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}
