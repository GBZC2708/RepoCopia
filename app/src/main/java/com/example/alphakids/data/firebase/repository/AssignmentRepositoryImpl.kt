package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.data.mappers.WordAssignmentMapper
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AssignmentResult
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : AssignmentRepository {

    private val asignacionesCol = db.collection("asignaciones")
    private val estudiantesCol = db.collection("estudiantes")

    override suspend fun createAssignment(assignment: WordAssignment): AssignmentResult {
        return try {
            val asignacionMap = WordAssignmentMapper.fromDomain(assignment)
            val docRef = asignacionesCol.add(asignacionMap).await()
            Log.d("AssignmentRepo", "Asignación creada con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("AssignmentRepo", "Error al crear asignación", e)
            Result.failure(e)
        }
    }

    override fun getStudentsForDocente(docenteId: String): Flow<List<Estudiante>> {
        Log.d("AssignmentRepo", "Fetching students for docente: $docenteId")
        val query: Query = estudiantesCol.whereEqualTo("id_docente", docenteId)
        return query.snapshots().map { querySnapshot ->
            querySnapshot.toObjects(Estudiante::class.java)
        }.catch { exception ->
            Log.e("AssignmentRepo", "Error in student flow for docente $docenteId", exception)
            emit(emptyList())
        }
    }

    override fun getStudentsAssignedToWord(wordId: String): Flow<List<Estudiante>> {
        val studentIdsFlow: Flow<List<String>> = asignacionesCol
            .whereEqualTo("id_palabra", wordId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.getString("id_estudiante") }
            }

        return studentIdsFlow.flatMapLatest { studentIds ->
            if (studentIds.isEmpty()) {
                flowOf(emptyList())
            } else {
                estudiantesCol.whereIn(FieldPath.documentId(), studentIds.take(10))
                    .snapshots()
                    .map { snapshot ->
                        snapshot.toObjects(Estudiante::class.java)
                    }
                    .catch { e ->
                        Log.e("AssignmentRepo", "Error fetching assigned students", e)
                        emit(emptyList())
                    }
            }
        }.catch { e ->
            Log.e("AssignmentRepo", "Error combining flows for assigned students", e)
            emit(emptyList())
        }
    }


    override fun getFilteredAssignmentsByStudent(
        studentId: String,
        difficulty: String?,
        query: String?
    ): Flow<List<WordAssignment>> = asignacionesCol
        .whereEqualTo("id_estudiante", studentId)
        .apply {

            if (difficulty != null && difficulty != "Todos") {
                whereEqualTo("palabra_dificultad", difficulty)
            }
        }
        .orderBy("fecha_asignacion", Query.Direction.DESCENDING)
        .snapshots()
        .map { snapshot ->

            snapshot.toObjects(AsignacionPalabra::class.java).mapNotNull { dto ->
                val domain = WordAssignmentMapper.toDomain(dto)

                if (query.isNullOrBlank() || domain.palabraTexto.contains(query, ignoreCase = true)) {
                    domain
                } else {
                    null
                }
            }
        }
        .catch { exception ->
            Log.e("AssignmentRepo", "Error fetching assignments for student $studentId", exception)
            emit(emptyList())
        }
}
