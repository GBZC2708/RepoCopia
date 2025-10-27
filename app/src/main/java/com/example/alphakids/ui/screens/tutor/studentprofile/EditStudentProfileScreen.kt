package com.example.alphakids.ui.screens.tutor.studentprofile

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledDropdownField
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun EditStudentProfileScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("Estudiante") }
    var apellido by remember { mutableStateOf("Apellido") }
    var institucion by remember { mutableStateOf("Mi Colegio") }
    var grado by remember { mutableStateOf("1ro") }
    var seccion by remember { mutableStateOf("A") }

    val instituciones = listOf("Mi Colegio", "Colegio Nacional", "Colegio Parroquial")
    val grados = listOf("1ro", "2do", "3ro", "4to", "5to")
    val secciones = listOf("A", "B", "C", "D")

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
                onValueChange = { nombre = it },
                placeholderText = "Escribe el nombre"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Apellido",
                value = apellido,
                onValueChange = { apellido = it },
                placeholderText = "Escribe el apellido"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Institución",
                selectedOption = institucion,
                options = instituciones,
                placeholderText = "Selecciona institución",
                onOptionSelected = { institucion = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Grado",
                selectedOption = grado,
                options = grados,
                placeholderText = "Selecciona grado",
                onOptionSelected = { grado = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Sección",
                selectedOption = seccion,
                options = secciones,
                placeholderText = "Selecciona sección",
                onOptionSelected = { seccion = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Guardar",
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditStudentProfileScreenPreview() {
    AlphakidsTheme {
        EditStudentProfileScreen(
            onBackClick = {},
            onCloseClick = {},
            onSaveClick = {}
        )
    }
}
