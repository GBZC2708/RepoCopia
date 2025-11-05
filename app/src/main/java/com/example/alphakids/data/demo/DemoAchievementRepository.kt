package com.example.alphakids.data.demo

import com.example.alphakids.domain.models.Achievement
import com.example.alphakids.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DemoAchievementRepository : AchievementRepository {
    override fun observeAchievements(studentId: String): Flow<List<Achievement>> {
        return DemoDataStore.achievements.map { achievements ->
            achievements.filter { it.studentId == studentId }
        }
    }
}

