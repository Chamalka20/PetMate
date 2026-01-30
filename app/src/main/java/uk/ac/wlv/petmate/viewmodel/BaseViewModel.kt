package uk.ac.wlv.petmate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.data.network.InternetChecker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject



open class BaseViewModel(
   
) : ViewModel()  , KoinComponent{
    private val internetChecker: InternetChecker by inject()

    // Internet connectivity
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            internetChecker.observeInternetConnectivity().collect { connected ->
                val wasConnected = _isConnected.value
                _isConnected.value = connected
                if (!wasConnected && connected) {
                    //onConnectionRestored()
                    SnackbarController.showConnected()
                }

                if (wasConnected && !connected) {
                    //onConnectionLost()
                    SnackbarController.showNoInternet()
                }

            }
        }
    }

    // Show error message
    protected fun showError(message: String) {
        SnackbarController.showError(message)
    }

    protected fun showSuccess(message: String) {
        SnackbarController.showSuccess(message)
    }

    // Execute with internet check
    fun checkInternetAndExecute(
        onConnected: () -> Unit,

    ) {
        if (_isConnected.value) {
            onConnected()
        }
    }

}