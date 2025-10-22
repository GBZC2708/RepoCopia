package com.example.alphakids.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.AppHeader
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LabeledTextField
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.TextLinkButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    isTutorLogin: Boolean = true
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Iniciar sesión",
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
                icon = if (isTutorLogin) Icons.Rounded.Face else Icons.Rounded.School,
                contentDescription = "Icono de Login"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inicio de sesión",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = if (isTutorLogin) "Accede a tu cuenta de tutor" else "Accede a tu cuenta de docente",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            LabeledTextField(
                label = "Correo Electrónico",
                value = email,
                onValueChange = { email = it },
                placeholderText = "Escribe tu correo"
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

            Box(modifier = Modifier.fillMaxWidth()) {
                TextLinkButton(
                    text = "¿Olvidaste tu contraseña?",
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Iniciar sesión",
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No tienes cuenta",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Regístrate aquí",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onRegisterClick() }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Login Tutor")
@Composable
fun LoginScreenTutorPreview() {
    AlphakidsTheme {
        LoginScreen(
            onBackClick = {},
            onCloseClick = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            isTutorLogin = true
        )
    }
}

@Preview(showBackground = true, name = "Login Docente")
@Composable
fun LoginScreenTeacherPreview() {
    AlphakidsTheme {
        LoginScreen(
            onBackClick = {},
            onCloseClick = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            isTutorLogin = false
        )
    }
}
