package uk.ac.wlv.petmate.data.datasources.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import uk.ac.wlv.petmate.model.Pet

class PetRemoteDataSource {

    private val firestore = Firebase.firestore
    private fun petsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("pets")

    suspend fun createPet(userId: String, pet: Pet): Pet {
        val docRef = petsCollection(userId).document()
        val petWithId = pet.copy(id = docRef.id)

        docRef.set(petWithId).await()
        return petWithId
    }

    suspend fun getPetList(userId: String): List<Pet> {
        val snapshot = petsCollection(userId).get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(Pet::class.java)
        }
    }

    suspend fun getPet(userId: String, petId: String): Pet? {
        val snapshot = petsCollection(userId).document(petId).get().await()
        return snapshot.toObject(Pet::class.java)
    }

    suspend fun updatePet(userId: String, pet: Pet): Pet {
        petsCollection(userId).document(pet.id).set(pet).await()
        return pet
    }

    suspend fun deletePet(userId: String, petId: String): Boolean {
        petsCollection(userId).document(petId).delete().await()
        return true
    }
}
