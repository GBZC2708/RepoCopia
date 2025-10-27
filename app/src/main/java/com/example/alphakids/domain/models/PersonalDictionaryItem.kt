package com.example.alphakids.domain.models

data class PersonalDictionaryItem(
    val idPalabra: String,
    val texto: String,
    val imagenUrl: String,
    val audioUrl: String,
    val fechaAgregadoMillis: Long?,
    val ultimoRepasoMillis: Long?,
    val vecesJugado: Long,
    val vecesAcertado: Long
)
