package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.example.alphakids.data.firebase.models.DiccionarioPersonalItem
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.data.mappers.PersonalDictionaryItemMapper
import com.example.alphakids.data.mappers.WordAssignmentMapper
import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AssignmentResult
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
    private val diccionariosCol = db.collection("diccionarios_personales")

    // Datos de apoyo para ambientes donde aún no se registran estudiantes reales.
    private val sampleStudents = listOf(
        Estudiante(
            id = "sample_student_1",
            nombre = "Sofía",
            apellido = "Arenas",
            edad = 7,
            grado = "Inicial 4 años",
            seccion = "A",
            idTutor = "sample_tutor",
            idDocente = "",
            idInstitucion = "Institución A",
            fotoPerfil = null,
            fechaRegistro = null
        ),
        Estudiante(
            id = "sample_student_2",
            nombre = "Diego",
            apellido = "Luna",
            edad = 8,
            grado = "Inicial 5 años",
            seccion = "B",
            idTutor = "sample_tutor",
            idDocente = "",
            idInstitucion = "Institución A",
            fotoPerfil = null,
            fechaRegistro = null
        ),
        Estudiante(
            id = "sample_student_3",
            nombre = "Valentina",
            apellido = "Campos",
            edad = 6,
            grado = "Inicial 3 años",
            seccion = "C",
            idTutor = "sample_tutor",
            idDocente = "",
            idInstitucion = "Institución B",
            fotoPerfil = null,
            fechaRegistro = null
        )
    )

    override suspend fun createAssignment(assignment: WordAssignment): AssignmentResult {
        return try {
            val asignacionMap = WordAssignmentMapper.fromDomain(assignment)
            val docRef = asignacionesCol.add(asignacionMap).await()
            Log.d("AssignmentRepo", "Asignación creada con ID: ${docRef.id}")

            // Asociamos al estudiante con el docente para que aparezca en sus listados.
            if (assignment.idEstudiante.isNotBlank() && assignment.idDocente.isNotBlank()) {
                try {
                    estudiantesCol
                        .document(assignment.idEstudiante)
                        .set(
                            mapOf("id_docente" to assignment.idDocente),
                            SetOptions.merge()
                        )
                        .await()
                } catch (updateError: Exception) {
                    Log.e(
                        "AssignmentRepo",
                        "Error al vincular estudiante ${assignment.idEstudiante} con docente ${assignment.idDocente}",
                        updateError
                    )
                }
            }

            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("AssignmentRepo", "Error al crear asignación", e)
            Result.failure(e)
        }
    }

    override suspend fun isWordAlreadyAssigned(studentId: String, wordId: String): Boolean {
        return try {
            asignacionesCol
                .whereEqualTo("id_estudiante", studentId)
                .whereEqualTo("id_palabra", wordId)
                .get()
                .await()
                .documents
                .isNotEmpty()
        } catch (e: Exception) {
            Log.e("AssignmentRepo", "Error checking duplicated assignment", e)
            false
        }
    }

    override fun getStudentsForDocente(docenteId: String): Flow<List<Estudiante>> {
        Log.d("AssignmentRepo", "Fetching students for docente: $docenteId")
        val query: Query = estudiantesCol.orderBy("grado", Query.Direction.ASCENDING)
        return query.snapshots()
            .map { querySnapshot ->
                val estudiantes = querySnapshot.toObjects(Estudiante::class.java)

                val resolvedStudents = if (estudiantes.isEmpty()) {
                    Log.w(
                        "AssignmentRepo",
                        "No se encontraron estudiantes en Firestore. Usando datos de ejemplo para pruebas."
                    )
                    sampleStudents
                } else {
                    estudiantes
                }

                // Ordenamos priorizando los asignados al docente y luego alfabéticamente.
                resolvedStudents.sortedWith(
                    compareByDescending<Estudiante> { it.idDocente == docenteId }
                        .thenBy { it.grado }
                        .thenBy { it.seccion }
                        .thenBy { it.nombre }
                )
            }
            .catch { exception ->
                Log.e("AssignmentRepo", "Error in student flow for docente $docenteId", exception)
                emit(sampleStudents)
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
            snapshot
                .toObjects(AsignacionPalabra::class.java)
                .map(WordAssignmentMapper::toDomain)
                .filter { assignment ->
                    // Las asignaciones completadas no deben volver al listado de juegos.
                    assignment.estado.uppercase() != "COMPLETADO" &&
                        (query.isNullOrBlank() || assignment.palabraTexto.contains(query, ignoreCase = true))
                }
        }
        .catch { exception ->
            Log.e("AssignmentRepo", "Error fetching assignments for student $studentId", exception)
            emit(emptyList())
        }

    override suspend fun completeAssignment(assignmentId: String): Result<Unit> {
        return try {
            val document = asignacionesCol.document(assignmentId)
            val snapshot = document.get().await()
            val dto = snapshot.toObject(AsignacionPalabra::class.java)
                ?: return Result.failure(IllegalStateException("Asignación no encontrada"))

            // Actualizamos el estado y dejamos constancia de la fecha de finalización.
            document.set(
                mapOf(
                    "estado" to "COMPLETADO",
                    "fecha_completado" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()

            // Persistimos la palabra en el diccionario personal para que el estudiante la consulte.
            if (dto.idEstudiante.isNotBlank() && dto.idPalabra.isNotBlank()) {
                val dictionaryEntry = mapOf(
                    "texto" to dto.palabraTexto,
                    "imagen" to dto.palabraImagen,
                    "audio" to dto.palabraAudio,
                    "fecha_agregado" to FieldValue.serverTimestamp(),
                    "ultimo_repaso" to FieldValue.serverTimestamp(),
                    "veces_jugado" to FieldValue.increment(1),
                    "veces_acertado" to FieldValue.increment(1)
                )

                diccionariosCol
                    .document(dto.idEstudiante)
                    .collection("palabras")
                    .document(dto.idPalabra)
                    .set(dictionaryEntry, SetOptions.merge())
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AssignmentRepo", "Error completing assignment", e)
            Result.failure(e)
        }
    }

    override fun observeStudentDictionary(studentId: String): Flow<List<PersonalDictionaryItem>> {
        return diccionariosCol
            .document(studentId)
            .collection("palabras")
            .orderBy("fecha_agregado", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(DiccionarioPersonalItem::class.java)
                    .map(PersonalDictionaryItemMapper::toDomain)
            }
            .catch { exception ->
                Log.e("AssignmentRepo", "Error fetching dictionary for $studentId", exception)
                emit(emptyList())
            }
    }
}
