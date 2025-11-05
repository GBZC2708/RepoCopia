package com.example.alphakids.data.demo

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AssignmentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Normalizer
import java.util.Locale

class DemoAssignmentRepository : AssignmentRepository {

    override suspend fun createAssignment(assignment: WordAssignment): AssignmentResult {
        return DemoDataStore.assignWord(assignment.idEstudiante, assignment.idPalabra)
    }

    override suspend fun isWordAlreadyAssigned(studentId: String, wordId: String): Boolean {
        return DemoDataStore.assignments.value.values.any {
            it.studentId == studentId && it.wordId == wordId && it.status == "pending"
        }
    }

    override fun getStudentsForDocente(docenteId: String): Flow<List<Estudiante>> {
        // En modo demo exponemos la lista completa de estudiantes sin filtrar por docente.
        return DemoDataStore.students.map { studentsMap ->
            studentsMap.values
                .sortedBy { it.fullName }
                .map { it.toFirestore() }
        }
    }

    override fun getStudentsAssignedToWord(wordId: String): Flow<List<Estudiante>> {
        return DemoDataStore.assignments.map { assignmentsMap ->
            val studentIds = assignmentsMap.values
                .filter { it.wordId == wordId }
                .map { it.studentId }
                .toSet()
            DemoDataStore.students.value
                .filterKeys { it in studentIds }
                .values
                .map { it.toFirestore() }
        }
    }

    override fun getFilteredAssignmentsByStudent(
        studentId: String,
        difficulty: String?,
        query: String?
    ): Flow<List<WordAssignment>> {
        val normalizedDifficulty = difficulty?.normalize()
        val normalizedQuery = query?.normalize()
        return DemoDataStore.assignments.map { assignmentsMap ->
            assignmentsMap.values
                .filter { it.studentId == studentId }
                .filter { it.status == "pending" }
                .mapNotNull { assignment ->
                    val word = DemoDataStore.words.value[assignment.wordId] ?: return@mapNotNull null
                    val student = DemoDataStore.students.value[assignment.studentId] ?: return@mapNotNull null
                    val matchesDifficulty = normalizedDifficulty == null || word.difficulty.normalize() == normalizedDifficulty
                    val matchesQuery = normalizedQuery == null || word.text.normalize().contains(normalizedQuery)
                    if (matchesDifficulty && matchesQuery) assignment.toDomain(word, student) else null
                }
                .sortedBy { it.palabraTexto.lowercase(Locale.getDefault()) }
        }
    }

    override suspend fun completeAssignment(assignmentId: String): Result<Unit> {
        val assignment = DemoDataStore.assignments.value[assignmentId]
            ?: return Result.failure(IllegalArgumentException("Asignación no encontrada"))
        val word = DemoDataStore.words.value[assignment.wordId]
            ?: return Result.failure(IllegalArgumentException("Palabra no encontrada"))
        DemoDataStore.markAssignmentCompleted(assignmentId, word.rewardCoins)
        return Result.success(Unit)
    }

    override fun observeStudentDictionary(studentId: String): Flow<List<PersonalDictionaryItem>> {
        return DemoDataStore.dictionaryItems.map { dictionaryMap ->
            dictionaryMap[studentId] ?: emptyList()
        }
    }

    private fun String.normalize(): String {
        val lower = lowercase(Locale.getDefault()).trim()
        val decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD)
        return decomposed.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}

