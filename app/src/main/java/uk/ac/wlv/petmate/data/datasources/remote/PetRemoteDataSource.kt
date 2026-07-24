package uk.ac.wlv.petmate.data.datasources.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.network.ApiClient

class PetRemoteDataSource @Inject constructor(private val userCache: UserCache) {

    private suspend fun bearerToken() =
        "Bearer ${userCache.getToken()}"

    suspend fun createPet(pet: Pet): Boolean {

        val response = ApiClient.petApi.createPet(
            token   = bearerToken(),
            request = pet
        )

        if (!response.isSuccessful) {
            Log.e("PetDebug", "Failed to create pet: ${response.errorBody()?.string()}")
        }

        if (response.isSuccessful) return true
        throw Exception("Failed to create pet: ${response.errorBody()?.string()}")
    }

    suspend fun getPetList(): List<Pet> {
        val response = ApiClient.petApi.getPetList(
            token = bearerToken()
        )

        if (response.isSuccessful) {
            val body = response.body()
            return body?.pets ?: emptyList()
        }

        throw Exception("Failed to get pets: ${response.errorBody()?.string()}")
    }

    // ── Get single pet ────────────────────────────
    suspend fun getPet(petId: Int): Pet {
        Log.d("PetDebug", "Fetching pet with ID: $petId") // Debug point 1

        val response = ApiClient.petApi.getPet(
            token = bearerToken(),
            petId = petId
        )
        Log.d("PetDebug", "Response received: code=${response.code()}, successful=${response.isSuccessful}") // Debug point 2

        if (response.isSuccessful) {
            val pet = response.body()
            Log.d("PetDebug", "Pet body: $pet") // Debug point 3
            return pet ?: throw Exception("Pet not found")
        }

        val error = response.errorBody()?.string()
        Log.e("PetDebug", "Failed to get pet: $error") // Debug point 4
        throw Exception("Failed to get pet: $error")
    }


    // ── Update pet ────────────────────────────────
    suspend fun updatePet(petId: Int, pet: Pet): Boolean {
        try {

            val response = ApiClient.petApi.updatePet(
                token = bearerToken(),
                petId = petId,
                request = pet
            )

            if (response.isSuccessful) {
                return true
            }
            val error = response.errorBody()?.string()
            throw Exception("Failed to update pet: $error")

        } catch (e: Exception) {
            Log.e("PetDebug", "Exception occurred while updating pet: ${e.message}", e)
            throw e
        }
    }

    suspend fun deletePet(petId: Int): Boolean {
        val response = ApiClient.petApi.deletePet(
            token = bearerToken(),
            petId = petId
        )
        if (response.isSuccessful) return true
        throw Exception("Failed to delete pet: ${response.errorBody()?.string()}")
    }
}
