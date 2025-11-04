#!/usr/bin/env kotlin

@file:Suppress("TooGenericExceptionCaught")

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.File

/**
 * Simple local backfill utility to align new reward fields with legacy development data.
 *
 * Usage:
 *  1. Provide a Firebase service account json via the SERVICE_ACCOUNT environment variable
 *     or by placing it in ./serviceAccount.json.
 *  2. `./gradlew dependencies` to ensure the kotlin scripting runtime is available.
 *  3. Execute `./tools/backfill_coins.kt` (requires Kotlin scripting support).
 */

val serviceAccountPath = System.getenv("SERVICE_ACCOUNT") ?: "serviceAccount.json"
val serviceAccountFile = File(serviceAccountPath)
require(serviceAccountFile.exists()) { "Service account credentials not found at $serviceAccountPath" }

val options = FirebaseOptions.builder()
    .setCredentials(GoogleCredentials.fromStream(serviceAccountFile.inputStream()))
    .build()

FirebaseApp.initializeApp(options)
val firestore = FirestoreClient.getFirestore()

fun backfillUsers() {
    val users = firestore.collection("usuarios").get().get()
    for (doc in users.documents) {
        val monedas = (doc.getLong("monedas") ?: 0L).coerceAtLeast(0)
        if (!doc.contains("monedas") || monedas == 0L) {
            firestore.collection("usuarios").document(doc.id)
                .update(mapOf("monedas" to monedas))
        }
    }
}

fun backfillWords() {
    val words = firestore.collection("palabras").get().get()
    for (doc in words.documents) {
        val reward = (doc.getLong("recompensaMonedas") ?: 5L).coerceIn(1, 50)
        val dificultad = (doc.getString("dificultad") ?: doc.getString("nivelDificultad") ?: "Fácil")
        val categoria = doc.getString("categoria") ?: "Animales"
        firestore.collection("palabras").document(doc.id).set(
            mapOf(
                "recompensaMonedas" to reward,
                "nivelDificultad" to dificultad,
                "categoria" to categoria
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )
    }
}

fun backfillAssignments() {
    val assignments = firestore.collection("asignaciones").get().get()
    for (doc in assignments.documents) {
        val reward = (doc.getLong("recompensaMonedas") ?: 5L).coerceIn(1, 50)
        val estado = doc.getString("estado") ?: "pendiente"
        firestore.collection("asignaciones").document(doc.id).set(
            mapOf(
                "recompensaMonedas" to reward,
                "estado" to estado.lowercase()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )
    }
}

fun backfillStudents() {
    val students = firestore.collection("estudiantes").get().get()
    for (doc in students.documents) {
        val updates = mutableMapOf<String, Any?>()
        if (!doc.contains("institucion")) updates["institucion"] = doc.getString("id_institucion")
        if (!doc.contains("grado")) updates["grado"] = doc.getString("grado")
        if (!doc.contains("seccion")) updates["seccion"] = doc.getString("seccion")
        if (!doc.contains("docenteId")) updates["docenteId"] = doc.getString("id_docente")
        if (updates.isNotEmpty()) {
            firestore.collection("estudiantes").document(doc.id)
                .set(updates, com.google.firebase.firestore.SetOptions.merge())
        }
    }
}

fun main() {
    backfillUsers()
    backfillWords()
    backfillAssignments()
    backfillStudents()
    println("Backfill complete ✅")
}

main()
