package com.example.alphakids

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.alphakids.ui.components.AppBottomNavigationBar
import com.example.alphakids.ui.components.BottomNavItem
import com.example.alphakids.ui.screens.game.GameScreen
import com.example.alphakids.ui.screens.login.LoginScreen
import com.example.alphakids.ui.screens.main_menu.MainMenuScreen
import com.example.alphakids.ui.screens.profile.ProfileSelectionScreen
import com.example.alphakids.ui.screens.role_selection.RoleSelectionScreen
import com.example.alphakids.ui.theme.AlphakidsTheme

//... (Object AppScreen no cambia)
object AppScreen {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login"
    const val PROFILE_SELECTION = "profile_selection"
    const val MAIN_MENU = "main_menu"
    const val GAME_SCREEN = "game_screen"
    const val CAMERA_SCREEN = "camera_screen" // Añadimos la pantalla de cámara
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
                val context = LocalContext.current

                // --- NUEVA LÓGICA PARA PERMISOS ---
                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (isGranted) {
                            // Si el usuario da el permiso, navegamos a la pantalla de la cámara
                            Log.d("Permissions", "Camera permission granted.")
                            currentScreen = AppScreen.CAMERA_SCREEN
                        } else {
                            // Si el usuario niega el permiso, nos quedamos en el menú
                            Log.d("Permissions", "Camera permission denied.")
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        // ... (Los casos ROLE_SELECTION, LOGIN, PROFILE_SELECTION no cambian)
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
                                MainMenuScreen(
                                    profileName = selectedProfile,
                                    onPlayClick = {
                                        // --- LÓGICA DE CLICK ACTUALIZADA ---
                                        // 1. Comprueba si ya tenemos el permiso
                                        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                            // Si ya lo tenemos, navega directamente
                                            currentScreen = AppScreen.CAMERA_SCREEN
                                        } else {
                                            // Si no, lanza la solicitud de permiso
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
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

                        // TODO: Añadir el caso para AppScreen.CAMERA_SCREEN
                        AppScreen.CAMERA_SCREEN -> {
                            // Por ahora, solo mostramos un texto. Aquí irá nuestra UI de cámara.
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Text("Pantalla de Cámara - ¡Permiso Concedido!")
                            }
                        }
                    }
                }
            }
        }
    }
}