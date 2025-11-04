package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.DiscoveredWord
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun observeDiscoveredWords(studentId: String): Flow<List<DiscoveredWord>>
    suspend fun hasDiscoveredWord(studentId: String, wordId: String): Boolean
    suspend fun saveDiscoveredWord(discoveredWord: DiscoveredWord): Result<Unit>
}
