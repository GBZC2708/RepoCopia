package com.example.alphakids.domain.models

data class DiscoveredWord(
    val id: String,
    val studentId: String,
    val wordId: String,
    val wordText: String,
    val discoveredAtMillis: Long?
)
