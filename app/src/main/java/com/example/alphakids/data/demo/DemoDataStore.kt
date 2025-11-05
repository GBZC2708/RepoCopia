package com.example.alphakids.data.demo

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.models.Achievement
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Fuente de datos en memoria que actúa como reemplazo de Firestore cuando el
 * modo demo está habilitado. Expone flows reactivos para simular la
 * sincronización en tiempo real de Firebase y ofrece operaciones comunes para
 * actualizar colecciones y sus relaciones.
 */
object DemoDataStore {

    data class DemoUser(
        val id: String,
        val role: UserRole,
        val displayName: String,
        val email: String,
        val password: String,
        val photoUrl: String? = null
    ) {
        fun toDomain(): User {
            val nameParts = displayName.split(" ", limit = 2)
            val firstName = nameParts.firstOrNull().orEmpty()
            val lastName = if (nameParts.size > 1) nameParts[1] else ""
            return User(
                uid = id,
                nombre = firstName,
                apellido = lastName,
                email = email,
                rol = role,
                estado = UserStatus.ACTIVE,
                fotoPerfil = photoUrl,
                creadoEn = System.currentTimeMillis()
            )
        }
    }

    data class DemoStudent(
        val id: String,
        val tutorId: String,
        val fullName: String,
        val grade: String,
        val section: String,
        val coins: Int,
        val avatarUrl: String? = null,
        val createdAt: Long = System.currentTimeMillis()
    ) {
        val firstName: String = fullName.substringBefore(" ")
        val lastName: String = fullName.substringAfter(" ", "")

        fun toFirestore(): Estudiante = Estudiante(
            id = id,
            nombre = firstName,
            apellido = lastName,
            edad = 0,
            grado = grade,
            seccion = section,
            idTutor = tutorId,
            idDocente = null,
            idInstitucion = "DEMO", // Valor ficticio
            fotoPerfil = avatarUrl,
            fechaRegistro = Timestamp(Date(createdAt)),
            monedas = coins
        )
    }

    data class DemoWord(
        val id: String,
        val text: String,
        val category: String,
        val difficulty: String,
        val rewardCoins: Int,
        val imageUrl: String,
        val createdBy: String,
        val createdAt: Long = System.currentTimeMillis(),
        val updatedAt: Long = createdAt,
        val audioUrl: String = "https://samplelib.com/lib/preview/mp3/sample-6s.mp3"
    ) {
        fun toDomain(): Word = Word(
            id = id,
            texto = text,
            categoria = category,
            nivelDificultad = difficulty,
            imagenUrl = imageUrl,
            audioUrl = audioUrl,
            recompensaMonedas = rewardCoins,
            fechaCreacionMillis = createdAt,
            creadoPor = createdBy
        )
    }

    data class DemoAssignment(
        val id: String,
        val studentId: String,
        val wordId: String,
        val status: String = "pending",
        val assignedAt: Long = System.currentTimeMillis(),
        val completedAt: Long? = null
    ) {
        fun toDomain(word: DemoWord, student: DemoStudent): WordAssignment = WordAssignment(
            id = id,
            idDocente = word.createdBy,
            idEstudiante = student.id,
            idPalabra = word.id,
            palabraTexto = word.text,
            palabraImagenUrl = word.imageUrl,
            palabraAudioUrl = word.audioUrl,
            palabraDificultad = word.difficulty,
            estudianteNombre = student.fullName,
            fechaAsignacionMillis = assignedAt,
            fechaLimiteMillis = null,
            estado = status,
            recompensaMonedas = word.rewardCoins
        )
    }

    data class DemoTeacherDictionary(
        val id: String,
        val wordIds: Set<String>,
        val updatedAt: Long = System.currentTimeMillis()
    )

    private val now = System.currentTimeMillis()

    private val demoUsers = mutableMapOf(
        "demo_teacher" to DemoUser(
            id = "demo_teacher",
            role = UserRole.DOCENTE,
            displayName = "Clara Docente",
            email = "docente@demo.com",
            password = "123456",
            photoUrl = null
        ),
        "demo_tutor" to DemoUser(
            id = "demo_tutor",
            role = UserRole.TUTOR,
            displayName = "Lucas Tutor",
            email = "tutor@demo.com",
            password = "123456",
            photoUrl = null
        )
    )

