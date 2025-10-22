package com.example.alphakids.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alphakids.ui.screens.teacher.words.AssignWordScreen
import com.example.alphakids.ui.screens.teacher.words.WordDetailScreen
import com.example.alphakids.ui.screens.teacher.words.WordsScreen
import com.example.alphakids.ui.screens.teacher.words.WordEditScreen
import com.example.alphakids.ui.screens.teacher.home.TeacherHomeScreen
import com.example.alphakids.ui.screens.teacher.students.TeacherStudentsScreen
import com.example.alphakids.ui.screens.teacher.students.StudentDetailScreen
import com.example.alphakids.ui.screens.tutor.profile_selection.ProfileSelectionScreen
import com.example.alphakids.ui.screens.tutor.home.StudentHomeScreen
import com.example.alphakids.ui.screens.tutor.dictionary.StudentDictionaryScreen
import com.example.alphakids.ui.screens.tutor.achievements.StudentAchievementsScreen
import com.example.alphakids.ui.screens.tutor.games.GameScreen
import com.example.alphakids.ui.screens.tutor.games.CameraScreen
import com.example.alphakids.ui.components.ActionDialog
import androidx.compose.material.icons.rounded.Warning
import com.example.alphakids.ui.screens.profile.EditProfileScreen
import com.example.alphakids.ui.screens.tutor.studentprofile.CreateStudentProfileScreen
import com.example.alphakids.ui.screens.tutor.studentprofile.EditStudentProfileScreen



