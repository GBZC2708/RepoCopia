package com.example.alphakids.ui.screens.auth

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
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onRegisterClick: () -> Unit,
    isTutorRegister: Boolean = true
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Registro",
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
                icon = if (isTutorRegister) Icons.Rounded.Face else Icons.Rounded.School,
                contentDescription = "Icono de Registro"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Registro",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (isTutorRegister) "Crea tu cuenta de tutor" else "Crea tu cuenta de docente",
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
                placeholderText = "Escribe tu nombre"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Apellido",
                value = apellido,
                onValueChange = { apellido = it },
                placeholderText = "Escribe tu apellido"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholderText = "Escribe tu email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Contraseña",
                value = password,
                onValueChange = { password = it },
                placeholderText = "Escribe tu contraseña",
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                label = "Teléfono",
                value = telefono,
                onValueChange = { telefono = it },
                placeholderText = "Escribe tu teléfono"
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Crear cuenta",
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Preview(showBackground = true, name = "Registro Tutor")
@Composable
fun RegisterScreenTutorPreview() {
    AlphakidsTheme {
        RegisterScreen(
            onBackClick = {},
            onCloseClick = {},
            onRegisterClick = {},
            isTutorRegister = true
        )
    }
}

@Preview(showBackground = true, name = "Registro Docente")
@Composable
fun RegisterScreenTeacherPreview() {
    AlphakidsTheme {
        RegisterScreen(
            onBackClick = {},
            onCloseClick = {},
            onRegisterClick = {},
            isTutorRegister = false
        )
    }
}
