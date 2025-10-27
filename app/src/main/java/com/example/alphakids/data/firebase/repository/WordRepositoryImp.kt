package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.Palabra
import com.example.alphakids.data.mappers.WordMapper
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.WordResult
import com.example.alphakids.domain.repository.WordSortOrder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : WordRepository {

    private val palabrasCol = db.collection("palabras")

    override suspend fun createWord(word: Word): WordResult {
        return try {
            val palabraDto = WordMapper.fromDomain(word)
            val docRef = palabrasCol.add(palabraDto).await()
            Log.d("WordRepo", "Palabra creada con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("WordRepo", "Error al crear palabra", e)
            Result.failure(e)
        }
    }

    override suspend fun updateWord(word: Word): Result<Unit> {
        if (word.id.isEmpty()) {
            return Result.failure(IllegalArgumentException("Word ID is empty"))
        }
        return try {
            val palabraDto = WordMapper.fromDomain(word)
            palabrasCol.document(word.id).set(palabraDto, SetOptions.merge()).await()
            Log.d("WordRepo", "Palabra actualizada con ID: ${word.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("WordRepo", "Error al actualizar palabra", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteWord(wordId: String): Result<Unit> {
        if (wordId.isEmpty()) {
            return Result.failure(IllegalArgumentException("Word ID is empty"))
        }
        return try {
            palabrasCol.document(wordId).delete().await()
            Log.d("WordRepo", "Palabra eliminada con ID: $wordId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("WordRepo", "Error al eliminar palabra", e)
            Result.failure(e)
        }
    }

    override fun getWordsByDocente(docenteId: String, sortBy: WordSortOrder): Flow<List<Word>> {
        Log.d("WordRepo", "Fetching words for docente: $docenteId")
        val query = palabrasCol.whereEqualTo("creadoPor", docenteId)
            .applySorting(sortBy)

        return query.toWordListFlow("Error in words flow for docente $docenteId")
    }

    override fun getAllWords(sortBy: WordSortOrder): Flow<List<Word>> {
        Log.d("WordRepo", "Fetching all words")
        val query = palabrasCol.applySorting(sortBy)
        return query.toWordListFlow("Error in all words flow")
    }

    override suspend fun searchWordsByText(query: String, docenteId: String?): Flow<List<Word>> = flow {
        Log.d("WordRepo", "Searching words for query: $query")
        try {
            var firestoreQuery: Query = palabrasCol
            if (docenteId != null) {
                firestoreQuery = firestoreQuery.whereEqualTo("creadoPor", docenteId)
            }

            val snapshot = firestoreQuery
                .orderBy("texto")
                .startAt(query.lowercase())
                .endAt(query.lowercase() + '\uf8ff')
                .get()
                .await()

            val palabrasDto = snapshot.toObjects(Palabra::class.java)
            emit(palabrasDto.map { WordMapper.toDomain(it) })

        } catch (e: Exception) {
            Log.e("WordRepo", "Error searching words", e)
            emit(emptyList())
        }
    }

    override fun getWordsByCategories(categories: List<String>, sortBy: WordSortOrder): Flow<List<Word>> {
        if (categories.isEmpty() || categories.size > 10) {
            Log.w("WordRepo", "Categories list is empty or > 10. Returning empty flow.")
            return flow { emit(emptyList()) }
        }
        Log.d("WordRepo", "Fetching words for categories: $categories")
        val query = palabrasCol.whereIn("categoria", categories)
            .applySorting(sortBy)
        return query.toWordListFlow("Error in categories flow")
    }

    override fun getWordsByDifficulties(difficulties: List<String>, sortBy: WordSortOrder): Flow<List<Word>> {
        if (difficulties.isEmpty() || difficulties.size > 10) {
            Log.w("WordRepo", "Difficulties list is empty or > 10. Returning empty flow.")
            return flow { emit(emptyList()) }
        }
        Log.d("WordRepo", "Fetching words for difficulties: $difficulties")
        val query = palabrasCol.whereIn("nivelDificultad", difficulties)
            .applySorting(sortBy)
        return query.toWordListFlow("Error in difficulties flow")
    }

    override fun getFilteredWords(
        docenteId: String?,
        categoria: String?,
        dificultad: String?,
        sortBy: WordSortOrder
    ): Flow<List<Word>> {
        Log.d("WordRepo", "Fetching filtered words")
        var query: Query = palabrasCol

        if (docenteId != null) {
            query = query.whereEqualTo("creadoPor", docenteId)
        }
        if (categoria != null) {
            query = query.whereEqualTo("categoria", categoria)
        }
        if (dificultad != null) {
            query = query.whereEqualTo("nivelDificultad", dificultad)
        }

        query = query.applySorting(sortBy)

        return query.toWordListFlow("Error in filtered words flow")
    }

    private fun Query.applySorting(sortBy: WordSortOrder): Query {
        return when (sortBy) {
            WordSortOrder.TEXT_ASC -> this.orderBy("texto", Query.Direction.ASCENDING)
            WordSortOrder.TEXT_DESC -> this.orderBy("texto", Query.Direction.DESCENDING)
            WordSortOrder.DATE_CREATED_DESC -> this.orderBy("fechaCreacion", Query.Direction.DESCENDING)
            WordSortOrder.DATE_CREATED_ASC -> this.orderBy("fechaCreacion", Query.Direction.ASCENDING)
        }
    }

    private fun Query.toWordListFlow(errorMessage: String): Flow<List<Word>> {
        return this.snapshots()
            .map { querySnapshot ->
                Log.d("WordRepo", "Snapshot received. Documents: ${querySnapshot.size()}")

                val palabrasDto = querySnapshot.toObjects(Palabra::class.java)

                palabrasDto.forEachIndexed { index, palabra ->
                    Log.d("WordRepo", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d("WordRepo", "📝 Word #$index:")
                    Log.d("WordRepo", "  ID: ${palabra.id}")
                    Log.d("WordRepo", "  Texto: ${palabra.texto}")
                    Log.d("WordRepo", "  Imagen (DTO): '${palabra.imagen}'")
                    Log.d("WordRepo", "  Imagen Length: ${palabra.imagen.length}")
                    Log.d("WordRepo", "  Imagen isEmpty: ${palabra.imagen.isEmpty()}")
                    Log.d("WordRepo", "  Imagen isBlank: ${palabra.imagen.isBlank()}")
                    Log.d("WordRepo", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                }

                val words = palabrasDto.map { palabra ->
                    val word = WordMapper.toDomain(palabra)
                    Log.d("WordRepo", "🔄 Mapped to Domain:")
                    Log.d("WordRepo", "  ID: ${word.id}")
                    Log.d("WordRepo", "  ImagenUrl (Domain): '${word.imagenUrl}'")
                    word
                }

                words
            }
            .catch { exception ->
                Log.e("WordRepo", "❌ $errorMessage", exception)
                emit(emptyList())
            }
    }
}
