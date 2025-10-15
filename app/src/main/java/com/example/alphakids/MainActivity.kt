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
import com.example.alphakids.ui.screens.profile.ProfileSelectionScreen
import com.example.alphakids.ui.screens.role_selection.RoleSelectionScreen
import com.example.alphakids.ui.theme.AlphakidsTheme

object AppScreen {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
    const val PROFILE_SELECTION = "profile_selection"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlphakidsTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.ROLE_SELECTION) }
                var selectedRole by remember { mutableStateOf<String?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        AppScreen.ROLE_SELECTION -> {
                            RoleSelectionScreen { role ->
                                selectedRole = role
                                currentScreen = AppScreen.LOGIN
                            }
                        }

                        AppScreen.LOGIN -> {
                            val role = selectedRole ?: "usuario"
                            val icon = if (role == "Tutor") Icons.Rounded.Face else Icons.Rounded.School

                            LoginScreen(
                                role = role.lowercase(),
                                icon = icon,
                                onBackClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onCloseClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onLoginClick = { email, password ->
                                    Log.d("LoginAttempt", "Role: $role, Email: $email, Pass: $password")
                                    // *** LÓGICA DE NAVEGACIÓN ACTUALIZADA ***
                                    if (role == "Tutor") {
                                        currentScreen = AppScreen.PROFILE_SELECTION
                                    } else {
                                        // Para el docente, por ahora solo logueamos
                                        Log.d("LoginSuccess", "Docente ha iniciado sesión.")
                                    }
                                },
                                onForgotPasswordClick = { Log.d("Navigation", "Go to Forgot Password") },
                                onRegisterClick = { Log.d("Navigation", "Go to Register") }
                            )
                        }

                        AppScreen.PROFILE_SELECTION -> {
                            ProfileSelectionScreen(
                                onLogoutClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onProfileClick = { profileName -> Log.d("ProfileSelected", profileName) },
                                onAddProfileClick = { Log.d("Navigation", "Go to Add Profile") }
                            )
                        }
                    }
                }
            }
        }
    }
}