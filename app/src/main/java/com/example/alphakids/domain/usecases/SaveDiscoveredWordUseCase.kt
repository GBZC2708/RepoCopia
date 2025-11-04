package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.repository.DiscoverRepository
import javax.inject.Inject

class SaveDiscoveredWordUseCase @Inject constructor(
    private val repository: DiscoverRepository
) {
    suspend operator fun invoke(discoveredWord: DiscoveredWord) =
        repository.saveDiscoveredWord(discoveredWord)
}
