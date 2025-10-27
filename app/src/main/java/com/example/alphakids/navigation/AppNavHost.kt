package com.example.alphakids.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.ui.auth.AuthViewModel
import com.example.alphakids.ui.word.WordUiState
import com.example.alphakids.ui.word.WordViewModel
import com.example.alphakids.ui.components.ActionDialog
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
import com.example.alphakids.ui.screens.tutor.games.CameraScreen
import com.example.alphakids.ui.screens.profile.EditProfileScreen
import com.example.alphakids.ui.screens.tutor.studentprofile.CreateStudentProfileScreen
import com.example.alphakids.ui.screens.tutor.studentprofile.EditStudentProfileScreen
import com.example.alphakids.ui.screens.tutor.games.AssignedWordsScreen
import com.example.alphakids.ui.screens.tutor.games.CameraOCRScreen
import com.example.alphakids.ui.screens.tutor.games.GameWordsScreen
import com.example.alphakids.ui.screens.tutor.games.MyGamesScreen
import com.example.alphakids.ui.screens.tutor.games.WordHistoryScreen
import com.example.alphakids.ui.screens.tutor.games.WordPuzzleScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = when {
        currentUser == null -> Routes.ROLE_SELECTION
        currentUser?.rol == UserRole.TUTOR -> Routes.PROFILES
        currentUser?.rol == UserRole.DOCENTE -> Routes.TEACHER_HOME
        else -> Routes.ROLE_SELECTION
    }

    val onLogout: () -> Unit = {
        authViewModel.logout()
    }

    val navigateToTeacherBottomNav: (String) -> Unit = { base ->
        val targetRoute = when (base) {
            "students" -> Routes.TEACHER_STUDENTS
            "words" -> Routes.WORDS
            else -> Routes.TEACHER_HOME
        }
        navController.navigateSingleTopTo(targetRoute, Routes.TEACHER_HOME)
    }

    val navigateToStudentBottomNav: (String) -> Unit = { route ->
        navController.navigateSingleTopTo(route, Routes.HOME)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Selección de rol
        composable(Routes.ROLE_SELECTION) {
            com.example.alphakids.ui.screens.common.RoleSelectScreen(
                onTutorClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TUTOR)) },
                onTeacherClick = { navController.navigate(Routes.loginRoute(Routes.ROLE_TEACHER)) }
            )
        }

        // Login
        composable(
            route = Routes.LOGIN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR
            
            com.example.alphakids.ui.auth.LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onLoginSuccess = {
                    val nextRoute = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { },
                onRegisterClick = { navController.navigate(Routes.registerRoute(role)) },
                isTutorLogin = isTutor
            )
        }

        // Registro
        composable(
            route = Routes.REGISTER,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: Routes.ROLE_TEACHER
            val isTutor = role == Routes.ROLE_TUTOR
            com.example.alphakids.ui.auth.RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    val nextRoute = if (isTutor) Routes.PROFILES else Routes.TEACHER_HOME
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                isTutorRegister = isTutor
            )
        }

        // Perfiles del tutor
        composable(Routes.PROFILES) {
            ProfileSelectionScreen(
                onProfileClick = { profileId ->
                    navController.navigate(Routes.homeRoute(profileId))
                },
                onAddProfileClick = { navController.navigate(Routes.STUDENT_PROFILE_CREATE) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TUTOR)) },
                onLogoutClick = onLogout
            )
        }

        // Pantalla principal del estudiante (ORIGEN)
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
                onPlayClick = {
                    android.util.Log.d(
                        "AppNavHost",
                        "Play button clicked, navigating to MyGames with studentId: $studentId"
                    )
                    navController.navigate(Routes.myGamesRoute(studentId))
                }, // <-- NAVEGA A LA SELECCIÓN DE JUEGOS
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


        // 2. Pantalla de Selección de Juego (MY_GAMES - PIVOTE)
        composable(
            route = Routes.MY_GAMES,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType }) // <-- RECIBE EL ID
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"

            MyGamesScreen(
                onBackClick = { navController.popBackStack() },
                onWordsGameClick = { navController.navigate(Routes.gameWordsRoute(studentId)) }, // PASA EL ID
                onHistoryClick = { navController.navigate(Routes.WORD_HISTORY) }
            )
        }

        // 3. Pantalla de Palabras Asignadas para Jugar (GAME_WORDS - DESTINO)
        composable(
            route = Routes.GAME_WORDS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType }) // <-- RECIBE EL ID
        ) {
            // El VM ahora lo obtendrá de SavedStateHandle
            GameWordsScreen(
                onBackClick = { navController.popBackStack() },
                onWordClick = { assignmentId ->
                    navController.navigate(Routes.wordPuzzleRoute(assignmentId))
                }
            )
        }
        // Pantalla de Palabras Asignadas
        composable(
            route = Routes.ASSIGNED_WORDS,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            AssignedWordsScreen(
                studentId = studentId,
                onBackClick = { navController.popBackStack() },
                onWordClick = { assignment ->
                    // Navegar al puzzle con los datos de la asignación
                    navController.navigate(Routes.wordPuzzleRoute(assignment.id ?: ""))
                }
            )
        }

        // Pantalla del Puzzle de Palabras
        composable(
            route = Routes.WORD_PUZZLE,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: ""

            WordPuzzleScreen(
                assignmentId = assignmentId,
                onBackClick = { navController.popBackStack() },
                onTakePhotoClick = { targetWord ->
                    val encodedWord = Uri.encode(targetWord)
                    navController.navigate(Routes.cameraOCRRoute(assignmentId, encodedWord))
                }
            )
        }

        // Pantalla de Cámara OCR
        composable(
            route = Routes.CAMERA_OCR,
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.StringType },
                navArgument("targetWord") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: ""
            val targetWord = backStackEntry.arguments
                ?.getString("targetWord")
                ?.let(Uri::decode)
                ?: ""

            CameraOCRScreen(
                assignmentId = assignmentId,
                targetWord = targetWord,
                onBackClick = { navController.popBackStack() },
                onWordCompleted = { navController.popBackStack() }
            )
        }

        // Historial de palabras completadas (flujo tutor)
        composable(Routes.WORD_HISTORY) {
            WordHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }



        // Diccionario
        composable(
            route = Routes.DICTIONARY,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: "default"
            StudentDictionaryScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onWordClick = { },
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

        // Logros
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

        // Cámara
        composable(Routes.CAMERA) {
            CameraScreen(
                onBackClick = { navController.popBackStack() },
                onShutterClick = { },
                onCloseNotificationClick = { },
                onFlashClick = { },
                onFlipCameraClick = { }
            )
        }

        // Docente: pantalla principal
        composable(Routes.TEACHER_HOME) {
            TeacherHomeScreen(
                onAssignWordsClick = { navController.navigate(Routes.ASSIGN_WORD) },
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "home"
            )
        }

        // Docente: estudiantes
        composable(Routes.TEACHER_STUDENTS) {
            TeacherStudentsScreen(
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onStudentClick = { studentId -> navController.navigate(Routes.teacherStudentDetailRoute(studentId)) },
                onSettingsClick = { navController.navigate(Routes.editProfileRoute(Routes.ROLE_TEACHER)) },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = "students"
            )
        }

        // Detalle de estudiante
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
                onSettingsClick = { },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = backStackEntry.destination.route ?: Routes.TEACHER_STUDENTS
            )
        }

        // Palabras
        composable(Routes.WORDS) { backStackEntry ->
            val viewModel: WordViewModel = hiltViewModel(backStackEntry)
            val words by viewModel.words.collectAsState()
            val currentFilter by viewModel.filterDifficulty.collectAsState()

            WordsScreen(
                words = words,
                currentDifficultyFilter = currentFilter,
                onSetDifficultyFilter = viewModel::setDifficultyFilter,
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

        // Editar palabra
        composable(
            route = Routes.WORD_EDIT,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId")
            val isEditing = wordId != null

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WORDS)
            }
            val viewModel: WordViewModel = hiltViewModel(parentEntry)

            val wordUiState by viewModel.uiState.collectAsState()
            val words by viewModel.words.collectAsState()

            val wordToEdit = remember(wordId, words) {
                words.find { it.id == wordId }
            }

            WordEditScreen(
                viewModel = viewModel,
                word = wordToEdit,
                isEditing = isEditing,
                onCloseClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() }
            )
        }

        // Detalle de palabra
        composable(
            route = Routes.WORD_DETAIL,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId") ?: "error"
            var showDeleteDialog by remember { mutableStateOf(false) }

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WORDS)
            }
            val viewModel: WordViewModel = hiltViewModel(parentEntry)

            val words by viewModel.words.collectAsState()
            val wordUiState by viewModel.uiState.collectAsState()

            val word = remember(wordId, words) {
                words.find { it.id == wordId }
            }

            WordDetailScreen(
                word = word,
                onLogoutClick = onLogout,
                onBackClick = { navController.popBackStack() },
                onEditWordClick = { navController.navigate(Routes.editWordRoute(wordId)) },
                onDeleteWordClick = { showDeleteDialog = true },
                onStudentClick = { },
                onSettingsClick = { },
                onBottomNavClick = navigateToTeacherBottomNav,
                currentRoute = backStackEntry.destination.route ?: Routes.WORDS
            )

            if (showDeleteDialog) {
                ActionDialog(
                    icon = Icons.Rounded.Warning,
                    message = "¿Estás seguro de eliminar esta palabra?",
                    primaryButtonText = "Eliminar",
                    onPrimaryButtonClick = {
                        viewModel.deleteWord(wordId)
                    },
                    secondaryButtonText = "Cancelar",
                    onSecondaryButtonClick = { showDeleteDialog = false },
                    onDismissRequest = { showDeleteDialog = false },
                    isError = true
                )
            }

            // Se corrigió el Smart Cast
            LaunchedEffect(wordUiState) {
                when (val state = wordUiState) {
                    is WordUiState.Success -> {
                        if (state.message.contains("eliminada")) {
                            showDeleteDialog = false
                            viewModel.resetUiState()
                            navController.popBackStack(Routes.WORDS, inclusive = false)
                        }
                    }
                    is WordUiState.Error -> {
                        showDeleteDialog = false
                        viewModel.resetUiState()
                    }
                    else -> {}
                }
            }
        }

        // Asignar palabra
        composable(Routes.ASSIGN_WORD) {
            AssignWordScreen(
                onBackClick = { navController.popBackStack() },
                onStudentClick = { }
            )
        }

        // Editar perfil
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

        // Crear perfil de estudiante
        composable(Routes.STUDENT_PROFILE_CREATE) {
            CreateStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onCreateSuccess = { navController.popBackStack() }
            )
        }

        // Editar perfil de estudiante
        composable(
            route = Routes.STUDENT_PROFILE_EDIT,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) {
            EditStudentProfileScreen(
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navigateSingleTopTo(
    route: String,
    popUpToRoute: String,
    inclusive: Boolean = false
) {
    navigate(route) {
        popUpTo(popUpToRoute) {
            saveState = true
            this.inclusive = inclusive
        }
        launchSingleTop = true
        restoreState = true
    }
}
