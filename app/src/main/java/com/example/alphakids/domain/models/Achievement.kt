package com.example.alphakids.domain.models

data class Achievement(
    val id: String,
    val studentId: String,
    val wordId: String,
    val wordText: String,
    val earnedCoins: Int,
    val earnedAtMillis: Long
)

