package uk.ac.wlv.petmate.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.safeApiCall
import uk.ac.wlv.petmate.data.model.ApiUser
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.data.session.SessionManager
import uk.ac.wlv.petmate.services.GoogleAuthService
@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository, private val sessionManager: SessionManager, private val googleAuthService:GoogleAuthService) : BaseViewModel() {


    private val _loginState = MutableStateFlow<UiState<ApiUser>>(UiState.Idle)
    val loginState: StateFlow<UiState<ApiUser>> = _loginState.asStateFlow()


    fun signInWithGoogle() {
        viewModelScope.launch {

            try {
                val idToken = googleAuthService.getGoogleIdToken()
                    ?: return@launch
                // send to backend → get ApiUser back
                performSignIn(idToken)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    private fun performSignIn(idToken: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            val result = safeApiCall {
                repository.GoogleSignInResult(idToken)
            }
            result.onSuccess { user ->
                sessionManager.saveUser(user)
                _loginState.value = UiState.Success(user)
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Sign in failed"
                _loginState.value = UiState.Error(errorMessage)
                showError(errorMessage)
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }
}