package uk.ac.wlv.petmate.data.repository.impl
import android.net.Uri
import uk.ac.wlv.petmate.data.datasources.remote.ImageDataSource
import uk.ac.wlv.petmate.data.repository.ImageRepository


class ImageRepositoryImpl(
    private val remoteDataSource: ImageDataSource
) : ImageRepository {

    override suspend fun uploadImage(
        imageUri: Uri,
        folder: String,
        compress: Boolean,
        quality: Int
    ): String {
        return remoteDataSource.uploadImage(
            imageUri,
            folder,
            compress,
            quality
        )
    }

    override suspend fun uploadImageWithProgress(
        imageUri: Uri,
        folder: String,
        compress: Boolean,
        quality: Int,
        onProgress: (Int) -> Unit
    ): String {
        return remoteDataSource.uploadImageWithProgress(
            imageUri,
            folder,
            compress,
            quality,
            onProgress
        )
    }

    override suspend fun uploadMultipleImages(
        imageUris: List<Uri>,
        folder: String,
        compress: Boolean,
        quality: Int,
        onProgress: ((current: Int, total: Int, progress: Int) -> Unit)?
    ): List<String> {
        return remoteDataSource.uploadMultipleImages(
            imageUris,
            folder,
            compress,
            quality,
            onProgress
        )
    }

    override suspend fun deleteImage(imageUrl: String): Boolean {
        return remoteDataSource.deleteImage(imageUrl)
    }



    override fun isImageSizeValid(imageUri: Uri, maxSizeMB: Int): Boolean {
        return remoteDataSource.isImageSizeValid(imageUri, maxSizeMB)
    }

    override fun getImageSize(imageUri: Uri): Long {
        return remoteDataSource.getImageSize(imageUri)
    }
}