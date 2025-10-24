package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.Word
import kotlinx.coroutines.flow.Flow

typealias WordResult = Result<String>

interface WordRepository {

    suspend fun createWord(word: Word): WordResult
    suspend fun updateWord(word: Word): Result<Unit>
    suspend fun deleteWord(wordId: String): Result<Unit>

    fun getWordsByDocente(
        docenteId: String,
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>>

    fun getAllWords(
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>>

    suspend fun searchWordsByText(
        query: String,
        docenteId: String? = null
    ): Flow<List<Word>>

    fun getWordsByCategories(
        categories: List<String>,
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>>

    fun getWordsByDifficulties(
        difficulties: List<String>,
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>>

    fun getFilteredWords(
        docenteId: String? = null,
        categoria: String? = null,
        dificultad: String? = null,
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>>
}