package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.DiscoverRepository
import javax.inject.Inject

class ObserveDiscoveredWordsUseCase @Inject constructor(
    private val repository: DiscoverRepository
) {
    operator fun invoke(studentId: String) = repository.observeDiscoveredWords(studentId)
}
