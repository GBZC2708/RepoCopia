package com.example.alphakids

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.alphakids.ui.screens.login.LoginScreen
import com.example.alphakids.ui.screens.role_selection.RoleSelectionScreen
import com.example.alphakids.ui.theme.AlphakidsTheme

object AppScreen {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlphakidsTheme {
                // Variables de estado para controlar la navegación
                var currentScreen by remember { mutableStateOf(AppScreen.ROLE_SELECTION) }
                var selectedRole by remember { mutableStateOf<String?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Usamos un 'when' para decidir qué pantalla mostrar
                    when (currentScreen) {
                        AppScreen.ROLE_SELECTION -> {
                            RoleSelectionScreen { role ->
                                // Cuando un rol es seleccionado, guardamos el rol
                                // y cambiamos el estado para mostrar la pantalla de login
                                selectedRole = role
                                currentScreen = AppScreen.LOGIN
                            }
                        }

                        AppScreen.LOGIN -> {
                            // Determinamos qué ícono y texto usar basado en el rol guardado
                            val role = selectedRole ?: "usuario" // Valor por defecto
                            val icon = if (role == "Tutor") Icons.Rounded.Face else Icons.Rounded.School

                            LoginScreen(
                                role = role.lowercase(),
                                icon = icon,
                                onBackClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onCloseClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onLoginClick = { email, password ->
                                    Log.d("LoginAttempt", "Role: $role, Email: $email, Pass: $password")
                                    // Aquí iría la lógica del ViewModel para iniciar sesión
                                },
                                onForgotPasswordClick = { Log.d("Navigation", "Go to Forgot Password") },
                                onRegisterClick = { Log.d("Navigation", "Go to Register") }
                            )
                        }
                    }
                }
            }
        }
    }
}