package uk.ac.wlv.petmate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.model.User

class SessionViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentUser = mutableStateOf<User?>(null)
        private set

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            currentUser.value = authRepository.getCachedUser()
        }
    }

    fun setUser(user: User) {
        currentUser.value = user
    }

    fun logout() {
        viewModelScope.launch {
//            authRepository.logout()
            currentUser.value = null
        }
    }
}
