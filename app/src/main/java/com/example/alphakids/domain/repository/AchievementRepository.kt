package com.example.alphakids.domain.repository

import com.example.alphakids.domain.models.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievements(studentId: String): Flow<List<Achievement>>
}

