package uk.ac.wlv.petmate.data.datasources.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import uk.ac.wlv.petmate.BuildConfig
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resumeWithException
import androidx.core.graphics.scale
import kotlin.coroutines.resume

class ImageDataSource(private val context: Context) {

    companion object {
        private const val TAG = "ImageDataSource"
        private const val MAX_IMAGE_WIDTH = 1200
        private const val MAX_IMAGE_HEIGHT = 1200
        private const val MAX_FILE_SIZE_MB = 5

        private var isInitialized = false
    }

    init {
        // Initialize Cloudinary
        if (!isInitialized) {
            try {
                val config = hashMapOf(
                    "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
                    "api_key" to BuildConfig.CLOUDINARY_API_KEY,
                    "api_secret" to BuildConfig.CLOUDINARY_API_SECRET,
                    "secure" to true
                )
                MediaManager.init(context, config)
                isInitialized = true
                Log.d(TAG, "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Cloudinary", e)
            }
        }
    }

    suspend fun uploadImage(
        imageUri: Uri,
        folder: String,
        compress: Boolean,
        quality: Int
    ): String = suspendCancellableCoroutine { continuation ->
        try {
            // Validate size
            if (!isImageSizeValid(imageUri, MAX_FILE_SIZE_MB)) {
                continuation.resumeWithException(
                    Exception("Image size exceeds ${MAX_FILE_SIZE_MB}MB limit")
                )
                return@suspendCancellableCoroutine
            }

            Log.d(TAG, "Uploading image to folder: $folder")

            val uploadRequest = if (compress) {
                val compressedData = compressImage(imageUri, quality)
                Log.d(TAG, "Compressed size: ${compressedData.size / 1024}KB")
                MediaManager.get().upload(compressedData)
            } else {
                MediaManager.get().upload(imageUri)
            }

            val requestId = uploadRequest
                .option("folder", folder)
                .option("resource_type", "image")
                .option("quality", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Progress not needed for this method
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d(TAG, "Upload successful: $url")
                            continuation.resume(url)
                        } else {
                            Log.e(TAG, "No URL in response")
                            continuation.resumeWithException(Exception("No URL in response"))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Upload failed: ${error.description}")
                        continuation.resumeWithException(Exception("Upload failed: ${error.description}"))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

            // Handle cancellation
            continuation.invokeOnCancellation {
                try {
                    MediaManager.get().cancelRequest(requestId)
                    Log.d(TAG, "Upload cancelled: $requestId")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to cancel upload", e)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Upload exception", e)
            continuation.resumeWithException(Exception("Failed to upload image: ${e.message}"))
        }
    }

    suspend fun uploadImageWithProgress(
        imageUri: Uri,
        folder: String,
        compress: Boolean,
        quality: Int,
        onProgress: (Int) -> Unit
    ): String = suspendCancellableCoroutine { continuation ->
        try {
            if (!isImageSizeValid(imageUri, MAX_FILE_SIZE_MB)) {
                continuation.resumeWithException(
                    Exception("Image size exceeds ${MAX_FILE_SIZE_MB}MB limit")
                )
                return@suspendCancellableCoroutine
            }

            Log.d(TAG, "Uploading with progress to folder: $folder")

            val uploadRequest = if (compress) {
                val compressedData = compressImage(imageUri, quality)
                MediaManager.get().upload(compressedData)
            } else {
                MediaManager.get().upload(imageUri)
            }

            val requestId = uploadRequest
                .option("folder", folder)
                .option("resource_type", "image")
                .option("quality", "auto")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started: $requestId")
                        onProgress(0)
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = ((bytes.toDouble() / totalBytes.toDouble()) * 100).toInt()
                        onProgress(progress)
                        Log.d(TAG, "Progress: $progress%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            onProgress(100)
                            Log.d(TAG, "Upload with progress successful: $url")
                            continuation.resume(url)
                        } else {
                            continuation.resumeWithException(Exception("No URL in response"))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Upload with progress failed: ${error.description}")
                        continuation.resumeWithException(Exception("Upload failed: ${error.description}"))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                try {
                    MediaManager.get().cancelRequest(requestId)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to cancel upload", e)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Upload with progress exception", e)
            continuation.resumeWithException(Exception("Failed to upload image: ${e.message}"))
        }
    }

    suspend fun uploadMultipleImages(
        imageUris: List<Uri>,
        folder: String,
        compress: Boolean,
        quality: Int,
        onProgress: ((current: Int, total: Int, progress: Int) -> Unit)?
    ): List<String> {
        val urls = mutableListOf<String>()
        val total = imageUris.size

        Log.d(TAG, "Uploading $total images")

        imageUris.forEachIndexed { index, uri ->
            try {
                val url = uploadImageWithProgress(
                    imageUri = uri,
                    folder = folder,
                    compress = compress,
                    quality = quality
                ) { imageProgress ->
                    val overallProgress = ((index * 100 + imageProgress) / total)
                    onProgress?.invoke(index + 1, total, overallProgress)
                }
                urls.add(url)
                Log.d(TAG, "Image ${index + 1}/$total uploaded")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upload image ${index + 1}", e)
                throw Exception("Failed to upload image ${index + 1}: ${e.message}")
            }
        }

        Log.d(TAG, "All $total images uploaded")
        return urls
    }

    suspend fun deleteImage(imageUrl: String): Boolean = suspendCancellableCoroutine { continuation ->

    }



    fun isImageSizeValid(imageUri: Uri, maxSizeMB: Int): Boolean {
        val sizeInBytes = getImageSize(imageUri)
        val sizeInMB = sizeInBytes / (1024 * 1024)
        return sizeInMB <= maxSizeMB
    }

    fun getImageSize(imageUri: Uri): Long {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val size = inputStream?.available()?.toLong() ?: 0L
            inputStream?.close()
            size
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get image size", e)
            0L
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Compress image to reduce file size
     */
    private fun compressImage(imageUri: Uri, quality: Int): ByteArray {
        // Open input stream
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Cannot open image")

        // Decode bitmap
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Calculate new dimensions
        val width = originalBitmap.width
        val height = originalBitmap.height

        val scaledBitmap = if (width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT) {
            val scale = minOf(
                MAX_IMAGE_WIDTH.toFloat() / width,
                MAX_IMAGE_HEIGHT.toFloat() / height
            )
            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            originalBitmap.scale(newWidth, newHeight)
        } else {
            originalBitmap
        }

        // Compress to JPEG
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Clean up
        originalBitmap.recycle()
        if (scaledBitmap != originalBitmap) {
            scaledBitmap.recycle()
        }

        return outputStream.toByteArray()
    }

    /**
     * Extract public_id from Cloudinary URL
     * Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/pets/abc123.jpg
     * Returns: pets/abc123
     */
    private fun extractPublicId(url: String): String {
        return try {
            // Split by "/upload/"
            val parts = url.split("/upload/")
            if (parts.size < 2) return ""

            // Get everything after /upload/
            val afterUpload = parts[1]

            // Remove version (v1234567890/) if present
            val withoutVersion = if (afterUpload.startsWith("v")) {
                afterUpload.split("/").drop(1).joinToString("/")
            } else {
                afterUpload
            }

            // Remove file extension
            withoutVersion.substringBeforeLast(".")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract public_id from URL: $url", e)
            ""
        }
    }
}
