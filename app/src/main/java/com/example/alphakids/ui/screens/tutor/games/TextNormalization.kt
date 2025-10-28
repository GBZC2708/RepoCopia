package com.example.alphakids.ui.screens.tutor.games

import java.text.Normalizer
import java.util.Locale

/**
 * Normaliza cadenas para comparaciones libres de acentos, signos y diferencias de mayúsculas.
 *
 * Se declara en un archivo independiente para reutilizarla entre la UI y el ViewModel y
 * centralizar cualquier ajuste futuro en la estrategia de coincidencia del OCR.
 */
internal fun normalizeTextForComparison(value: String): String {
    // Eliminamos tildes y caracteres no alfanuméricos para hacer el match más tolerante.
    val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace("\\p{Mn}".toRegex(), "") // caracteres combinados (acentos)
        .uppercase(Locale("es", "ES"))
        .replace("[^A-Z0-9]".toRegex(), " ") // sustituimos símbolos por espacios
        .trim()

    // Compactamos espacios para simplificar la comparación final.
    return normalized.replace("\\s+".toRegex(), " ")
}