    private val demoStudents = listOf(
        DemoStudent(
            id = "demo_student_1",
            tutorId = "demo_tutor",
            fullName = "Sofía Herrera",
            grade = "Primaria 1",
            section = "A",
            coins = 0,
            avatarUrl = null,
            createdAt = now - 86_400_000L
        ),
        DemoStudent(
            id = "demo_student_2",
            tutorId = "demo_tutor",
            fullName = "Mateo Castillo",
            grade = "Primaria 1",
            section = "B",
            coins = 0,
            avatarUrl = null,
            createdAt = now - 43_200_000L
        ),
        DemoStudent(
            id = "demo_student_3",
            tutorId = "demo_tutor",
            fullName = "Valentina Ríos",
            grade = "Primaria 2",
            section = "A",
            coins = 0,
            avatarUrl = null,
            createdAt = now - 21_600_000L
        )
    )

    private val demoWords = listOf(
        DemoWord(
            id = "demo_word_sol",
            text = "Sol",
            category = "Naturaleza",
            difficulty = "Fácil",
            rewardCoins = 5,
            imageUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 72_000_000L
        ),
        DemoWord(
            id = "demo_word_luna",
            text = "Luna",
            category = "Naturaleza",
            difficulty = "Fácil",
            rewardCoins = 5,
            imageUrl = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 70_000_000L
        ),
        DemoWord(
            id = "demo_word_gato",
            text = "Gato",
            category = "Animales",
            difficulty = "Intermedio",
            rewardCoins = 8,
            imageUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 68_000_000L
        ),
        DemoWord(
            id = "demo_word_perro",
            text = "Perro",
            category = "Animales",
            difficulty = "Intermedio",
            rewardCoins = 8,
            imageUrl = "https://images.unsplash.com/photo-1507149833265-60c372daea22?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 66_000_000L
        ),
        DemoWord(
            id = "demo_word_libro",
            text = "Libro",
            category = "Objetos",
            difficulty = "Intermedio",
            rewardCoins = 10,
            imageUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 64_000_000L
        ),
        DemoWord(
            id = "demo_word_mesa",
            text = "Mesa",
            category = "Objetos",
            difficulty = "Fácil",
            rewardCoins = 4,
            imageUrl = "https://images.unsplash.com/photo-1449247526693-aa049327be54?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 62_000_000L
        ),
        DemoWord(
            id = "demo_word_flor",
            text = "Flor",
            category = "Naturaleza",
            difficulty = "Fácil",
            rewardCoins = 6,
            imageUrl = "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 60_000_000L
        ),
        DemoWord(
            id = "demo_word_arbol",
            text = "Árbol",
            category = "Naturaleza",
            difficulty = "Intermedio",
            rewardCoins = 7,
            imageUrl = "https://images.unsplash.com/photo-1521293281845-9e9dc09ffd39?w=400",
            createdBy = "demo_teacher",
            createdAt = now - 58_000_000L
        )
    )

    private val demoTeacherDictionary = DemoTeacherDictionary(
        id = "demo_dictionary",
        wordIds = setOf(
            "demo_word_sol",
            "demo_word_luna",
            "demo_word_gato",
            "demo_word_flor",
            "demo_word_libro"
        ),
        updatedAt = now
    )

    private val demoAssignments = listOf(
        DemoAssignment(
            id = "demo_assignment_1",
            studentId = "demo_student_1",
            wordId = "demo_word_sol",
            status = "pending",
            assignedAt = now - 54_000_000L
        ),
        DemoAssignment(
            id = "demo_assignment_2",
            studentId = "demo_student_1",
            wordId = "demo_word_gato",
            status = "pending",
            assignedAt = now - 52_000_000L
        ),
        DemoAssignment(
            id = "demo_assignment_3",
            studentId = "demo_student_2",
            wordId = "demo_word_luna",
            status = "pending",
            assignedAt = now - 51_000_000L
        ),
        DemoAssignment(
            id = "demo_assignment_4",
            studentId = "demo_student_2",
            wordId = "demo_word_libro",
            status = "pending",
            assignedAt = now - 50_000_000L
        ),
        DemoAssignment(
            id = "demo_assignment_5",
            studentId = "demo_student_3",
            wordId = "demo_word_flor",
            status = "pending",
            assignedAt = now - 49_000_000L
        ),
        DemoAssignment(
            id = "demo_assignment_6",
            studentId = "demo_student_3",
            wordId = "demo_word_mesa",
            status = "pending",
            assignedAt = now - 48_000_000L
        )
    )

