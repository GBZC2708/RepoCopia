package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.StudentRepository
import javax.inject.Inject

class AdjustStudentCoinsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(studentId: String, delta: Int) =
        repository.adjustStudentCoins(studentId, delta)
}
