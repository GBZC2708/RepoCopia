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
import com.example.alphakids.ui.student.StudentViewModel

@Composable
fun EditStudentProfileScreen(
    studentId: String,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Opciones (una sola vez, no duplicadas)
    val institucionesOpts = listOf("Mi Colegio", "Colegio Nacional", "Colegio Parroquial", "Otra")
    val gradosOpts = listOf("Inicial 3 años", "Inicial 4 años", "Inicial 5 años", "1ro", "2do", "3ro", "4to", "5to")
    val seccionesOpts = listOf("A", "B", "C", "D")

    // Estado proveniente del VM
    val selectedStudent by viewModel.selectedStudent.collectAsState()
    val editUiState by viewModel.editUiState.collectAsState()

    // Cargar el alumno si aplica
    LaunchedEffect(studentId) {
        // Si tu VM tiene un loader, úsalo (si no, quita este bloque):
        // viewModel.loadStudentById(studentId)
    }

    // Estados locales (se inicializan con el alumno seleccionado)
    var nombre by remember(selectedStudent) { mutableStateOf(selectedStudent?.nombre ?: "") }
    var apellido by remember(selectedStudent) { mutableStateOf(selectedStudent?.apellido ?: "") }
    var edad by remember(selectedStudent) { mutableStateOf(selectedStudent?.edad?.toString() ?: "") }
    var institucion by remember(selectedStudent) { mutableStateOf(selectedStudent?.idInstitucion ?: "") }
    var grado by remember(selectedStudent) { mutableStateOf(selectedStudent?.grado ?: "") }
    var seccion by remember(selectedStudent) { mutableStateOf(selectedStudent?.seccion ?: "") }

    val latestOnSaveSuccess by rememberUpdatedState(newValue = onSaveSuccess)

    fun onSave() {
        val alumno = selectedStudent
        if (alumno == null) {
            Toast.makeText(context, "No se pudo cargar el estudiante.", Toast.LENGTH_SHORT).show()
            return
        }
        if (nombre.isBlank() || apellido.isBlank() || edad.isBlank() || institucion.isBlank() || grado.isBlank() || seccion.isBlank()) {
            Toast.makeText(context, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }
        val edadInt = edad.toIntOrNull()
        if (edadInt == null || edadInt <= 0) {
            Toast.makeText(context, "Edad inválida.", Toast.LENGTH_SHORT).show()
            return
        }

        // Llama al update del VM (ajusta nombres de parámetros a tu implementación real)
        viewModel.updateStudent(
            id = alumno.id,
            nombre = nombre,
            apellido = apellido,
            edad = edadInt,
            grado = grado,
            seccion = seccion,
            idInstitucion = institucion,
            idTutor = alumno.idTutor,
            idDocente = alumno.idDocente,
            fotoPerfil = alumno.fotoPerfil
        )

        Toast.makeText(context, "Perfil actualizado.", Toast.LENGTH_SHORT).show()
        latestOnSaveSuccess()
    }

    EditStudentProfileContent(
        nombre = nombre,
        apellido = apellido,
        edad = edad,
        institucion = institucion,
        grado = grado,
        seccion = seccion,
        instituciones = institucionesOpts,
        grados = gradosOpts,
        secciones = seccionesOpts,
        isLoading = editUiState.isLoading, // ajusta según tu UI state
        onBackClick = onBackClick,
        onCloseClick = onCloseClick,
        onNombreChange = { nombre = it },
        onApellidoChange = { apellido = it },
        onEdadChange = { edad = it },
        onInstitucionChange = { institucion = it },
        onGradoChange = { grado = it },
        onSeccionChange = { seccion = it },
        onSaveClick = { onSave() }
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
                    placeholderText = "Ej.: 7"
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
            institucion = "Mi Colegio",
            grado = "Inicial 4 años",
            seccion = "A",
            instituciones = listOf("Mi Colegio", "Colegio Nacional"),
            grados = listOf("Inicial 3 años", "Inicial 4 años"),
            secciones = listOf("A", "B"),
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
