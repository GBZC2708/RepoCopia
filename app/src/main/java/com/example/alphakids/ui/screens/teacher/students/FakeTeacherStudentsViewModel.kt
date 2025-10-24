package com.example.alphakids.ui.screens.teacher.students

import androidx.lifecycle.ViewModel
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel simplificado para la vista previa de TeacherStudentsScreen
 */
class FakeTeacherStudentsViewModel(
    previewStudents: List<Student>
) : ViewModel() {

    // Convertir los estudiantes de dominio a entidades
    private val estudiantesList = previewStudents.map { student ->
        Estudiante(
            id = student.id,
            nombre = student.nombre,
            apellido = student.apellido,
            edad = student.edad,
            grado = student.grado,
            seccion = student.seccion,
            idTutor = student.idTutor,
            idDocente = student.idDocente,
            idInstitucion = student.idInstitucion,
            fotoPerfil = student.fotoPerfilUrl
        )
    }

    // Estado UI
    private val _uiState = MutableStateFlow(TeacherStudentsUiState())
    val uiState: StateFlow<TeacherStudentsUiState> = _uiState.asStateFlow()

    // Datos para la vista previa
    private val _students = MutableStateFlow(estudiantesList)
    val students: StateFlow<List<Estudiante>> = _students.asStateFlow()
    
    private val _filteredStudents = MutableStateFlow(previewStudents)
    val filteredStudents: StateFlow<List<Student>> = _filteredStudents.asStateFlow()

    private val _metrics = MutableStateFlow(
        TeacherStudentsMetrics(
            totalStudents = previewStudents.size,
            activeStudents = previewStudents.size,
            gradeDistribution = previewStudents.groupBy { it.grado }.mapValues { it.value.size },
            sectionDistribution = previewStudents.groupBy { it.seccion }.mapValues { it.value.size }
        )
    )
    val metrics: StateFlow<TeacherStudentsMetrics> = _metrics.asStateFlow()

    private val _availableFilters = MutableStateFlow(
        previewStudents.map { it.grado }.distinct().sorted()
    )
    val availableFilters: StateFlow<List<String>> = _availableFilters.asStateFlow()

    // Métodos para actualizar el estado
    fun updateSearchQuery(query: String) {
        // No hace nada en la vista previa
    }

    fun updateSelectedFilter(filter: String?) {
        // No hace nada en la vista previa
    }

    fun selectStudent(studentId: String) {
        _uiState.value = _uiState.value.copy(selectedStudentId = studentId)
    }
}