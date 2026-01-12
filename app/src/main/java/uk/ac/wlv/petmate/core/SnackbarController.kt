package uk.ac.wlv.petmate.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarController {

    private val _snackbarState = MutableStateFlow<SnackbarData?>(null)
    val snackbarState: StateFlow<SnackbarData?> = _snackbarState.asStateFlow()

    fun showSnackbar(
        message: String,
        type: SnackbarType = SnackbarType.Info,
        duration: SnackbarDuration = SnackbarDuration.SHORT,
        actionLabel: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        _snackbarState.value = SnackbarData(
            message = message,
            type = type,
            duration = duration,
            actionLabel = actionLabel,
            onActionClick = onActionClick
        )
    }

    // Convenience methods
    fun showSuccess(message: String, duration: SnackbarDuration = SnackbarDuration.SHORT) {
        showSnackbar(message, SnackbarType.Success, duration)
    }

    fun showError(message: String, duration: SnackbarDuration = SnackbarDuration.LONG) {
        showSnackbar(message, SnackbarType.Error, duration)
    }

    fun showWarning(message: String, duration: SnackbarDuration = SnackbarDuration.SHORT) {
        showSnackbar(message, SnackbarType.Warning, duration)
    }

    fun showInfo(message: String, duration: SnackbarDuration = SnackbarDuration.SHORT) {
        showSnackbar(message, SnackbarType.Info, duration)
    }

    fun showNoInternet() {
        showSnackbar("No internet connection", SnackbarType.NoInternet, SnackbarDuration.INDEFINITE)
    }

    fun showConnected() {
        showSnackbar("Connected", SnackbarType.Connected, SnackbarDuration.SHORT)
    }

    fun dismissSnackbar() {
        _snackbarState.value = null
    }
}