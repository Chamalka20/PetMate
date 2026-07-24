package uk.ac.wlv.petmate.data.session

import com.google.firebase.firestore.auth.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.model.ApiUser

@Singleton
class SessionManager @Inject constructor(
    private val userCache: UserCache
) {


    suspend fun saveUser(user: ApiUser) {
        userCache.saveUser(user)
    }


    suspend fun getUser(): ApiUser? {
        return userCache.getUser()
    }


    suspend fun clearSession() {
        userCache.clear()
    }
}