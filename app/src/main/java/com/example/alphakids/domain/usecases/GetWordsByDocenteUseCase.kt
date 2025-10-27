package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.WordSortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordsByDocenteUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(
        docenteId: String,
        sortBy: WordSortOrder = WordSortOrder.TEXT_ASC
    ): Flow<List<Word>> {
        return repository.getWordsByDocente(docenteId, sortBy)
    }
}
