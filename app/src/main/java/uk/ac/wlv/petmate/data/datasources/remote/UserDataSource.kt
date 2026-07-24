package uk.ac.wlv.petmate.data.datasources.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.model.ApiUser
import uk.ac.wlv.petmate.data.model.GoogleSignInRequest
import uk.ac.wlv.petmate.data.model.LoginRequest
import uk.ac.wlv.petmate.data.model.RegisterRequest
import uk.ac.wlv.petmate.data.network.ApiClient

class UserDataSource @Inject constructor() {

    private val auth = Firebase.auth

    // ── Google Sign-In ────────────────────────────────────
    suspend fun signInWithGoogle(idToken: String): ApiUser {

        // Step 1 — Send Google ID token directly to your backend
        val response = ApiClient.authApi.googleSignIn(
            GoogleSignInRequest(idToken = idToken)
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            Log.e("TAG", "API Error: $errorBody")
            throw Exception("Sign-in failed: $errorBody")
        }

        // Step 2 — Handle response
        val apiUser = response.body()?.user
            ?: throw Exception("Empty response from server")

        // Step 3 — Save your backend JWT token
        saveToken(apiUser.token)

        Log.d("TAG", "User saved successfully via .NET API!")
        return apiUser
    }

    // ── Normal Login ──────────────────────────────────────
    suspend fun login(email: String, password: String): ApiUser {

        val response = ApiClient.authApi.login(
            LoginRequest(email = email, password = password)
        )

        if (response.isSuccessful) {
            val apiUser = response.body()?.user
                ?: throw Exception("Empty response from server")

            saveToken(apiUser.token)
            return apiUser

        } else {
            throw Exception("Invalid email or password")
        }
    }

    // ── Register ──────────────────────────────────────────
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phoneNumber: String? = null,
        profilePhotoUrl: String? = null
    ): ApiUser {

        val response =ApiClient.authApi.register(
            RegisterRequest(
                fullName = fullName,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
                profilePhotoUrl = profilePhotoUrl
            )
        )

        if (response.isSuccessful) {
            val apiUser = response.body()?.user
                ?: throw Exception("Empty response from server")

            saveToken(apiUser.token)
            return apiUser

        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Registration failed: $errorBody")
        }
    }

    // ── Get current user ──────────────────────────────────
    fun getCurrentUser(): ApiUser? {
        // Check if JWT token exists locally
        val token = getToken() ?: return null
        val firebaseUser = auth.currentUser ?: return null

        // Return basic user info from Firebase
        // For full info — call GET /api/profile (build this later)
        return ApiUser(
            id = firebaseUser.uid,
            fullName = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            phoneNumber = null,
            profilePhotoUrl = firebaseUser.photoUrl?.toString(),
            isGoogleUser = true,
            token = token,
            createdAt = ""
        )
    }

    // ── Sign out ──────────────────────────────────────────
    fun signOut() {
        auth.signOut()
        clearToken() // also clear saved JWT
    }

    // ── Token management (SharedPreferences) ──────────────
    private fun saveToken(token: String) {
        // Save to SharedPreferences
        // You need context for this — inject it or use a singleton
        Log.d("TAG", "JWT Token saved: $token")
    }

    private fun getToken(): String? {
        // Get from SharedPreferences
        return null
    }

    private fun clearToken() {
        // Clear from SharedPreferences
    }
}