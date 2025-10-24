package com.example.alphakids.domain.repository

import android.net.Uri

interface ImageStorageRepository {

    suspend fun uploadImage(imageUri: Uri, path: String): Result<String>
}
