package com.example.alphakids.ui.student

sealed interface StudentUiState {
    object Idle : StudentUiState
    object Loading : StudentUiState
    data class Success(val studentId: String) : StudentUiState
    data class Error(val message: String) : StudentUiState
}