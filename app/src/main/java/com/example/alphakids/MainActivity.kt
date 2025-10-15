package com.example.alphakids

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.alphakids.ui.components.AppBottomNavigationBar
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.screens.game.GameScreen
import com.example.alphakids.ui.screens.login.LoginScreen
import com.example.alphakids.ui.screens.main_menu.MainMenuScreen
import com.example.alphakids.ui.screens.profile.ProfileSelectionScreen
import com.example.alphakids.ui.screens.role_selection.RoleSelectionScreen
import com.example.alphakids.ui.theme.AlphakidsTheme

object AppScreen {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
    const val PROFILE_SELECTION = "profile_selection"
    const val MAIN_MENU = "main_menu"
    const val GAME_SCREEN = "game_screen" // Nueva pantalla
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlphakidsTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.ROLE_SELECTION) }
                var selectedRole by remember { mutableStateOf<String?>(null) }
                var selectedProfile by remember { mutableStateOf("Sofía") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        AppScreen.ROLE_SELECTION -> RoleSelectionScreen { role ->
                            selectedRole = role
                            currentScreen = AppScreen.LOGIN
                        }

                        AppScreen.LOGIN -> {
                            val role = selectedRole ?: "usuario"
                            val icon = if (role == "Tutor") Icons.Rounded.Face else Icons.Rounded.School
                            LoginScreen(
                                role = role.lowercase(),
                                icon = icon,
                                onBackClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onCloseClick = { currentScreen = AppScreen.ROLE_SELECTION },
                                onLoginClick = { _, _ ->
                                    if (role == "Tutor") {
                                        currentScreen = AppScreen.PROFILE_SELECTION
                                    } else { Log.d("LoginSuccess", "Docente ha iniciado sesión.") }
                                },
                                onForgotPasswordClick = { Log.d("Navigation", "Ir a Olvidé Contraseña") },
                                onRegisterClick = { Log.d("Navigation", "Ir a Registro") }
                            )
                        }

                        AppScreen.PROFILE_SELECTION -> ProfileSelectionScreen(
                            onLogoutClick = { currentScreen = AppScreen.ROLE_SELECTION },
                            onProfileClick = { profileName ->
                                selectedProfile = profileName
                                currentScreen = AppScreen.MAIN_MENU
                            },
                            onAddProfileClick = { Log.d("Navigation", "Go to Add Profile") }
                        )

                        AppScreen.MAIN_MENU -> {
                            var currentBottomRoute by remember { mutableStateOf(BottomNavItem.Inicio.route) }
                            Scaffold(
                                topBar = {
                                    TopAppBar(
                                        title = { Text("Menú Principal") },
                                        navigationIcon = {
                                            IconButton(onClick = { currentScreen = AppScreen.PROFILE_SELECTION }) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver a Perfil")
                                            }
                                        },
                                        actions = {
                                            IconButton(onClick = { currentScreen = AppScreen.ROLE_SELECTION }) {
                                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                                            }
                                        }
                                    )
                                },
                                bottomBar = {
                                    AppBottomNavigationBar(
                                        currentRoute = currentBottomRoute,
                                        onItemSelected = { route -> currentBottomRoute = route }
                                    )
                                }
                            ) { paddingValues ->
                                // La llamada a MainMenuScreen ahora está completa
                                MainMenuScreen(
                                    profileName = selectedProfile,
                                    onPlayClick = { currentScreen = AppScreen.GAME_SCREEN },
                                    onDictionaryClick = { Log.d("Navigation", "Go to Dictionary") },
                                    onAchievementsClick = { Log.d("Navigation", "Go to Achievements") },
                                    modifier = Modifier.padding(paddingValues)
                                )
                            }
                        }

                        AppScreen.GAME_SCREEN -> GameScreen(
                            onBackClick = { currentScreen = AppScreen.MAIN_MENU },
                            onCloseClick = { currentScreen = AppScreen.PROFILE_SELECTION }
                        )
                    }
                }
            }
        }
    }
}