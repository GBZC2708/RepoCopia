package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AssignmentResult
import com.example.alphakids.data.firebase.models.Estudiante
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignment: WordAssignment): AssignmentResult {
        return repository.createAssignment(assignment)
    }
}

class IsWordAlreadyAssignedUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(studentId: String, wordId: String): Boolean {
        return repository.isWordAlreadyAssigned(studentId, wordId)
    }
}

class GetStudentsForDocenteUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(docenteId: String): Flow<List<Estudiante>> {
        return repository.getStudentsForDocente(docenteId)
    }
}

class GetStudentsAssignedToWordUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(wordId: String): Flow<List<Estudiante>> {
        return repository.getStudentsAssignedToWord(wordId)
    }
}

class CompleteAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignmentId: String): Result<Unit> {
        return repository.completeAssignment(assignmentId)
    }
}

class ObserveStudentDictionaryUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(studentId: String) = repository.observeStudentDictionary(studentId)
}
