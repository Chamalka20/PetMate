package uk.ac.wlv.petmate.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.network.InternetChecker
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.model.User

class AuthViewModel(private val repository: AuthRepository,private val sessionViewModel: SessionViewModel) : BaseViewModel() {


    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()


    fun signIn(task: Task<GoogleSignInAccount>) {
        checkInternetAndExecute(
            onConnected = {
                performSignIn(task)
            },

        )
    }

    private fun performSignIn(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            val result = repository.handleSignInResult(task)

            result.onSuccess { user ->
                sessionViewModel.setUser(user)
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