    private val _currentUser = MutableStateFlow<DemoUser?>(null)
    val currentUser: StateFlow<DemoUser?> = _currentUser.asStateFlow()

    private val _students = MutableStateFlow(demoStudents.associateBy { it.id })
    val students: StateFlow<Map<String, DemoStudent>> = _students.asStateFlow()

    private val _words = MutableStateFlow(demoWords.associateBy { it.id })
    val words: StateFlow<Map<String, DemoWord>> = _words.asStateFlow()

    private val _assignments = MutableStateFlow(demoAssignments.associateBy { it.id })
    val assignments: StateFlow<Map<String, DemoAssignment>> = _assignments.asStateFlow()

    private val _teacherDictionary = MutableStateFlow(demoTeacherDictionary)
    val teacherDictionary: StateFlow<DemoTeacherDictionary> = _teacherDictionary.asStateFlow()

    private val _discoveries = MutableStateFlow<List<DiscoveredWord>>(emptyList())
    val discoveries: StateFlow<List<DiscoveredWord>> = _discoveries.asStateFlow()

    private val _dictionaryItems = MutableStateFlow<Map<String, List<PersonalDictionaryItem>>>(emptyMap())
    val dictionaryItems: StateFlow<Map<String, List<PersonalDictionaryItem>>> = _dictionaryItems.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    fun registerUser(displayName: String, email: String, password: String, role: UserRole): User {
        val id = "demo_user_${UUID.randomUUID()}"
        val user = DemoUser(
            id = id,
            role = role,
            displayName = displayName,
            email = email.lowercase(Locale.getDefault()),
            password = password
        )
        demoUsers[id] = user
        _currentUser.value = user
        return user.toDomain()
    }

