package com.example.alphakids.data.demo

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.CreateStudentResult
import com.example.alphakids.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Reimplementación en memoria de [StudentRepository] que reutiliza el almacén
 * de datos de demo para exponer CRUD de estudiantes sin depender de Firestore.
 */
class DemoStudentRepository : StudentRepository {

    override suspend fun createStudent(estudiante: Estudiante): CreateStudentResult {
        if (estudiante.idTutor.isBlank()) {
            return Result.failure(IllegalArgumentException("El tutor es obligatorio"))
        }

        val fullName = listOf(estudiante.nombre, estudiante.apellido)
            .filter { it.isNotBlank() }
            .joinToString(separator = " ")
            .ifBlank { "Nuevo Estudiante" }

        val grade = estudiante.grado?.takeIf { it.isNotBlank() }
            ?: "Sin grado"

        val section = estudiante.seccion?.takeIf { it.isNotBlank() } ?: ""

        val created = DemoDataStore.createStudent(
            tutorId = estudiante.idTutor,
            fullName = fullName,
            grade = grade,
            section = section
        )

        val updated = created.copy(avatarUrl = estudiante.fotoPerfil, coins = estudiante.monedas)
        DemoDataStore.upsertStudent(updated)

        return Result.success(created.id)
    }

    override fun getStudentsForTutor(tutorId: String): Flow<List<Estudiante>> {
        return DemoDataStore.students.map { studentsMap ->
            studentsMap.values
                .filter { it.tutorId == tutorId }
                .sortedBy { it.fullName }
                .map { it.toFirestore() }
        }
    }

    override suspend fun getStudentById(studentId: String): Estudiante? {
        return DemoDataStore.students.value[studentId]?.toFirestore()
    }

    override fun observeStudentById(studentId: String): Flow<Estudiante?> {
        return DemoDataStore.students.map { studentsMap ->
            studentsMap[studentId]?.toFirestore()
        }
    }

    override suspend fun updateStudent(estudiante: Estudiante): Result<Unit> {
        if (estudiante.id.isBlank()) {
            return Result.failure(IllegalArgumentException("El ID del estudiante no puede estar vacío."))
        }

        val current = DemoDataStore.students.value[estudiante.id]
            ?: return Result.failure(IllegalArgumentException("Estudiante no encontrado"))

        val fullName = listOf(estudiante.nombre, estudiante.apellido)
            .filter { it.isNotBlank() }
            .joinToString(separator = " ")
            .ifBlank { current.fullName }

        val updated = current.copy(
            fullName = fullName,
            grade = estudiante.grado?.takeIf { it.isNotBlank() } ?: current.grade,
            section = estudiante.seccion?.takeIf { it.isNotBlank() } ?: current.section,
            avatarUrl = estudiante.fotoPerfil ?: current.avatarUrl,
            coins = estudiante.monedas.takeIf { it >= 0 } ?: current.coins
        )

        DemoDataStore.upsertStudent(updated)
        return Result.success(Unit)
    }

    override suspend fun adjustStudentCoins(studentId: String, delta: Int): Result<Int> {
        return DemoDataStore.updateStudentCoins(studentId, delta)
    }
}

