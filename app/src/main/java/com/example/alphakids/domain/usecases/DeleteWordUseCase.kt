package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.WordRepository
import javax.inject.Inject

class DeleteWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(wordId: String): Result<Unit> {
        return repository.deleteWord(wordId)
    }
}
