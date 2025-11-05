package com.example.alphakids.data.demo

import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.WordResult
import com.example.alphakids.domain.repository.WordSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Normalizer
import java.util.Locale
import java.util.UUID

class DemoWordRepository : WordRepository {

    override suspend fun createWord(word: Word): WordResult {
        val id = if (word.id.isBlank()) "demo_word_${UUID.randomUUID()}" else word.id
        val creator = word.creadoPor ?: DemoDataStore.currentUser.value?.id ?: "demo_teacher"
        val now = System.currentTimeMillis()

        val demoWord = DemoDataStore.DemoWord(
            id = id,
            text = word.texto.ifBlank { "Nueva palabra" },
            category = word.categoria.ifBlank { "General" },
            difficulty = word.nivelDificultad.ifBlank { "Fácil" },
            rewardCoins = word.recompensaMonedas.coerceAtLeast(1),
            imageUrl = word.imagenUrl,
            createdBy = creator,
            createdAt = word.fechaCreacionMillis ?: now,
            updatedAt = now,
            audioUrl = word.audioUrl
        )

        DemoDataStore.saveWord(demoWord)
        return Result.success(demoWord.id)
    }

    override suspend fun updateWord(word: Word): Result<Unit> {
        if (word.id.isBlank()) {
            return Result.failure(IllegalArgumentException("El ID de la palabra es obligatorio"))
        }

        val current = DemoDataStore.words.value[word.id]
            ?: return Result.failure(IllegalArgumentException("Palabra no encontrada"))

        val updated = current.copy(
            text = word.texto.ifBlank { current.text },
            category = word.categoria.ifBlank { current.category },
            difficulty = word.nivelDificultad.ifBlank { current.difficulty },
            rewardCoins = word.recompensaMonedas.coerceAtLeast(1),
            imageUrl = word.imagenUrl.ifBlank { current.imageUrl },
            updatedAt = System.currentTimeMillis(),
            audioUrl = word.audioUrl.ifBlank { current.audioUrl }
        )

        DemoDataStore.saveWord(updated)
        return Result.success(Unit)
    }

    override suspend fun deleteWord(wordId: String): Result<Unit> {
        DemoDataStore.deleteWord(wordId)
        return Result.success(Unit)
    }

    override fun getWordsByDocente(
        docenteId: String,
        sortBy: WordSortOrder
    ): Flow<List<Word>> {
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values
                .filter { it.createdBy == docenteId }
                .sortAndMap(sortBy)
        }
    }

    override fun getAllWords(sortBy: WordSortOrder): Flow<List<Word>> {
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values.sortAndMap(sortBy)
        }
    }

    override suspend fun searchWordsByText(
        query: String,
        docenteId: String?
    ): Flow<List<Word>> {
        val normalized = query.normalize()
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values
                .asSequence()
                .filter { docenteId == null || it.createdBy == docenteId }
                .filter { it.text.normalize().contains(normalized) }
                .map { it.toDomain() }
                .sortedBy { it.texto }
                .toList()
        }
    }

    override fun getWordsByCategories(
        categories: List<String>,
        sortBy: WordSortOrder
    ): Flow<List<Word>> {
        val normalized = categories.map { it.normalize() }.toSet()
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values
                .filter { normalized.contains(it.category.normalize()) }
                .sortAndMap(sortBy)
        }
    }

    override fun getWordsByDifficulties(
        difficulties: List<String>,
        sortBy: WordSortOrder
    ): Flow<List<Word>> {
        val normalized = difficulties.map { it.normalize() }.toSet()
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values
                .filter { normalized.contains(it.difficulty.normalize()) }
                .sortAndMap(sortBy)
        }
    }

    override fun getFilteredWords(
        docenteId: String?,
        categoria: String?,
        dificultad: String?,
        sortBy: WordSortOrder
    ): Flow<List<Word>> {
        val normalizedCategory = categoria?.normalize()
        val normalizedDifficulty = dificultad?.normalize()
        return DemoDataStore.words.map { wordsMap ->
            wordsMap.values
                .asSequence()
                .filter { docenteId == null || it.createdBy == docenteId }
                .filter { normalizedCategory == null || it.category.normalize() == normalizedCategory }
                .filter { normalizedDifficulty == null || it.difficulty.normalize() == normalizedDifficulty }
                .toList()
                .sortAndMap(sortBy)
        }
    }

    override suspend fun findWordByTextOptions(options: List<String>): Word? {
        val normalizedOptions = options.map { it.normalize() }
        return DemoDataStore.words.value.values.firstOrNull { demoWord ->
            normalizedOptions.any { option -> demoWord.text.normalize() == option }
        }?.toDomain()
    }

    private fun Collection<DemoDataStore.DemoWord>.sortAndMap(order: WordSortOrder): List<Word> {
        val sorted = when (order) {
            WordSortOrder.TEXT_ASC -> this.sortedBy { it.text.lowercase(Locale.getDefault()) }
            WordSortOrder.TEXT_DESC -> this.sortedByDescending { it.text.lowercase(Locale.getDefault()) }
            WordSortOrder.DATE_CREATED_ASC -> this.sortedBy { it.createdAt }
            WordSortOrder.DATE_CREATED_DESC -> this.sortedByDescending { it.createdAt }
        }
        return sorted.map { it.toDomain() }
    }

    private fun String.normalize(): String {
        val lower = lowercase(Locale.getDefault()).trim()
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}

