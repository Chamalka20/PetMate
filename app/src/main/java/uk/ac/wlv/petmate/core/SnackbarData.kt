package uk.ac.wlv.petmate.core

data class SnackbarData(
    val message: String,
    val type: SnackbarType = SnackbarType.Info,
    val duration: SnackbarDuration = SnackbarDuration.SHORT,
    val actionLabel: String? = null,
    val onActionClick: (() -> Unit)? = null
)

enum class SnackbarDuration(val milliseconds: Long) {
    SHORT(3000L),
    LONG(5000L),
    INDEFINITE(Long.MAX_VALUE)
}