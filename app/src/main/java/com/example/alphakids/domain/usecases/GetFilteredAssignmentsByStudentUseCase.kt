package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredAssignmentsByStudentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(
        studentId: String,
        difficulty: String? = null,
        query: String? = null
    ): Flow<List<WordAssignment>> {
        return repository.getFilteredAssignmentsByStudent(studentId, difficulty, query)
    }
}
