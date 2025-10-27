package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.domain.models.WordAssignment
import kotlinx.coroutines.flow.Flow

typealias AssignmentResult = Result<String>

interface AssignmentRepository {

    suspend fun createAssignment(assignment: WordAssignment): AssignmentResult

    /**
     * Devuelve `true` cuando el estudiante ya tiene la palabra asignada, sin importar el estado.
     * Se usa antes de crear nuevas asignaciones para evitar duplicados.  */
    suspend fun isWordAlreadyAssigned(studentId: String, wordId: String): Boolean

    fun getStudentsForDocente(docenteId: String): Flow<List<com.example.alphakids.data.firebase.models.Estudiante>>

    fun getStudentsAssignedToWord(wordId: String): Flow<List<com.example.alphakids.data.firebase.models.Estudiante>>

    fun getFilteredAssignmentsByStudent(
        studentId: String,
        difficulty: String? = null,
        query: String? = null
    ): Flow<List<WordAssignment>>

    /** Marca la asignación como completada y traslada la palabra al diccionario personal. */
    suspend fun completeAssignment(assignmentId: String): Result<Unit>

    /**
     * Devuelve el diccionario personal del estudiante para poblar la UI con los
     * elementos que ya completó.
     */
    fun observeStudentDictionary(studentId: String): Flow<List<PersonalDictionaryItem>>
}
