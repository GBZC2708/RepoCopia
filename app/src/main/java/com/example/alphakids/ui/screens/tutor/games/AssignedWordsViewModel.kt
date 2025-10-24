package com.example.alphakids.ui.screens.tutor.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphakids.data.firebase.models.AsignacionPalabra
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AssignedWordsUiState(
    val isLoading: Boolean = false,
    val assignedWords: List<AsignacionPalabra> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AssignedWordsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssignedWordsUiState())
    val uiState: StateFlow<AssignedWordsUiState> = _uiState.asStateFlow()

    fun loadAssignedWords(studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Log detallado para debug
                android.util.Log.d("AssignedWords", "=== LOADING ASSIGNMENTS ===")
                android.util.Log.d("AssignedWords", "Student ID received: $studentId")
                
                // Verificar autenticación
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                android.util.Log.d("AssignedWords", "Current user: ${currentUser?.uid}")
                android.util.Log.d("AssignedWords", "User authenticated: ${currentUser != null}")
                
                if (currentUser == null) {
                    android.util.Log.e("AssignedWords", "User not authenticated!")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Usuario no autenticado. Por favor, inicia sesión."
                    )
                    return@launch
                }
                
                android.util.Log.d("AssignedWords", "=== FIRESTORE RULES ISSUE DETECTED ===")
                android.util.Log.d("AssignedWords", "Las reglas de Firestore están bloqueando el acceso.")
                android.util.Log.d("AssignedWords", "Necesitas aplicar reglas más permisivas temporalmente.")
                
                // Intentar buscar asignaciones directamente
                val assignments = firestore.collection("asignaciones")
                    .whereEqualTo("id_estudiante", studentId)
                    .whereEqualTo("estado", "PENDIENTE")
                    .get()
                    .await()
                    .toObjects(AsignacionPalabra::class.java)

                android.util.Log.d("AssignedWords", "Found ${assignments.size} assignments for student: $studentId")
                assignments.forEach { assignment ->
                    android.util.Log.d("AssignedWords", "Assignment: ${assignment.palabraTexto} - Estado: ${assignment.estado}")
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    assignedWords = assignments
                )
            } catch (e: Exception) {
                android.util.Log.e("AssignedWords", "Error loading assignments", e)
                
                // Mensaje de error más específico para reglas de Firestore
                val errorMessage = when {
                    e.message?.contains("PERMISSION_DENIED") == true -> {
                        android.util.Log.e("AssignedWords", "FIRESTORE RULES ERROR: Las reglas actuales no permiten acceso")
                        android.util.Log.e("AssignedWords", "SOLUCIÓN: Aplicar reglas temporales más permisivas en Firebase Console")
                        "🔒 Error de permisos de Firestore\n\n" +
                        "Las reglas de seguridad están bloqueando el acceso a las asignaciones.\n\n" +
                        "SOLUCIÓN REQUERIDA:\n" +
                        "1. Ve a Firebase Console\n" +
                        "2. Firestore Database → Rules\n" +
                        "3. Aplica reglas temporales más permisivas\n\n" +
                        "Contacta al desarrollador para las reglas correctas."
                    }
                    e.message?.contains("UNAUTHENTICATED") == true -> 
                        "Usuario no autenticado. Por favor, inicia sesión nuevamente."
                    else -> "Error al cargar asignaciones: ${e.message}"
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
}