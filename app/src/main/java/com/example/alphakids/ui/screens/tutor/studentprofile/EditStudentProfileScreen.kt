package com.example.alphakids.ui.screens.tutor.studentprofile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledDropdownField
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily
import com.example.alphakids.ui.student.StudentUiState
import com.example.alphakids.ui.student.StudentViewModel

// Listas base reutilizadas por el formulario y la vista previa para evitar declaraciones duplicadas.
private val DefaultInstituciones = listOf("Institución A", "Institución B", "Otra")
private val DefaultGrados = listOf("Inicial 3 años", "Inicial 4 años", "Inicial 5 años", "1ro", "2do")
private val DefaultSecciones = listOf("A", "B", "C", "D")

@Composable
fun EditStudentProfileScreen(
    studentId: String,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Opciones base para los desplegables. Se memorizan para mantener la misma instancia.
    val institucionesDisponibles = remember { DefaultInstituciones }
    val gradosDisponibles = remember { DefaultGrados }
    val seccionesDisponibles = remember { DefaultSecciones }

    val selectedStudent by viewModel.selectedStudent.collectAsState()
    val editUiState by viewModel.editUiState.collectAsState()

    // Estados del formulario. Se usan rememberSaveable para conservar valores tras recomposiciones
    // y para soportar giros de pantalla durante la edición.
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var institucion by rememberSaveable { mutableStateOf("") }
    var grado by rememberSaveable { mutableStateOf("") }
    var seccion by rememberSaveable { mutableStateOf("") }

    // Bandera para indicar que los datos del estudiante ya se cargaron en los campos.
    var camposInicializados by remember { mutableStateOf(false) }

    val latestOnSaveSuccess by rememberUpdatedState(newValue = onSaveSuccess)

    LaunchedEffect(studentId) {
        // Se solicita al ViewModel el estudiante que se desea editar.
        viewModel.loadStudent(studentId)
    }

    LaunchedEffect(selectedStudent) {
        // Cuando llega la información del estudiante, rellenamos los campos del formulario
        // únicamente la primera vez para evitar pisar cambios que el usuario haga manualmente.
        if (!camposInicializados) {
            selectedStudent?.let { estudiante ->
                nombre = estudiante.nombre
                apellido = estudiante.apellido
                edad = estudiante.edad.takeIf { it > 0 }?.toString().orEmpty()
                institucion = estudiante.idInstitucion
                grado = estudiante.grado
                seccion = estudiante.seccion
                camposInicializados = true
            }
        }
    }

    LaunchedEffect(editUiState) {
        when (editUiState) {
            is StudentUiState.Success -> {
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                viewModel.resetEditState()
                latestOnSaveSuccess()
            }
            is StudentUiState.Error -> {
                Toast.makeText(
                    context,
                    (editUiState as StudentUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetEditState()
            }
            else -> Unit
        }
    }

    // Indicador combinado: se muestra loading mientras no se cargan los datos iniciales o la actualización está en curso.
    val isFormLoading = !camposInicializados || editUiState is StudentUiState.Loading

    EditStudentProfileContent(
        nombre = nombre,
        apellido = apellido,
        edad = edad,
        institucion = institucion,
        grado = grado,
        seccion = seccion,
        instituciones = institucionesDisponibles,
        grados = gradosDisponibles,
        secciones = seccionesDisponibles,
        isLoading = isFormLoading,
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        onNombreChange = { nombre = it },
        onApellidoChange = { apellido = it },
        onEdadChange = { value -> edad = value.filter { it.isDigit() } },
        onInstitucionChange = { institucion = it },
        onGradoChange = { grado = it },
        onSeccionChange = { seccion = it },
        onSaveClick = {
            val estudiante = selectedStudent

            if (!camposInicializados || estudiante == null) {
                Toast.makeText(context, "Cargando información del estudiante...", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }

            val edadInt = edad.toIntOrNull()

            if (nombre.isBlank()) {
                Toast.makeText(context, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }
            if (apellido.isBlank()) {
                Toast.makeText(context, "Ingresa el apellido", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }
            if (edadInt == null || edadInt <= 0) {
                Toast.makeText(context, "Ingresa una edad válida", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }
            if (institucion.isBlank()) {
                Toast.makeText(context, "Selecciona la institución", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }
            if (grado.isBlank()) {
                Toast.makeText(context, "Selecciona el grado", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }
            if (seccion.isBlank()) {
                Toast.makeText(context, "Selecciona la sección", Toast.LENGTH_SHORT).show()
                return@onSaveClick
            }

            viewModel.updateStudent(
                id = estudiante.id,
                nombre = nombre,
                apellido = apellido,
                edad = edadInt,
                grado = grado,
                seccion = seccion,
                idInstitucion = institucion,
                idTutor = estudiante.idTutor,
                idDocente = estudiante.idDocente,
                fotoPerfil = estudiante.fotoPerfil
            )
        }
    )
}

@Composable
private fun EditStudentProfileContent(
    nombre: String,
    apellido: String,
    edad: String,
    institucion: String,
    grado: String,
    seccion: String,
    instituciones: List<String>,
    grados: List<String>,
    secciones: List<String>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onNombreChange: (String) -> Unit,
    onApellidoChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onInstitucionChange: (String) -> Unit,
    onGradoChange: (String) -> Unit,
    onSeccionChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Editar perfil",
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
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                IconContainer(
                    icon = Icons.Rounded.Star,
                    contentDescription = "Icono de Perfil de Estudiante"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Editar perfil",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Edita el perfil de tu hijo",
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                LabeledTextField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = onNombreChange,
                    placeholderText = "Escribe el nombre"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Apellido",
                    value = apellido,
                    onValueChange = onApellidoChange,
                    placeholderText = "Escribe el apellido"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Edad",
                    value = edad,
                    onValueChange = onEdadChange,
                    placeholderText = "Edad del niño"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledDropdownField(
                    label = "Institución",
                    selectedOption = institucion,
                    options = instituciones,
                    placeholderText = "Selecciona institución",
                    onOptionSelected = onInstitucionChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledDropdownField(
                    label = "Grado",
                    selectedOption = grado,
                    options = grados,
                    placeholderText = "Selecciona grado",
                    onOptionSelected = onGradoChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledDropdownField(
                    label = "Sección",
                    selectedOption = seccion,
                    options = secciones,
                    placeholderText = "Selecciona sección",
                    onOptionSelected = onSeccionChange
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Guardar",
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditStudentProfileScreenPreview() {
    AlphakidsTheme {
        EditStudentProfileContent(
            nombre = "Sofía",
            apellido = "Arenas",
            edad = "7",
            institucion = DefaultInstituciones.first(),
            grado = DefaultGrados[1],
            seccion = DefaultSecciones.first(),
            instituciones = DefaultInstituciones,
            grados = DefaultGrados,
            secciones = DefaultSecciones,
            isLoading = false,
            onBackClick = {},
            onCloseClick = {},
            onNombreChange = {},
            onApellidoChange = {},
            onEdadChange = {},
            onInstitucionChange = {},
            onGradoChange = {},
            onSeccionChange = {},
            onSaveClick = {}
        )
    }
}
