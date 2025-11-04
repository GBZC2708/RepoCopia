package com.example.alphakids.domain.usecases

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Expone un flujo en tiempo real con los datos del estudiante solicitado para
 * que la UI pueda reaccionar ante cambios como la actualización de monedas.
 */
class ObserveStudentByIdUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    operator fun invoke(studentId: String): Flow<Estudiante?> {
        return repository.observeStudentById(studentId)
    }
}
