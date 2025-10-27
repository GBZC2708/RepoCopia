package com.example.alphakids.navigation

import android.net.Uri

/**
 * Contenedor centralizado de rutas para la navegación de la aplicación.
 * Separa y documenta los destinos disponibles para cada flujo.
 */
object Routes {
    // Roles disponibles en la app.
    const val ROLE_TEACHER = "teacher"
    const val ROLE_TUTOR = "tutor"

    // --- 1. Entrada ---
    const val ROLE_SELECTION = "role_selection"

    // --- 2. Autenticación (Rutas Parametrizadas) ---
    const val LOGIN_BASE = "login"
    const val LOGIN = "$LOGIN_BASE/{role}"

    /**
     * Construye la ruta parametrizada de login con el rol indicado.
     */
    fun loginRoute(role: String) = "$LOGIN_BASE/$role"

    const val REGISTER_BASE = "register"
    const val REGISTER = "$REGISTER_BASE/{role}"

    /**
     * Construye la ruta parametrizada de registro con el rol indicado.
     */
    fun registerRoute(role: String) = "$REGISTER_BASE/$role"

    // --- 3. Tutor (Post-Auth) ---
    const val PROFILES = "perfiles"

    // --- 4. Home y Navegación del Estudiante ---
    const val HOME_BASE = "home"
    const val HOME = "$HOME_BASE/{studentId}"

    /**
     * Ruta concreta al home de un estudiante específico.
     */
    fun homeRoute(studentId: String) = "$HOME_BASE/$studentId"

    // Rutas de la barra inferior (BottomNav)
    const val DICTIONARY = "dictionary/{studentId}"

    /**
     * Ruta concreta al diccionario del estudiante indicado.
     */
    fun dictionaryRoute(studentId: String) = "dictionary/$studentId"

    const val ACHIEVEMENTS = "achievements/{studentId}"

    /**
     * Ruta concreta a los logros del estudiante indicado.
     */
    fun achievementsRoute(studentId: String) = "achievements/$studentId"

    // --- Rutas de Juego (actualizadas) ---
    const val MY_GAMES_BASE = "my_games"
    const val MY_GAMES = "$MY_GAMES_BASE/{studentId}"

    /**
     * Ruta concreta para la pantalla de selección de juegos.
     */
    fun myGamesRoute(studentId: String) = "$MY_GAMES_BASE/$studentId"

    const val GAME_WORDS_BASE = "game_words"
    const val GAME_WORDS = "$GAME_WORDS_BASE/{studentId}"

    /**
     * Ruta concreta para la pantalla de palabras asignadas.
     */
    fun gameWordsRoute(studentId: String) = "$GAME_WORDS_BASE/$studentId"

    const val ASSIGNED_WORDS = "assigned_words/{studentId}"

    /**
     * Ruta concreta al listado de palabras asignadas.
     */
    fun assignedWordsRoute(studentId: String) = "assigned_words/$studentId"

    const val WORD_PUZZLE_BASE = "word_puzzle"
    const val WORD_PUZZLE = "$WORD_PUZZLE_BASE/{assignmentId}"

    /**
     * Ruta concreta al puzzle asociado a una asignación específica.
     */
    fun wordPuzzleRoute(assignmentId: String) = "$WORD_PUZZLE_BASE/$assignmentId"

    const val CAMERA = "camera"

    // OCR Camera routes
    const val CAMERA_OCR_BASE = "camera_ocr"
    const val CAMERA_OCR = "$CAMERA_OCR_BASE/{assignmentId}/{targetWord}?imageUrl={imageUrl}&audioUrl={audioUrl}"

    /**
     * Construye la ruta para la cámara OCR codificando parámetros opcionales.
     */
    fun cameraOCRRoute(
        assignmentId: String,
        targetWord: String,
        imageUrl: String? = null,
        audioUrl: String? = null
    ): String {
        val encodedWord = Uri.encode(targetWord)
        val encodedImage = Uri.encode(imageUrl ?: "")
        val encodedAudio = Uri.encode(audioUrl ?: "")
        return "$CAMERA_OCR_BASE/$assignmentId/$encodedWord?imageUrl=$encodedImage&audioUrl=$encodedAudio"
    }

    // Word History
    const val WORD_HISTORY = "word_history"

    // --- 5. Docente (Bottom Navigation) ---
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_STUDENTS = "teacher_students"
    const val WORDS = "words"

    // Role constants
    const val ROLE_TEACHSHORTENED = "teacher_short"

    // --- 6. Docente (CRUD y Tareas) ---
    const val TEACHER_STUDENT_DETAIL = "teacher_student_detail/{studentId}"

    /**
     * Ruta concreta al detalle del estudiante en el flujo docente.
     */
    fun teacherStudentDetailRoute(studentId: String) = "teacher_student_detail/$studentId"

    const val WORD_EDIT_BASE = "word_edit"
    const val WORD_EDIT = "$WORD_EDIT_BASE?wordId={wordId}"

    /**
     * Ruta sin parámetro para crear una nueva palabra.
     */
    fun createWordRoute() = WORD_EDIT_BASE

    /**
     * Ruta parametrizada para editar una palabra existente.
     */
    fun editWordRoute(wordId: String) = "$WORD_EDIT_BASE?wordId=$wordId"

    const val WORD_DETAIL = "word_detail/{wordId}"

    /**
     * Ruta al detalle de una palabra según su identificador.
     */
    fun wordDetailRoute(wordId: String) = "word_detail/$wordId"

    const val ASSIGN_WORD = "assign_word"

    // --- 7. Perfiles y Edición ---
    const val EDIT_PROFILE_BASE = "edit_profile"
    const val EDIT_PROFILE = "$EDIT_PROFILE_BASE/{role}"

    /**
     * Ruta para editar el perfil según el rol recibido.
     */
    fun editProfileRoute(role: String) = "$EDIT_PROFILE_BASE/$role"

    const val STUDENT_PROFILE_CREATE = "student_profile_create"

    const val STUDENT_PROFILE_EDIT_BASE = "student_profile_edit"
    const val STUDENT_PROFILE_EDIT = "$STUDENT_PROFILE_EDIT_BASE/{studentId}"

    /**
     * Ruta para editar el perfil de un estudiante concreto.
     */
    fun editStudentProfileRoute(studentId: String) = "$STUDENT_PROFILE_EDIT_BASE/$studentId"
}
