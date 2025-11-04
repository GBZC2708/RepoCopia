package com.example.alphakids.fakes

import android.net.Uri
import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AuthRepository
import com.example.alphakids.domain.repository.ImageStorageRepository
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.WordResult
import com.example.alphakids.domain.repository.WordSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeAuthRepository(initialUser: User? = null) : AuthRepository {
    private val currentUser = MutableStateFlow(initialUser)

    override fun getCurrentUser(): Flow<User?> = currentUser

    override fun register(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ) = flow {
        val user = User(
            uid = "user_${email.hashCode()}",
            nombre = nombre,
            apellido = apellido,
            email = email,
            telefono = telefono,
            fotoPerfil = null,
            rol = when (rol) {
                "docente" -> UserRole.DOCENTE
                "tutor" -> UserRole.TUTOR
                else -> UserRole.UNKNOWN
            },
            estado = UserStatus.ACTIVO,
            monedas = 0,
            creadoEn = 0L,
            actualizadoEn = 0L
        )
        currentUser.value = user
        emit(Result.success(user))
    }

    override fun login(email: String, clave: String) = flow {
        val user = currentUser.value ?: User(
            uid = "user_${email.hashCode()}",
            nombre = "Test",
            apellido = "User",
            email = email,
            rol = UserRole.TUTOR,
            estado = UserStatus.ACTIVO,
            monedas = 0
        )
        currentUser.value = user
        emit(Result.success(user))
    }

    override suspend fun logout() {
        currentUser.value = null
    }
}

class FakeWordRepository : WordRepository {
    private val wordsState = MutableStateFlow<List<Word>>(emptyList())

    fun setWords(words: List<Word>) {
        wordsState.value = words
    }

    override suspend fun createWord(word: Word): WordResult {
        val generatedId = if (word.id.isBlank()) {
            "word_${wordsState.value.size + 1}"
        } else {
            word.id
        }
        val savedWord = word.copy(id = generatedId)
        wordsState.value = wordsState.value + savedWord
        return Result.success(generatedId)
    }

    override suspend fun updateWord(word: Word): Result<Unit> {
        wordsState.value = wordsState.value.map { existing ->
            if (existing.id == word.id) word else existing
        }
        return Result.success(Unit)
    }

    override suspend fun deleteWord(wordId: String): Result<Unit> {
        wordsState.value = wordsState.value.filterNot { it.id == wordId }
        return Result.success(Unit)
    }

    override fun getWordsByDocente(
        docenteId: String,
        sortBy: WordSortOrder
    ): Flow<List<Word>> = getAllWords(sortBy)

    override fun getAllWords(sortBy: WordSortOrder): Flow<List<Word>> =
        wordsState.map { sort(it, sortBy) }

    override suspend fun searchWordsByText(query: String, docenteId: String?): Flow<List<Word>> =
        flowOf(wordsState.value.filter { it.texto.contains(query, ignoreCase = true) })

    override fun getWordsByCategories(
        categories: List<String>,
        sortBy: WordSortOrder
    ): Flow<List<Word>> = wordsState.map { list ->
        sort(list.filter { categories.contains(it.categoria) }, sortBy)
    }

    override fun getWordsByDifficulties(
        difficulties: List<String>,
        sortBy: WordSortOrder
    ): Flow<List<Word>> = wordsState.map { list ->
        sort(list.filter { difficulties.contains(it.nivelDificultad) }, sortBy)
    }

    override fun getFilteredWords(
        docenteId: String?,
        categoria: String?,
        dificultad: String?,
        sortBy: WordSortOrder
    ): Flow<List<Word>> = wordsState.map { list ->
        list.filter { word ->
            (categoria == null || word.categoria == categoria) &&
                (dificultad == null || word.nivelDificultad == dificultad)
        }.let { sort(it, sortBy) }
    }

    private fun sort(words: List<Word>, sortBy: WordSortOrder): List<Word> = when (sortBy) {
        WordSortOrder.TEXT_ASC -> words.sortedBy { it.texto }
        WordSortOrder.TEXT_DESC -> words.sortedByDescending { it.texto }
        WordSortOrder.DATE_CREATED_DESC -> words
        WordSortOrder.DATE_CREATED_ASC -> words
    }
}

class FakeImageStorageRepository : ImageStorageRepository {
    override suspend fun uploadImage(imageUri: Uri, path: String): Result<String> {
        return Result.success("https://example.com/$path")
    }
}

class FakeAssignmentRepository : AssignmentRepository {
    private val studentsState = MutableStateFlow<List<Estudiante>>(emptyList())
    private val assignments = mutableListOf<WordAssignment>()

    fun setStudents(students: List<Estudiante>) {
        studentsState.value = students
    }

    override suspend fun createAssignment(assignment: WordAssignment): Result<String> {
        val identifier = if (assignment.id.isBlank()) {
            "assignment_${assignments.size + 1}"
        } else {
            assignment.id
        }
        assignments += assignment.copy(id = identifier)
        return Result.success(identifier)
    }

    override suspend fun isWordAlreadyAssigned(studentId: String, wordId: String): Boolean {
        return assignments.any { it.idEstudiante == studentId && it.idPalabra == wordId }
    }

    override fun getStudentsForDocente(docenteId: String): Flow<List<Estudiante>> = studentsState

    override fun getStudentsAssignedToWord(wordId: String): Flow<List<Estudiante>> = flowOf(emptyList())

    override fun getFilteredAssignmentsByStudent(
        studentId: String,
        difficulty: String?,
        query: String?
    ): Flow<List<WordAssignment>> = flowOf(emptyList())

    override suspend fun completeAssignment(assignmentId: String): Result<Unit> = Result.success(Unit)

    override fun observeStudentDictionary(studentId: String): Flow<List<com.example.alphakids.domain.models.PersonalDictionaryItem>> =
        flowOf(emptyList())
}

