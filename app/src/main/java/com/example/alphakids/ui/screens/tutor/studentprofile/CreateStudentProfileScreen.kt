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
fun CreateStudentProfileScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var seccion by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Crear perfil",
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
                text = "Crear perfil",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Crea un perfil para tu hijo",
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

            LabeledTextField(
                label = "Fecha de Nacimiento",
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                placeholderText = "DD/MM/AAAA"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Institución",
                selectedOption = institucion,
                placeholderText = "Select option",
                onClick = { /* TODO: Mostrar dropdown */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Grado",
                selectedOption = grado,
                placeholderText = "Select option",
                onClick = { /* TODO: Mostrar dropdown */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledDropdownField(
                label = "Sección",
                selectedOption = seccion,
                placeholderText = "Select option",
                onClick = { /* TODO: Mostrar dropdown */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Crear perfil",
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateStudentProfileScreenPreview() {
    AlphakidsTheme {
        CreateStudentProfileScreen(
            onBackClick = {},
            onCloseClick = {},
            onCreateClick = {}
        )
    }
}
