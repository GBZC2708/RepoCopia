package com.example.alphakids.data.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class DiccionarioPersonalItem(
    @DocumentId
    val idPalabra: String = "",

    val texto: String = "",
    val imagen: String = "",
    val audio: String = "",

    @PropertyName("fecha_agregado") @ServerTimestamp
    val fechaAgregado: Timestamp? = null,
    @PropertyName("ultimo_repaso")
    val ultimoRepaso: Timestamp? = null,
    val veces_jugado: Long = 0,
    val veces_acertado: Long = 0
)
