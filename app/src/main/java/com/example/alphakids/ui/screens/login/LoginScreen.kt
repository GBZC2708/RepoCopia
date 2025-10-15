package com.example.alphakids.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.AlphaPrimaryButton
import com.example.alphakids.ui.components.AlphaTextField
import com.example.alphakids.ui.components.ClickableTextLink
import com.example.alphakids.ui.components.HeaderIcon
import com.example.alphakids.ui.theme.AlphakidsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    role: String, // Recibe "tutor" o "docente" para personalizar el texto
    icon: ImageVector, // Recibe el ícono correspondiente al rol
    onLoginClick: (String, String) -> Unit, // Callback para el botón de login (email, password)
    onBackClick: () -> Unit, // Callback para la flecha de atrás
    onCloseClick: () -> Unit, // Callback para el botón 'X'
    onForgotPasswordClick: () -> Unit, // Callback para el enlace de contraseña
    onRegisterClick: () -> Unit // Callback para el enlace de registro
) {
    // Estado interno para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar sesión") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
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
            Spacer(modifier = Modifier.height(32.dp))

            HeaderIcon(icon = icon)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inicio de sesión",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Accede a tu cuenta de $role",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            AlphaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo Electrónico"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlphaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ClickableTextLink(
                text = "¿Olvidaste tu contraseña?",
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AlphaPrimaryButton(
                text = "Iniciar sesión",
                onClick = { onLoginClick(email, password) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("No tienes cuenta", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(4.dp))
                ClickableTextLink(text = "Regístrate aquí", onClick = onRegisterClick)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AlphakidsTheme {
        LoginScreen(
            role = "tutor",
            icon = Icons.Rounded.Face,
            onLoginClick = { _, _ -> },
            onBackClick = {},
            onCloseClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {}
        )
    }
}