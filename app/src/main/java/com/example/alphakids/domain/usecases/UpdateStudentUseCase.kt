package com.example.alphakids.domain.usecases

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.repository.StudentRepository
import javax.inject.Inject

class GetStudentByIdUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(studentId: String): Estudiante? {
        // Encapsulamos el acceso directo al repositorio para mantener el dominio limpio.
        return repository.getStudentById(studentId)
    }
}

class UpdateStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(estudiante: Estudiante): Result<Unit> {
        // Delegamos en el repositorio la responsabilidad de persistir la edición.
        return repository.updateStudent(estudiante)
    }
}
