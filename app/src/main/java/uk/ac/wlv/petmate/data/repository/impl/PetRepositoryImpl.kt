package uk.ac.wlv.petmate.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import uk.ac.wlv.petmate.data.datasources.remote.PetRemoteDataSource
import uk.ac.wlv.petmate.data.repository.PetRepository
import uk.ac.wlv.petmate.model.Pet

class PetRepositoryImpl(
    private val remoteDataSource: PetRemoteDataSource
) : PetRepository {
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")
    override suspend fun savePet(pet: Pet): Pet {
        return remoteDataSource.createPet(userId, pet)
    }

    override suspend fun getPetList(): List<Pet> {
        return remoteDataSource.getPetList(userId)
    }

    override suspend fun getPet(petId: String): Pet? {
        return remoteDataSource.getPet(userId, petId)
    }

    override suspend fun updatePet(pet: Pet): Pet {
        return remoteDataSource.updatePet(userId, pet)
    }

    override suspend fun deletePet(petId: String): Boolean {
        return remoteDataSource.deletePet(userId, petId)
    }
}
