package uk.ac.wlv.petmate.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.data.repository.AuthRepository

class AuthViewModel(private val repository: AuthRepository,private val sessionViewModel: SessionViewModel) : ViewModel() {



    var errorState = mutableStateOf<String?>(null)
        private set
    var signInSuccess = mutableStateOf(false)
    var isLoading = mutableStateOf(false)

    fun signIn(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            isLoading.value = true;
            val result = repository.handleSignInResult(task)
            result.onSuccess {
                sessionViewModel.setUser(it)
                signInSuccess.value = true
            }.onFailure {
                errorState.value = it.message
            }
            isLoading.value = false;
        }
    }
}