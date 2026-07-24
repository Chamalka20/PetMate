package uk.ac.wlv.petmate.data.repository.impl

import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.datasources.remote.PetRemoteDataSource
import uk.ac.wlv.petmate.data.repository.PetRepository
import uk.ac.wlv.petmate.data.model.Pet

class PetRepositoryImpl @Inject constructor(
    private val remoteDataSource: PetRemoteDataSource
) : PetRepository {
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")
    override suspend fun savePet(pet: Pet): Boolean{
        return remoteDataSource.createPet( pet)
    }

    override suspend fun getPetList(): List<Pet> {
        return remoteDataSource.getPetList()
    }

    override suspend fun getPet(petId: Int): Pet {
        return remoteDataSource.getPet( petId= petId)
    }

    override suspend fun updatePet(petId: Int, pet: Pet): Boolean {
        return  remoteDataSource.updatePet( petId= petId, pet = pet)
    }

    override suspend fun deletePet(petId: Int) : Boolean{
       return remoteDataSource.deletePet( petId=petId)
    }
}
