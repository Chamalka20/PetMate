package uk.ac.wlv.petmate.data.repository.impl
import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.remote.UserDataSource
import uk.ac.wlv.petmate.data.model.ApiUser
import uk.ac.wlv.petmate.data.repository.AuthRepository

class AuthRepositoryImpl @Inject constructor(
    private val userCache: UserCache,
    private val UserDataSource: UserDataSource
) : AuthRepository {

    override suspend fun GoogleSignInResult(
        idToken: String
    ): ApiUser {

            val user = UserDataSource.signInWithGoogle(idToken)
            // 💾 Cache locally
            userCache.saveUser(user)

            Result.success(user)

        return user
    }

    override suspend fun normalLogin(email: String, password: String): ApiUser{

        val user = UserDataSource.login(email,password)
        // 💾 Cache locally
        userCache.saveUser(user)
        Result.success(user)

        return user

    }

    override suspend fun getCachedUser(): ApiUser? {
        return userCache.getUser()
    }
}