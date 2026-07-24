package uk.ac.wlv.petmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.utils.NetworkObserver
import uk.ac.wlv.petmate.data.network.InternetChecker


open class BaseViewModel @Inject constructor(

) : ViewModel() {




    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    protected suspend fun checkInternet(
        networkObserver: NetworkObserver
    ): Boolean {

        val connected = networkObserver
            .observeConnectivity()
            .first()

        if (!connected) {
            showError("No internet connection")
        }

        return connected
    }

    // Show error message
    protected fun showError(message: String) {
        SnackbarController.showError(message)
    }

    protected fun showSuccess(message: String) {
        SnackbarController.showSuccess(message)
    }



}