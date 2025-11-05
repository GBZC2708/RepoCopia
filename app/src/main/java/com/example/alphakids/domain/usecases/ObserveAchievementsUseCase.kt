package com.example.alphakids.domain.usecases

import com.example.alphakids.domain.repository.AchievementRepository
import javax.inject.Inject

class ObserveAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository
) {
    operator fun invoke(studentId: String) = repository.observeAchievements(studentId)
}

