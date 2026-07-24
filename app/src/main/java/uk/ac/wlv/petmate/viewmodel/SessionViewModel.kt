package uk.ac.wlv.petmate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.model.ApiUser
import uk.ac.wlv.petmate.data.repository.AuthRepository
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userCache: UserCache
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _user = MutableStateFlow<ApiUser?>(null)
    val user: StateFlow<ApiUser?> = _user

    init {
        viewModelScope.launch {
            val token = userCache.getToken()
            val user  = userCache.getUser()
            _isLoggedIn.value = token != null
            _user.value       = user
        }
    }

    fun setUser(user: ApiUser) {
        viewModelScope.launch {
            userCache.saveUser(user)
            userCache.saveToken(user.token)
            _user.value       = user
            _isLoggedIn.value = true
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userCache.clear()
            _user.value       = null
            _isLoggedIn.value = false
        }
    }
}