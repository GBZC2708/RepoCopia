package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.CreateStudentResult
import com.example.alphakids.domain.repository.StudentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : StudentRepository {

    private val estudiantesCol = db.collection("estudiantes")
    // Lista mock utilizada cuando el tutor no tiene estudiantes en Firestore.
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
            fechaRegistro = null
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
            fechaRegistro = null
        )
    )

    override suspend fun createStudent(estudiante: Estudiante): CreateStudentResult {
        return try {
            val documentReference = estudiantesCol.add(estudiante).await()
            Log.d("StudentRepo", "Estudiante creado con ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al crear estudiante", e)
            Result.failure(e)
        }
    }

    override fun getStudentsForTutor(tutorId: String): Flow<List<Estudiante>> {
        Log.d("StudentRepo", "Fetching students for tutor ID: $tutorId")
        val query: Query = estudiantesCol.whereEqualTo("id_tutor", tutorId)
        return query.snapshots().map { querySnapshot ->
            Log.d("StudentRepo", "Snapshot received. Documents found: ${querySnapshot.size()}")
            if (querySnapshot.metadata.hasPendingWrites()) {
                Log.d("StudentRepo", "Snapshot has pending writes.")
            }
            val students = querySnapshot.toObjects(Estudiante::class.java)
            Log.d("StudentRepo", "Mapped ${students.size} students")

            if (students.isEmpty()) {
                Log.w(
                    "StudentRepo",
                    "Tutor $tutorId sin estudiantes en Firestore. Entregando lista mock para pruebas."
                )
                sampleTutorStudents
            } else {
                students
            }
        }.catch { exception ->
            Log.e("StudentRepo", "Error in student flow for tutor $tutorId", exception)
            emit(sampleTutorStudents)
        }
    }

    override suspend fun getStudentById(studentId: String): Estudiante? {
        return try {
            // Se consulta el documento específico del estudiante para obtener la versión más reciente.
            val snapshot = estudiantesCol.document(studentId).get().await()
            snapshot.toObject(Estudiante::class.java)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al obtener estudiante con ID: $studentId", e)
            null
        }
    }

    override suspend fun updateStudent(estudiante: Estudiante): Result<Unit> {
        if (estudiante.id.isBlank()) {
            // Evitamos intentar guardar documentos sin identificador válido.
            return Result.failure(IllegalArgumentException("El ID del estudiante no puede estar vacío."))
        }

        return try {
            estudiantesCol
                .document(estudiante.id)
                .set(estudiante, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al actualizar estudiante ${estudiante.id}", e)
            Result.failure(e)
        }
    }

    override suspend fun getStudentById(studentId: String): Estudiante? {
        return try {
            // Se consulta el documento específico del estudiante para obtener la versión más reciente.
            val snapshot = estudiantesCol.document(studentId).get().await()
            snapshot.toObject(Estudiante::class.java)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al obtener estudiante con ID: $studentId", e)
            null
        }
    }

    override suspend fun updateStudent(estudiante: Estudiante): Result<Unit> {
        if (estudiante.id.isBlank()) {
            // Evitamos intentar guardar documentos sin identificador válido.
            return Result.failure(IllegalArgumentException("El ID del estudiante no puede estar vacío."))
        }

        return try {
            estudiantesCol
                .document(estudiante.id)
                .set(estudiante, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StudentRepo", "Error al actualizar estudiante ${estudiante.id}", e)
            Result.failure(e)
        }
    }
}
