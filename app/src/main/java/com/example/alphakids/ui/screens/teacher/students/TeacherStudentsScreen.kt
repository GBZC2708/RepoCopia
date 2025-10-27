package com.example.alphakids.ui.screens.teacher.students

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.domain.models.Student
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.components.CustomFAB
import com.example.alphakids.ui.components.InfoCard
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.MainBottomBar
import com.example.alphakids.ui.components.SearchBar
import com.example.alphakids.ui.components.StudentListItem
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun TeacherStudentsScreen(
    viewModel: TeacherStudentsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onStudentClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBottomNavClick: (String) -> Unit,
    currentRoute: String = "students",
    teacherId: String = "teacher_id_placeholder" // En una app real, esto vendría de la sesión
) {
    val teacherBottomNavItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("students", "Alumnos", Icons.Rounded.Groups),
        BottomNavItem("words", "Palabras", Icons.Rounded.Spellcheck)
    )

    // Observar el estado UI
    val uiState by viewModel.uiState.collectAsState()
    val rawStudents by viewModel.students.collectAsState()
    val students by viewModel.filteredStudents.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val availableFilters by viewModel.availableFilters.collectAsState()

    // Estado local para la búsqueda y filtros
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    // Actualizar el ViewModel cuando cambie la búsqueda o filtro
    LaunchedEffect(searchQuery) {
        viewModel.updateSearchQuery(searchQuery)
    }

    LaunchedEffect(selectedFilter) {
        viewModel.updateSelectedFilter(selectedFilter)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Mis alumnos",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actionIcon = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                items = teacherBottomNavItems,
                currentRoute = currentRoute,
                onNavigate = onBottomNavClick
            )
        },
        floatingActionButton = {
            CustomFAB(
                icon = Icons.Rounded.Settings,
                contentDescription = "Configuración",
                onClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de métricas
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Alumnos",
                    data = metrics.totalStudents.toString(),
                    icon = Icons.Rounded.Groups
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Alumnos Activos",
                    data = metrics.activeStudents.toString(),
                    icon = Icons.Rounded.Face
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Mostrar el grado con más estudiantes
                val topGrade = metrics.gradeDistribution.entries
                    .maxByOrNull { it.value }
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Grado Principal",
                    data = if (topGrade != null) "${topGrade.key} (${topGrade.value})" else "N/A",
                    icon = Icons.Rounded.School
                )
                // Mostrar la sección con más estudiantes
                val topSection = metrics.sectionDistribution.entries
                    .maxByOrNull { it.value }
                InfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Sección Principal",
                    data = if (topSection != null) "${topSection.key} (${topSection.value})" else "N/A",
                    icon = Icons.Rounded.Groups
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Barra de búsqueda
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholderText = "Buscar por nombre"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chips de filtrado por grado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtrar:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Chip para mostrar todos
                InfoChip(
                    text = "Todos",
                    isSelected = selectedFilter == null,
                    onClick = { selectedFilter = null }
                )
                
                // Chips para cada grado disponible
                availableFilters.forEach { grade ->
                    InfoChip(
                        text = grade,
                        isSelected = selectedFilter == grade,
                        onClick = { selectedFilter = grade }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de estudiantes
            if (rawStudents.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty() && selectedFilter == null) 
                            "No hay estudiantes disponibles" 
                        else 
                            "No se encontraron estudiantes con los filtros aplicados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Primero intentamos mostrar los estudiantes filtrados (dominio)
                    if (students.isNotEmpty()) {
                        items(students, key = { it.id }) { student ->
                            StudentListItem(
                                fullname = "${student.nombre} ${student.apellido}",
                                age = "${student.edad} años",
                                numWords = "0 palabras", // Esto podría venir de otra fuente de datos
                                icon = Icons.Rounded.Face,
                                chipText = student.grado,
                                isSelected = (uiState.selectedStudentId == student.id),
                                onClickNavigation = { 
                                    viewModel.selectStudent(student.id)
                                    onStudentClick(student.id) 
                                }
                            )
                        }
                    } else {
                        // Si no hay estudiantes filtrados, mostramos todos los estudiantes (entidad)
                        items(rawStudents, key = { it.id }) { estudiante ->
                            StudentListItem(
                                fullname = "${estudiante.nombre} ${estudiante.apellido}",
                                age = "${estudiante.edad} años",
                                numWords = "0 palabras", // Esto podría venir de otra fuente de datos
                                icon = Icons.Rounded.Face,
                                chipText = estudiante.grado,
                                isSelected = (uiState.selectedStudentId == estudiante.id),
                                onClickNavigation = { 
                                    viewModel.selectStudent(estudiante.id)
                                    onStudentClick(estudiante.id) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeacherStudentsScreenPreview() {
    AlphakidsTheme {
        // Crear datos de ejemplo para la vista previa
        val previewStudents = listOf(
            Student(
                id = "1",
                nombre = "Sofia",
                apellido = "Arenas",
                edad = 8,
                grado = "3ro",
                seccion = "A",
                idTutor = "tutor1",
                idDocente = "docente1",
                idInstitucion = "inst1",
                fotoPerfilUrl = null,
                fechaRegistroMillis = null
            ),
            Student(
                id = "2",
                nombre = "Juan",
                apellido = "Pérez",
                edad = 7,
                grado = "2do",
                seccion = "B",
                idTutor = "tutor1",
                idDocente = "docente1",
                idInstitucion = "inst1",
                fotoPerfilUrl = null,
                fechaRegistroMillis = null
            ),
            Student(
                id = "3",
                nombre = "María",
                apellido = "González",
                edad = 9,
                grado = "4to",
                seccion = "A",
                idTutor = "tutor1",
                idDocente = "docente1",
                idInstitucion = "inst1",
                fotoPerfilUrl = null,
                fechaRegistroMillis = null
            ),
            Student(
                id = "4",
                nombre = "Carlos",
                apellido = "López",
                edad = 6,
                grado = "1ro",
                seccion = "C",
                idTutor = "tutor1",
                idDocente = "docente1",
                idInstitucion = "inst1",
                fotoPerfilUrl = null,
                fechaRegistroMillis = null
            )
        )
        
        // Usar un ViewModel simplificado para la vista previa
        val previewViewModel = FakeTeacherStudentsViewModel(previewStudents)
        
        // Crear una versión de TeacherStudentsScreen que use el ViewModel simplificado
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppHeader(
                    title = "Mis alumnos",
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actionIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                MainBottomBar(
                    items = listOf(
                        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
                        BottomNavItem("students", "Alumnos", Icons.Rounded.Groups),
                        BottomNavItem("words", "Palabras", Icons.Rounded.Spellcheck)
                    ),
                    currentRoute = "students",
                    onNavigate = {}
                )
            },
            floatingActionButton = {
                CustomFAB(
                    icon = Icons.Rounded.Settings,
                    contentDescription = "Configuración",
                    onClick = {}
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Tarjetas de métricas
                val metrics by previewViewModel.metrics.collectAsState()
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Alumnos",
                        data = metrics.totalStudents.toString(),
                        icon = Icons.Rounded.Groups
                    )
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Alumnos Activos",
                        data = metrics.activeStudents.toString(),
                        icon = Icons.Rounded.Face
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Mostrar el grado con más estudiantes
                    val topGrade = metrics.gradeDistribution.entries
                        .maxByOrNull { it.value }
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Grado Principal",
                        data = if (topGrade != null) "${topGrade.key} (${topGrade.value})" else "N/A",
                        icon = Icons.Rounded.School
                    )
                    // Mostrar la sección con más estudiantes
                    val topSection = metrics.sectionDistribution.entries
                        .maxByOrNull { it.value }
                    InfoCard(
                        modifier = Modifier.weight(1f),
                        title = "Sección Principal",
                        data = if (topSection != null) "${topSection.key} (${topSection.value})" else "N/A",
                        icon = Icons.Rounded.Groups
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Barra de búsqueda
                var searchQuery by remember { mutableStateOf("") }
                SearchBar(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        previewViewModel.updateSearchQuery(it)
                    },
                    placeholderText = "Buscar por nombre"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chips de filtrado por grado
                val availableFilters by previewViewModel.availableFilters.collectAsState()
                var selectedFilter by remember { mutableStateOf<String?>(null) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtrar:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Chip para mostrar todos
                    InfoChip(
                        text = "Todos",
                        isSelected = selectedFilter == null,
                        onClick = { 
                            selectedFilter = null
                            previewViewModel.updateSelectedFilter(null)
                        }
                    )
                    
                    // Chips para cada grado disponible
                    availableFilters.forEach { grade ->
                        InfoChip(
                            text = grade,
                            isSelected = selectedFilter == grade,
                            onClick = { 
                                selectedFilter = grade
                                previewViewModel.updateSelectedFilter(grade)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de estudiantes
                val students by previewViewModel.filteredStudents.collectAsState()
                val uiState by previewViewModel.uiState.collectAsState()
                
                if (students.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty() && selectedFilter == null) 
                                "No hay estudiantes disponibles" 
                            else 
                                "No se encontraron estudiantes con los filtros aplicados",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(students, key = { it.id }) { student ->
                            StudentListItem(
                                fullname = "${student.nombre} ${student.apellido}",
                                age = "${student.edad} años",
                                numWords = "0 palabras", // Esto podría venir de otra fuente de datos
                                icon = Icons.Rounded.Face,
                                chipText = student.grado,
                                isSelected = (uiState.selectedStudentId == student.id),
                                onClickNavigation = { 
                                    previewViewModel.selectStudent(student.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
