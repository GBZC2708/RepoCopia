package com.example.alphakids.data.firebase.repository

import android.net.Uri
import android.util.Log
import com.example.alphakids.domain.repository.ImageStorageRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.UUID

class ImageStorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageStorageRepository {

    private val storageRef: StorageReference = storage.reference

    override suspend fun uploadImage(imageUri: Uri, path: String): Result<String> {
        return try {
            val fileRef = storageRef.child(path)

            val uploadTask = fileRef.putFile(imageUri).await()

            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("StorageRepo", "Error al subir imagen a $path", e)
            Result.failure(e)
        }
    }

    companion object {
        fun createWordImagePath(wordId: String): String {
            val uniqueId = if (wordId.isEmpty()) UUID.randomUUID().toString() else wordId
            return "palabras/$uniqueId/imagen_${System.currentTimeMillis()}.jpg"
        }
    }
}
