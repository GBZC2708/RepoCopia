package com.example.alphakids.ui.word

sealed interface WordUiState {
    object Idle : WordUiState
    object Loading : WordUiState
    data class Success(val message: String, val newWordId: String? = null) : WordUiState
    data class Error(val message: String) : WordUiState
}
