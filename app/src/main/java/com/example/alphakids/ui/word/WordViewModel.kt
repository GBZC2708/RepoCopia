package com.example.alphakids.ui.word

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordSortOrder
import com.example.alphakids.domain.usecases.CreateWordUseCase
import com.example.alphakids.domain.usecases.DeleteWordUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetFilteredWordsUseCase
import com.example.alphakids.domain.usecases.UpdateWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val createWordUseCase: CreateWordUseCase,
    private val updateWordUseCase: UpdateWordUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val getFilteredWordsUseCase: GetFilteredWordsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordUiState>(WordUiState.Idle)
    val uiState: StateFlow<WordUiState> = _uiState.asStateFlow()

    private val _sortOrder = MutableStateFlow(WordSortOrder.TEXT_ASC)
    val sortOrder: StateFlow<WordSortOrder> = _sortOrder.asStateFlow()

    private val _filterDifficulty = MutableStateFlow<String?>(null)
    val filterDifficulty: StateFlow<String?> = _filterDifficulty.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val docenteIdFlow: Flow<String?> = getCurrentUserUseCase()
        .map { it?.uid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val words: StateFlow<List<Word>> = combine(
        docenteIdFlow,
        _sortOrder,
        _filterDifficulty
    ) { docenteId, sortOrder, difficulty ->
        Triple(docenteId, sortOrder, difficulty)
    }.flatMapLatest { (docenteId, sortOrder, difficulty) ->
        if (docenteId != null) {
            getFilteredWordsUseCase(
                docenteId = docenteId,
                dificultad = difficulty,
                sortBy = sortOrder
            ).catch { e ->
                Log.e("WordViewModel", "Error fetching filtered words", e)
                emit(emptyList())
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun createWord(
        texto: String,
        categoria: String,
        nivelDificultad: String,
        audioUrl: String
    ) {
        viewModelScope.launch {
            _uiState.value = WordUiState.Loading
            val currentUser = getCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _uiState.value = WordUiState.Error("No se pudo obtener el usuario.")
                return@launch
            }
            val newWord = Word(
                id = "",
                texto = texto,
                categoria = categoria,
                nivelDificultad = nivelDificultad,
                imagenUrl = "",
                audioUrl = audioUrl,
                fechaCreacionMillis = null,
                creadoPor = currentUser.uid
            )

            val result = createWordUseCase(
                word = newWord,
                imageUri = _selectedImageUri.value
            )

            _selectedImageUri.value = null

            _uiState.value = if (result.isSuccess) {
                WordUiState.Success("Palabra creada con éxito", result.getOrNull())
            } else {
                WordUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun updateWord(wordToUpdate: Word) {
        viewModelScope.launch {
            _uiState.value = WordUiState.Loading
            val result = updateWordUseCase(wordToUpdate)
            _uiState.value = if (result.isSuccess) {
                WordUiState.Success("Palabra actualizada con éxito")
            } else {
                WordUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun deleteWord(wordId: String) {
        viewModelScope.launch {
            _uiState.value = WordUiState.Loading
            if (wordId.isEmpty()) {
                _uiState.value = WordUiState.Error("ID de palabra inválido.")
                return@launch
            }
            val result = deleteWordUseCase(wordId)
            _uiState.value = if (result.isSuccess) {
                WordUiState.Success("Palabra eliminada con éxito")
            } else {
                WordUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun setDifficultyFilter(difficulty: String?) {
        _filterDifficulty.value = if (_filterDifficulty.value == difficulty) null else difficulty
    }

    fun setSortOrder(sortOrder: WordSortOrder) {
        _sortOrder.value = sortOrder
    }

    fun resetUiState() {
        _uiState.value = WordUiState.Idle
    }

    fun testImageUrl() {
        val testUrl = "https://firebasestorage.googleapis.com/v0/b/alphakids-tecsup.firebasestorage.app/o/palabras%2F561c7555-1d02-4321-9c83-dfff92a472f4%2Fimagen_1761314478472.jpg?alt=media&token=551edd4b-8036-4489-9d18-28263b64b692"

        Log.d("TEST_URL", "Testing URL: $testUrl")
        Log.d("TEST_URL", "URL Length: ${testUrl.length}")
        Log.d("TEST_URL", "Contains 'firebasestorage': ${testUrl.contains("firebasestorage")}")

        // Intenta descargar directamente
        viewModelScope.launch {
            try {
                val url = URL(testUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("TEST_URL", "Response Code: $responseCode")

                if (responseCode == 200) {
                    Log.d("TEST_URL", "✅ URL es accesible")
                } else {
                    Log.e("TEST_URL", "❌ URL retorna código: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("TEST_URL", "❌ Error al acceder a URL", e)
            }
        }
    }

    fun checkInternetPermission(context: Context): Boolean {
        val pm = context.packageManager
        val internetPermission = pm.checkPermission(
            android.Manifest.permission.INTERNET,
            context.packageName
        )
        val networkStatePermission = pm.checkPermission(
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            context.packageName
        )

        Log.d("PERMISSIONS", "INTERNET: ${internetPermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("PERMISSIONS", "NETWORK_STATE: ${networkStatePermission == PackageManager.PERMISSION_GRANTED}")

        return internetPermission == PackageManager.PERMISSION_GRANTED
    }
}
