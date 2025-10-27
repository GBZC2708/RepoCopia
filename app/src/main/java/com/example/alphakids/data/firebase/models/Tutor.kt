package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class Tutor(
    @DocumentId
    val uid: String = "",

    @PropertyName("fechaRegistro")
    @ServerTimestamp
    val fechaRegistro: Timestamp? = null
)
