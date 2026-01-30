package uk.ac.wlv.petmate.data.repository

import android.net.Uri

interface ImageRepository {

    suspend fun uploadImage(
        imageUri: Uri,
        folder: String ,
        compress: Boolean = true,
        quality: Int = 75
    ): String

    suspend fun uploadImageWithProgress(
        imageUri: Uri,
        folder: String ,
        compress: Boolean = true,
        quality: Int = 75,
        onProgress: (Int) -> Unit
    ): String

    suspend fun uploadMultipleImages(
        imageUris: List<Uri>,
        folder: String ,
        compress: Boolean = true,
        quality: Int = 75,
        onProgress: ((current: Int, total: Int, progress: Int) -> Unit)? = null
    ): List<String>


    suspend fun deleteImage(imageUrl: String): Boolean

    fun isImageSizeValid(imageUri: Uri, maxSizeMB: Int = 5): Boolean

    fun getImageSize(imageUri: Uri): Long
}