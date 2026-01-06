package uk.ac.wlv.petmate.data.datasources.remote

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import uk.ac.wlv.petmate.model.User

class FirebaseUserDataSource {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    suspend fun signInWithGoogle(
        account: GoogleSignInAccount
    ): User {

        val credential = GoogleAuthProvider.getCredential(
            account.idToken,
            null
        )

        val authResult = auth
            .signInWithCredential(credential)
            .await()

        val firebaseUser = authResult.user
            ?: throw Exception("Firebase user is null")

        val user = User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )

        //  Save to Firestore
        try {
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await()
            println("User saved successfully!")
        } catch (e: Exception) {
            Log.d("TAG", "${e.message}")

        }

        return user
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString()
        )
    }

    fun signOut() {
        auth.signOut()
    }
}