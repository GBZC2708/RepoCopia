package com.example.alphakids.ui.word

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.WordAssignment
import com.example.alphakids.domain.usecases.GetFilteredAssignmentsByStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GameWordsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFilteredAssignmentsByStudentUseCase: GetFilteredAssignmentsByStudentUseCase
) : ViewModel() {

    private val studentId: String = savedStateHandle.get<String>("studentId")
        ?: throw IllegalArgumentException("Student ID is required for GameWordsScreen")

    init {
        android.util.Log.d("GameWordsVM", "VM Inicializado. Buscando asignaciones para ID: $studentId")
    }

    private val _difficultyFilter = MutableStateFlow<String?>("Todos")
    val difficultyFilter: StateFlow<String?> = _difficultyFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val assignedWords: StateFlow<List<WordAssignment>> = combine(
        _difficultyFilter,
        _searchQuery
    ) { difficulty, query ->
        val actualDifficulty = if (difficulty == "Todos") null else difficulty
        actualDifficulty to query
    }.flatMapLatest { (difficulty, query) ->
        getFilteredAssignmentsByStudentUseCase(
            studentId = studentId,
            difficulty = difficulty,
            query = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDifficultyFilter(filter: String) {
        _difficultyFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
