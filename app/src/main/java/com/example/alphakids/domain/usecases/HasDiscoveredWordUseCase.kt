package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.DiscoverRepository
import javax.inject.Inject

class HasDiscoveredWordUseCase @Inject constructor(
    private val repository: DiscoverRepository
) {
    suspend operator fun invoke(studentId: String, wordId: String) =
        repository.hasDiscoveredWord(studentId, wordId)
}
