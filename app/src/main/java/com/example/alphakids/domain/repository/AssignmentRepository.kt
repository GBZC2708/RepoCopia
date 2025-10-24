package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.WordAssignment
import kotlinx.coroutines.flow.Flow

typealias AssignmentResult = Result<String>

interface AssignmentRepository {

    suspend fun createAssignment(assignment: WordAssignment): AssignmentResult

    fun getStudentsForDocente(docenteId: String): Flow<List<com.example.alphakids.data.firebase.models.Estudiante>>

    fun getStudentsAssignedToWord(wordId: String): Flow<List<com.example.alphakids.data.firebase.models.Estudiante>>
}
