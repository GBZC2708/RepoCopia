package com.example.alphakids.navigation

object Routes {
    // Roles
    const val ROLE_TEACHER = "teacher"
    const val ROLE_TUTOR = "tutor"

    // --- 1. Entrada ---
    const val ROLE_SELECTION = "role_selection" // Ruta inicial: /

    // --- 2. Autenticación (Rutas Parametrizadas) ---
    const val LOGIN_BASE = "login"
    const val LOGIN = "$LOGIN_BASE/{role}" // /login/teacher o /login/tutor
    fun loginRoute(role: String) = "$LOGIN_BASE/$role"

    const val REGISTER_BASE = "register"
    const val REGISTER = "$REGISTER_BASE/{role}" // /register/teacher o /register/tutor
    fun registerRoute(role: String) = "$REGISTER_BASE/$role"

    // --- 3. Tutor (Post-Auth) ---
    const val PROFILES = "perfiles" // /perfiles (ProfileSelectionScreen)

    // --- 4. Home y Navegación del Estudiante ---
    const val HOME_BASE = "home"
    const val HOME = "$HOME_BASE/{studentId}"
    fun homeRoute(studentId: String) = "$HOME_BASE/$studentId"

    // Rutas de la barra inferior (BottomNav)
    const val DICTIONARY = "dictionary/{studentId}"
    fun dictionaryRoute(studentId: String) = "dictionary/$studentId"

    const val ACHIEVEMENTS = "achievements/{studentId}"
    fun achievementsRoute(studentId: String) = "achievements/$studentId"

    // Rutas de Juego
    const val GAME = "game"
    const val CAMERA = "camera"

    // --- 5. Docente (Bottom Navigation) ---
    const val TEACHER_HOME = "teacher_home"
    const val TEACHER_STUDENTS = "teacher_students"
    const val WORDS = "words"

    // --- 6. Docente (CRUD y Tareas) ---
    const val TEACHER_STUDENT_DETAIL = "teacher_student_detail/{studentId}"
    fun teacherStudentDetailRoute(studentId: String) = "teacher_student_detail/$studentId"

    const val WORD_EDIT_BASE = "word_edit"
    const val WORD_EDIT = "$WORD_EDIT_BASE?wordId={wordId}"
    fun createWordRoute() = WORD_EDIT_BASE
    fun editWordRoute(wordId: String) = "$WORD_EDIT_BASE?wordId=$wordId"

    const val WORD_DETAIL = "word_detail/{wordId}"
    fun wordDetailRoute(wordId: String) = "word_detail/$wordId"
    const val ASSIGN_WORD = "assign_word"

    // --- 7. Perfiles y Edición ---
    const val EDIT_PROFILE_BASE = "edit_profile"
    const val EDIT_PROFILE = "$EDIT_PROFILE_BASE/{role}" // role: teacher|tutor
    fun editProfileRoute(role: String) = "$EDIT_PROFILE_BASE/$role"

    const val STUDENT_PROFILE_CREATE = "student_profile_create"

    const val STUDENT_PROFILE_EDIT_BASE = "student_profile_edit"
    const val STUDENT_PROFILE_EDIT = "$STUDENT_PROFILE_EDIT_BASE/{studentId}"
    fun editStudentProfileRoute(studentId: String) = "$STUDENT_PROFILE_EDIT_BASE/$studentId"
}
