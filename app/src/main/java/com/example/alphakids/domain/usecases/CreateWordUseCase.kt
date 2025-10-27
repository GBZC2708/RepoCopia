package com.example.alphakids.domain.usecases

import android.net.Uri
import com.example.alphakids.data.firebase.repository.ImageStorageRepositoryImpl
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.repository.ImageStorageRepository
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.WordResult
import javax.inject.Inject

class CreateWordUseCase @Inject constructor(
    private val repository: WordRepository,
    private val imageStorageRepository: ImageStorageRepository
) {
    suspend operator fun invoke(
        word: Word,
        imageUri: Uri?
    ): WordResult {
        var finalWord = word

        if (imageUri != null) {

            val imagePath = ImageStorageRepositoryImpl.createWordImagePath(word.id)
            val uploadResult = imageStorageRepository.uploadImage(imageUri, imagePath)

            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Error desconocido al subir imagen."))
            }

            finalWord = finalWord.copy(imagenUrl = uploadResult.getOrThrow())
        }

        val result = repository.createWord(finalWord)

        return result
    }
}
