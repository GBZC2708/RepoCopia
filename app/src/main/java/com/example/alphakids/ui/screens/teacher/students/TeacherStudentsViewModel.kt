package com.example.alphakids.ui.screens.teacher.students

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.data.mappers.StudentMapper
import com.example.alphakids.domain.models.Student
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetStudentsForDocenteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherStudentsViewModel @Inject constructor(
    private val getStudentsForDocenteUseCase: GetStudentsForDocenteUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    // Estado UI
    private val _uiState = MutableStateFlow(TeacherStudentsUiState())
    val uiState: StateFlow<TeacherStudentsUiState> = _uiState.asStateFlow()
    
    // Filtros
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow<String?>(null)

    // Estudiantes
    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Estudiante>> = getCurrentUserUseCase()
        .map { it?.uid }
        .flatMapLatest { docenteId ->
            if (docenteId != null) {
                Log.d("TeacherStudentsVM", "Cargando estudiantes para docente: $docenteId")
                getStudentsForDocenteUseCase(docenteId)
            } else {
                Log.d("TeacherStudentsVM", "No hay docente autenticado")
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estudiantes convertidos a dominio
    private val domainStudents = students.map { estudiantes ->
        estudiantes.map { StudentMapper.toDomain(it) }
    }

    // Estudiantes filtrados
    val filteredStudents = combine(
        domainStudents,
        _searchQuery,
        _selectedFilter
    ) { students, query, filter ->
        var result = students
        
        // Aplicar filtro de búsqueda
        if (query.isNotEmpty()) {
            result = result.filter { student ->
                val fullName = "${student.nombre} ${student.apellido}"
                fullName.contains(query, ignoreCase = true)
            }
        }
        
        // Aplicar filtro de categoría
        if (filter != null) {
            result = result.filter { it.grado == filter }
        }
        
        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Métricas para las tarjetas de información
    val metrics = domainStudents.map { students ->
        val totalStudents = students.size
        val activeStudents = students.size // Asumimos que todos están activos por ahora
        val gradeCounts = students.groupBy { it.grado }
        val sectionCounts = students.groupBy { it.seccion }
        
        TeacherStudentsMetrics(
            totalStudents = totalStudents,
            activeStudents = activeStudents,
            gradeDistribution = gradeCounts.mapValues { it.value.size },
            sectionDistribution = sectionCounts.mapValues { it.value.size }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TeacherStudentsMetrics()
    )

    // Filtros disponibles
    val availableFilters = domainStudents.map { students ->
        students.map { it.grado }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedFilter(filter: String?) {
        _selectedFilter.value = filter
    }

    fun selectStudent(studentId: String) {
        _uiState.update { it.copy(selectedStudentId = studentId) }
    }
}

data class TeacherStudentsUiState(
    val isLoading: Boolean = false,
    val selectedStudentId: String? = null,
    val error: String? = null
)

data class TeacherStudentsMetrics(
    val totalStudents: Int = 0,
    val activeStudents: Int = 0,
    val gradeDistribution: Map<String, Int> = emptyMap(),
    val sectionDistribution: Map<String, Int> = emptyMap()
)