package com.example.alphakids.ui.screens.tutor.studentprofile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.ui.student.StudentUiState
import com.example.alphakids.ui.student.StudentViewModel
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledDropdownField
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateStudentProfileScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCreateSuccess: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edadString by remember { mutableStateOf("") }

    val instituciones = listOf("Institución A", "Institución B", "Otra")
    val grados = listOf("Inicial 3 años", "Inicial 4 años", "Inicial 5 años")
    val secciones = listOf("A", "B", "C")

    var selectedInstitucion by remember { mutableStateOf<String?>(null) }
    var selectedGrado by remember { mutableStateOf<String?>(null) }
    var selectedSeccion by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.createUiState.collectAsState()
    val isLoading = uiState is StudentUiState.Loading
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.createUiState.collectLatest { state ->
            when (state) {
                is StudentUiState.Success -> {
                    Toast.makeText(context, "Perfil creado", Toast.LENGTH_SHORT).show()
                    onCreateSuccess()
                    viewModel.resetCreateState()
                }
                is StudentUiState.Error -> {
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetCreateState()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Crear Perfil",
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                IconContainer(
                    icon = Icons.Rounded.ChildCare,
                    contentDescription = "Icono Niño"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Nuevo Perfil", fontFamily = dmSansFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text("Ingresa los datos de tu hijo", fontFamily = dmSansFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))

                LabeledTextField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholderText = "Nombre del niño"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Apellido",
                    value = apellido,
                    onValueChange = { apellido = it },
                    placeholderText = "Apellido del niño"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledTextField(
                    label = "Edad",
                    value = edadString,
                    onValueChange = { edadString = it.filter { char -> char.isDigit() } },
                    placeholderText = "Edad del niño (ej. 4)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LabeledDropdownField(
                    label = "Institución",
                    selectedOption = selectedInstitucion ?: "",
                    placeholderText = "Selecciona institución (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledDropdownField(
                    label = "Grado",
                    selectedOption = selectedGrado ?: "",
                    placeholderText = "Selecciona grado (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledDropdownField(
                    label = "Sección",
                    selectedOption = selectedSeccion ?: "",
                    placeholderText = "Selecciona sección (Opcional)",
                    onClick = { /* TODO: Mostrar menú dropdown real */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState is StudentUiState.Error) {

                }

                PrimaryButton(
                    text = "Crear Perfil",
                    onClick = {
                        val edadInt = edadString.toIntOrNull()
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (apellido.isBlank()) {
                            Toast.makeText(context, "Ingresa el apellido", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (edadInt == null || edadInt <= 0) {
                            Toast.makeText(context, "Ingresa una edad válida", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        viewModel.createStudent(
                            nombre = nombre,
                            apellido = apellido,
                            edad = edadInt,
                            grado = selectedGrado ?: "",
                            seccion = selectedSeccion ?: "",
                            idInstitucion = "" // TODO: Replace "" with actual institution ID
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateStudentProfileScreenPreview() {
    AlphakidsTheme {
        CreateStudentProfileScreen({}, {}, {})
    }
}
