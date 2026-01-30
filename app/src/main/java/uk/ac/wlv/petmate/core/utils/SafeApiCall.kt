package uk.ac.wlv.petmate.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): Result<T> {
    return try {
        val result = withContext(Dispatchers.IO) {
            apiCall()
        }
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