@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.ROLE_SELECTION
) {
    // Función centralizada para CERRAR SESIÓN (navega a ROLE_SELECTION y limpia el stack)
    val onLogout: () -> Unit = {
        // Lógica: Llamar a AuthViewModel.signOut()
        navController.navigate(Routes.ROLE_SELECTION) {
            popUpTo(0) { inclusive = true } // Limpia todas las pantallas del stack
        }
    }

    // Función auxiliar para manejar la navegación de la barra inferior del DOCENTE
    // Recibe rutas base: "home" | "students" | "words" y las mapea a las rutas reales
    val navigateToTeacherBottomNav: (String) -> Unit = { base ->
        val targetRoute = when (base) {
            "students" -> Routes.TEACHER_STUDENTS
            "words" -> Routes.WORDS
            else -> Routes.TEACHER_HOME
        }
        navController.navigate(targetRoute) {
            // Usamos una ruta fija para popUpTo dentro del flujo del docente
            popUpTo(Routes.TEACHER_HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Función auxiliar para manejar la navegación de la barra inferior del TUTOR
    // Recibe rutas concretas (con studentId) y hace popUpTo a la ruta base del flujo
    val navigateToStudentBottomNav: (String) -> Unit = { route ->
        navController.navigate(route) {
            // Importante: popUpTo a la ruta base declarada (no parametrizada)
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- 1. Selección de Rol (/ - ROLE_SELECTION) ---
        composable(Routes.ROLE_SELECTION) {
            com.example.alphakids.ui.screens.common.RoleSelectScreen(
                onTutorClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TUTOR)) },
                onTeacherClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TEACHER)) }
            )
        }

        // --- 2. Autenticación (Login / Register) ---
        composable(
            route = Routes.LOGIN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR
            com.example.alphakids.ui.screens.auth.LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onLoginClick = {
                    val nextRoute = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(nextRoute) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onForgotPasswordClick = { /* TODO: recuperar contraseña */ },
                onRegisterClick = { navController.navigate(Routes.registerRoute(role)) },
                isTutorLogin = isTutor
            )
        }

        composable(
            route = Routes.REGISTER,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR
            com.example.alphakids.ui.screens.auth.RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onRegisterClick = {
                    val nextRoute = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(nextRoute) { popUpTo(Routes.REGISTER) { inclusive = true } }
                },
                isTutorRegister = isTutor
            )
        }

        // --- 3. Rutas de TUTORÍA/JUEGO ---

        // Pantalla de Selección de Perfiles (/perfiles)
        composable(Routes.PROFILES) {
            ProfileSelectionScreen(
                onProfileClick = { profileId -> navController.navigate(Routes.homeRoute(profileId)) },
                onAddProfileClick = { navController.navigate(Routes.STUDENT_PROFILE_CREATE) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TUTOR)) },
                onLogoutClick = onLogout
            )
        }

        // Home del Estudiante (/home/{studentId})
        composable(
            route = Routes.HOME,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            val studentName = if (studentId == "sofia_id") "Sofía" else "Estudiante"

            StudentHomeScreen(
                studentName = studentName,
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onPlayClick = { navController.navigate(Routes.GAME) },
                onDictionaryClick = { navigateToStudentBottomNav(Routes.dictionaryRoute(studentId)) },
                onAchievementsClick = { navigateToStudentBottomNav(Routes.achievementsRoute(studentId)) },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "dictionary" -> Routes.dictionaryRoute(studentId)
                        "achievements" -> Routes.achievementsRoute(studentId)
                        else -> Routes.homeRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "home"
            )
        }

        // Mi Diccionario (/dictionary/{studentId})
        composable(
            route = Routes.DICTIONARY,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            StudentDictionaryScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onWordClick = { /* Ver detalle de palabra en el diccionario */ },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "achievements" -> Routes.achievementsRoute(studentId)
                        else -> Routes.dictionaryRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "dictionary"
            )
        }

        // Mis Logros (/achievements/{studentId})
        composable(
            route = Routes.ACHIEVEMENTS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            StudentAchievementsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editStudentProfileRoute(studentId)) },
                onBottomNavClick = { route ->
                    val targetRoute = when (route) {
                        "home" -> Routes.homeRoute(studentId)
                        "dictionary" -> Routes.dictionaryRoute(studentId)
                        else -> Routes.achievementsRoute(studentId)
                    }
                    navigateToStudentBottomNav(targetRoute)
                },
                currentRoute = "achievements"
            )
        }

        // Pantalla de Juego (/game)
        composable(Routes.GAME) {
            GameScreen(
                wordLength = 4, icon = Icons.Rounded.Checkroom, difficulty = "Fácil",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Routes.HOME, inclusive = true) },
                onTakePhotoClick = { navController.navigate(Routes.CAMERA) }
            )
        }

        // Pantalla de Cámara (/camera)
        composable(Routes.CAMERA) {
            CameraScreen(
                onBackClick = { navController.popBackStack() },
                onShutterClick = { /* Lógica para mostrar diálogo */ },
                onCloseNotificationClick = { /* Cerrar la notificación */ },
                onFlashClick = { /* Toggle Flash */ }, onFlipCameraClick = { /* Flip Camera */ }
            )
        }

        // --- 4. Rutas de DOCENTE (Bottom Navigation) ---

        // Home del Docente (Inicio)
        composable(Routes.TEACHER_HOME) { backStackEntry ->
            TeacherHomeScreen(
                teacherName = "Profesor/a",
                onAssignWordsClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "home"
            )
        }

        // Alumnos (students)
        composable(Routes.TEACHER_STUDENTS) { backStackEntry ->
            TeacherStudentsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onStudentClick = { studentId -> navController.navigate(Routes.teacherStudentDetailRoute(studentId)) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "students"
            )
        }

        // Detalle de Alumno (Docente)
        composable(
            route = Routes.TEACHER_STUDENT_DETAIL,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "error"

            StudentDetailScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onAssignWordClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onWordClick = { wordId -> navController.navigate(Routes.wordDetailRoute(wordId)) },
                onSettingsClick = { /* Abrir Settings */ },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = backStackEntry.destination.route ?: Routes.TEACHER_STUDENTS
            )
        }

        // Palabras (words)
        composable(Routes.WORDS) { backStackEntry ->
            WordsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onCreateWordClick = { navController.navigate(Routes.createWordRoute()) },
                onAssignWordClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onWordClick = { wordId -> navController.navigate(Routes.wordDetailRoute(wordId)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "words"
            )
        }

        // Pantalla de Creación/Edición de Palabra (WORD_EDIT)
        composable(
            route = Routes.WORD_EDIT,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId")
            val isEditing = wordId != null

            WordEditScreen(
                isEditing = isEditing,
                onCloseClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() },
                onPrimaryActionClick = { navController.popBackStack() }
            )
        }

        // Pantalla de Detalle de Palabra (WORD_DETAIL)
        composable(
            route = Routes.WORD_DETAIL,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId") ?: "error"
            var showDeleteDialog by remember { mutableStateOf(false) }

            WordDetailScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onEditWordClick = { navController.navigate(Routes.editWordRoute(wordId)) },
                onDeleteWordClick = { showDeleteDialog = true },
                onStudentClick = { /* Navegar al detalle del estudiante */ },
                onSettingsClick = { /* Abrir Settings */ },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = backStackEntry.destination.route ?: Routes.WORDS
            )

            if (showDeleteDialog) {
                ActionDialog(
                    icon = Icons.Rounded.Warning,
                    message = "¿Estás seguro de eliminar esta palabra?",
                    primaryButtonText = "Eliminar",
                    onPrimaryButtonClick = {
                        showDeleteDialog = false
                        navController.popBackStack(Routes.WORDS, inclusive = false)
                    },
                    secondaryButtonText = "Cancelar",
                    onSecondaryButtonClick = { showDeleteDialog = false },
                    onDismissRequest = { showDeleteDialog = false },
                    isError = true
                )
            }
        }

        // Pantalla de Asignar Palabra
        composable(Routes.ASSIGN_WORD) {
            AssignWordScreen(
                onBackClick = { navController.popBackStack() },
                onAssignWordClick = { studentId, wordId -> /* Lógica de asignación */ },
                onStudentClick = { /* Navegar al detalle del estudiante */ }
            )
        }

        // --- 5. Perfiles y Edición ---
        // Editar perfil (Docente o Tutor)
        composable(
            route = Routes.EDIT_PROFILE,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: Routes.ROLE_TUTOR
            val isTutor = role == Routes.ROLE_TUTOR
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                isTutorProfile = isTutor
            )
        }

        // Crear perfil de estudiante (Tutor)
        composable(Routes.STUDENT_PROFILE_CREATE) {
            CreateStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onCreateClick = {
                    // Tras crear, volvemos a la lista de perfiles
                    navController.popBackStack()
                }
            )
        }

        // Editar perfil de estudiante (Tutor)
        composable(
            route = Routes.STUDENT_PROFILE_EDIT,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            EditStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
}
