package uk.ac.wlv.petmate.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import uk.ac.wlv.petmate.model.User


interface AuthRepository {

    suspend fun handleSignInResult(
        task: Task<GoogleSignInAccount>
    ): User

    suspend fun getCachedUser(): User?
}