    fun login(email: String, password: String): Result<User> {
        val normalizedEmail = email.lowercase(Locale.getDefault())
        val user = demoUsers.values.firstOrNull { it.email == normalizedEmail }
            ?: return Result.failure(IllegalArgumentException("Correo no registrado"))

        return if (user.password == password) {
            _currentUser.value = user
            Result.success(user.toDomain())
        } else {
            Result.failure(IllegalArgumentException("Contraseña incorrecta"))
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun getUserByEmail(email: String): DemoUser? =
        demoUsers.values.firstOrNull { it.email == email.lowercase(Locale.getDefault()) }

    fun getUserById(id: String): DemoUser? = demoUsers[id]

    fun upsertStudent(student: DemoStudent): DemoStudent {
        _students.update { current -> current + (student.id to student) }
        return student
    }

    fun createStudent(tutorId: String, fullName: String, grade: String, section: String): DemoStudent {
        val id = "demo_student_${UUID.randomUUID()}"
        val student = DemoStudent(
            id = id,
            tutorId = tutorId,
            fullName = fullName,
            grade = grade,
            section = section,
            coins = 0,
            avatarUrl = null,
            createdAt = System.currentTimeMillis()
        )
        upsertStudent(student)
        return student
    }

    fun updateStudentCoins(studentId: String, delta: Int): Result<Int> {
        val current = _students.value[studentId] ?: return Result.failure(IllegalArgumentException("Estudiante no encontrado"))
        val updated = current.copy(coins = (current.coins + delta).coerceAtLeast(0))
        _students.update { it + (studentId to updated) }
        return Result.success(updated.coins)
    }

    fun setStudentCoins(studentId: String, newValue: Int) {
        val current = _students.value[studentId] ?: return
        val updated = current.copy(coins = newValue)
        _students.update { it + (studentId to updated) }
    }

    fun saveWord(word: DemoWord) {
        _words.update { current -> current + (word.id to word) }
    }

    fun deleteWord(wordId: String) {
        _words.update { current -> current - wordId }
        _teacherDictionary.update { dict ->
            dict.copy(wordIds = dict.wordIds - wordId, updatedAt = System.currentTimeMillis())
        }
        _assignments.update { current -> current.filterNot { it.value.wordId == wordId } }
    }

    fun assignWord(studentId: String, wordId: String): Result<String> {
        val existing = _assignments.value.values.firstOrNull {
            it.studentId == studentId && it.wordId == wordId && it.status == "pending"
        }
        if (existing != null) {
            return Result.failure(IllegalStateException("La palabra ya está asignada"))
        }

        val id = "demo_assignment_${UUID.randomUUID()}"
        val assignment = DemoAssignment(
            id = id,
            studentId = studentId,
            wordId = wordId,
            status = "pending",
            assignedAt = System.currentTimeMillis()
        )
        _assignments.update { current -> current + (id to assignment) }
        return Result.success(id)
    }

    fun markAssignmentCompleted(assignmentId: String, rewardCoins: Int) {
        val assignment = _assignments.value[assignmentId] ?: return
        val completed = assignment.copy(
            status = "completed",
            completedAt = System.currentTimeMillis()
        )
        _assignments.update { current -> current + (assignmentId to completed) }

        val student = _students.value[assignment.studentId] ?: return
        val word = _words.value[assignment.wordId] ?: return

        updateStudentCoins(student.id, rewardCoins)

        val dictionaryEntry = PersonalDictionaryItem(
            idPalabra = word.id,
            texto = word.text,
            imagenUrl = word.imageUrl,
            audioUrl = word.audioUrl,
            fechaAgregadoMillis = System.currentTimeMillis(),
            ultimoRepasoMillis = null,
            vecesJugado = 1,
            vecesAcertado = 1
        )
        _dictionaryItems.update { current ->
            val existing = current[student.id] ?: emptyList()
            val updatedList = (existing + dictionaryEntry).sortedByDescending { it.fechaAgregadoMillis }
            current + (student.id to updatedList)
        }

        val achievement = Achievement(
            id = "achievement_${UUID.randomUUID()}",
            studentId = student.id,
            wordId = word.id,
            wordText = word.text,
            earnedCoins = rewardCoins,
            earnedAtMillis = System.currentTimeMillis()
        )
        _achievements.update { current -> (current + achievement).sortedByDescending { it.earnedAtMillis } }

        val discovered = DiscoveredWord(
            id = "discover_${UUID.randomUUID()}",
            studentId = student.id,
            wordId = word.id,
            wordText = word.text,
            discoveredAtMillis = System.currentTimeMillis()
        )
        _discoveries.update { current -> (current + discovered).sortedByDescending { it.discoveredAtMillis } }
    }

    fun addDiscovery(discoveredWord: DiscoveredWord): Result<Unit> {
        _discoveries.update { current ->
            if (current.any { it.studentId == discoveredWord.studentId && it.wordId == discoveredWord.wordId }) {
                current
            } else {
                (current + discoveredWord).sortedByDescending { it.discoveredAtMillis }
            }
        }
        return Result.success(Unit)
    }

    fun hasDiscovery(studentId: String, wordId: String): Boolean =
        _discoveries.value.any { it.studentId == studentId && it.wordId == wordId }

    fun setTeacherDictionary(wordIds: Set<String>) {
        _teacherDictionary.value = DemoTeacherDictionary(
            id = _teacherDictionary.value.id,
            wordIds = wordIds,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun addWordToDictionary(wordId: String) {
        _teacherDictionary.update { dict ->
            dict.copy(wordIds = dict.wordIds + wordId, updatedAt = System.currentTimeMillis())
        }
    }

    fun removeWordFromDictionary(wordId: String) {
        _teacherDictionary.update { dict ->
            dict.copy(wordIds = dict.wordIds - wordId, updatedAt = System.currentTimeMillis())
        }
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }
}

