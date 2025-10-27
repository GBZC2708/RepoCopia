package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.StudentRepository
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    operator fun invoke(tutorId: String) = repository.getStudentsForTutor(tutorId)
}
