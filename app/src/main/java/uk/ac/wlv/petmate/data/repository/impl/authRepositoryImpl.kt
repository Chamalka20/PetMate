package uk.ac.wlv.petmate.data.repository.impl

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.remote.FirebaseUserDataSource
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.model.User
import kotlin.math.log

class AuthRepositoryImpl(
    private val userCache: UserCache,
    private val firebaseDataSource: FirebaseUserDataSource
) : AuthRepository {

    override suspend fun handleSignInResult(
        task: Task<GoogleSignInAccount>
    ): Result<User> {

        return try {
            val account = task.getResult(Exception::class.java)

            val user = firebaseDataSource.signInWithGoogle(account)

            // 💾 Cache locally
            userCache.saveUser(user)

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCachedUser(): User? {
        return userCache.getUser()
    }
}