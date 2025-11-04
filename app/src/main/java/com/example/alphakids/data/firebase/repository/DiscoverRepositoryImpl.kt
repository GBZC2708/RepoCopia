package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.PalabraEncontrada
import com.example.alphakids.data.mappers.DiscoveredWordMapper
import com.example.alphakids.domain.models.DiscoveredWord
import com.example.alphakids.domain.repository.DiscoverRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DiscoverRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : DiscoverRepository {

    private companion object {
        const val TAG = "DiscoverRepo"
        const val COLLECTION = "palabras_encontradas"
    }

    private val discoveredCol = db.collection(COLLECTION)

    override fun observeDiscoveredWords(studentId: String): Flow<List<DiscoveredWord>> {
        return discoveredCol
            .whereEqualTo("id_estudiante", studentId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(PalabraEncontrada::class.java)
                    .map(DiscoveredWordMapper::toDomain)
            }
            .catch { exception ->
                Log.e(TAG, "Error observando palabras encontradas", exception)
                emit(emptyList())
            }
    }

    override suspend fun hasDiscoveredWord(studentId: String, wordId: String): Boolean {
        return try {
            discoveredCol
                .whereEqualTo("id_estudiante", studentId)
                .whereEqualTo("id_palabra", wordId)
                .limit(1)
                .get()
                .await()
                .isEmpty
                .not()
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando palabra encontrada", e)
            false
        }
    }

    override suspend fun saveDiscoveredWord(discoveredWord: DiscoveredWord): Result<Unit> {
        return try {
            val dto = DiscoveredWordMapper.fromDomain(discoveredWord)
            discoveredCol.add(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando palabra encontrada", e)
            Result.failure(e)
        }
    }
}
