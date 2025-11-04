package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.CreateStudentResult
import com.example.alphakids.domain.repository.StudentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : StudentRepository {

    private companion object {
        const val TAG = "StudentRepo"
    }

    private val estudiantesCol = db.collection("estudiantes")

    private val sampleTutorStudents = listOf(
        Estudiante(
            id = "sample_student_tutor_1",
            nombre = "Lucía",
            apellido = "Rojas",
            edad = 6,
            grado = "Inicial 4 años",
            seccion = "A",
            idTutor = "sample_tutor",
            idDocente = "",
            idInstitucion = "Institución A",
            fotoPerfil = null,
            fechaRegistro = null,
            monedas = 0
        ),
        Estudiante(
            id = "sample_student_tutor_2",
            nombre = "Mateo",
            apellido = "Gómez",
            edad = 7,
            grado = "Inicial 5 años",
            seccion = "B",
            idTutor = "sample_tutor",
            idDocente = "",
            idInstitucion = "Institución B",
            fotoPerfil = null,
            fechaRegistro = null,
            monedas = 0
        )
    )

    override suspend fun createStudent(estudiante: Estudiante): CreateStudentResult {
        return try {
            val documentReference = estudiantesCol.add(estudiante).await()
            Log.d(TAG, "Estudiante creado con ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear estudiante", e)
            Result.failure(e)
        }
    }

    override fun getStudentsForTutor(tutorId: String): Flow<List<Estudiante>> {
        Log.d(TAG, "Fetching students for tutor ID: $tutorId")
        val query: Query = estudiantesCol.whereEqualTo("id_tutor", tutorId)
        return query.snapshots().map { querySnapshot ->
            Log.d(TAG, "Snapshot received. Documents found: ${querySnapshot.size()}")
            if (querySnapshot.metadata.hasPendingWrites()) {
                Log.d(TAG, "Snapshot has pending writes.")
            }
            val students = querySnapshot.toObjects(Estudiante::class.java)
            Log.d(TAG, "Mapped ${students.size} students")

            if (students.isEmpty()) {
                Log.w(TAG, "Tutor $tutorId sin estudiantes en Firestore. Entregando lista mock para pruebas.")
                sampleTutorStudents
            } else {
                students
            }
        }.catch { exception ->
            Log.e(TAG, "Error in student flow for tutor $tutorId", exception)
            emit(sampleTutorStudents)
        }
    }

    override suspend fun getStudentById(studentId: String): Estudiante? {
        return try {
            val snapshot = estudiantesCol.document(studentId).get().await()
            snapshot.toObject(Estudiante::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener estudiante con ID: $studentId", e)
            null
        }
    }

    override fun observeStudentById(studentId: String): Flow<Estudiante?> {
        // Mantiene sincronizado el perfil del estudiante con los cambios que se produzcan en Firestore.
        return estudiantesCol
            .document(studentId)
            .snapshots()
            .map { snapshot -> snapshot.toObject(Estudiante::class.java) }
            .catch { exception ->
                Log.e(TAG, "Error observando estudiante $studentId", exception)
                emit(null)
            }
    }

    override suspend fun updateStudent(estudiante: Estudiante): Result<Unit> {
        if (estudiante.id.isBlank()) {
            return Result.failure(IllegalArgumentException("El ID del estudiante no puede estar vacío."))
        }

        return try {
            estudiantesCol
                .document(estudiante.id)
                .set(estudiante, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estudiante ${estudiante.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun adjustStudentCoins(studentId: String, delta: Int): Result<Int> {
        return try {
            val newBalance: Int = db.runTransaction { transaction ->
                val docRef = estudiantesCol.document(studentId)
                val snapshot = transaction.get(docRef)

                val currentCoins = snapshot.getLong("monedas")?.toInt() ?: 0
                val updatedCoins = (currentCoins + delta).coerceAtLeast(0)

                transaction.update(docRef, mapOf("monedas" to updatedCoins))
                updatedCoins
            }.await() // <- importante: obtener Int, no Task<Int>

            Log.d(TAG, "Balance actualizado para estudiante $studentId: $newBalance")
            Result.success(newBalance)
        } catch (e: Exception) {
            Log.e(TAG, "Error al ajustar monedas para estudiante $studentId", e)
            Result.failure(e)
        }
    }
}
