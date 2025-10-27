package com.example.alphakids.ui.screens.tutor.dictionary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.domain.models.PersonalDictionaryItem
import com.example.alphakids.domain.usecases.ObserveStudentDictionaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StudentDictionaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeStudentDictionaryUseCase: ObserveStudentDictionaryUseCase
) : ViewModel() {

    private val studentId: String = savedStateHandle.get<String>("studentId")
        ?: throw IllegalArgumentException("Student ID is required to load the dictionary")

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val dictionaryItems: StateFlow<List<PersonalDictionaryItem>> =
        observeStudentDictionaryUseCase(studentId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allItems: StateFlow<List<PersonalDictionaryItem>> = dictionaryItems

    val filteredItems: StateFlow<List<PersonalDictionaryItem>> = combine(
        dictionaryItems,
        _searchQuery
    ) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.texto.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
