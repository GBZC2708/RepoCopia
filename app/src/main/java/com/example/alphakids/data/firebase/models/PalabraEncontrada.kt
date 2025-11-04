package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class PalabraEncontrada(
    @DocumentId
    val id: String = "",
    @get:PropertyName("id_estudiante") @set:PropertyName("id_estudiante")
    val studentId: String = "",
    @get:PropertyName("id_palabra") @set:PropertyName("id_palabra")
    val wordId: String = "",
    @get:PropertyName("palabra") @set:PropertyName("palabra")
    val wordText: String = "",
    @get:PropertyName("fecha") @set:PropertyName("fecha") @ServerTimestamp
    val timestamp: Timestamp? = null
)
