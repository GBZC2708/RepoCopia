package com.example.alphakids.data.demo

import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DemoDiscoverRepository : DiscoverRepository {

    override fun observeDiscoveredWords(studentId: String): Flow<List<DiscoveredWord>> {
        return DemoDataStore.discoveries.map { discoveries ->
            discoveries.filter { it.studentId == studentId }
        }
    }

    override suspend fun hasDiscoveredWord(studentId: String, wordId: String): Boolean {
        return DemoDataStore.hasDiscovery(studentId, wordId)
    }

    override suspend fun saveDiscoveredWord(discoveredWord: DiscoveredWord): Result<Unit> {
        return DemoDataStore.addDiscovery(discoveredWord)
    }
}

