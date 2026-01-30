package uk.ac.wlv.petmate.data.repository

import uk.ac.wlv.petmate.model.Pet

interface PetRepository {

    suspend fun savePet(pet: Pet): Pet

    suspend fun getPetList(): List<Pet>

    suspend fun getPet(petId: String): Pet?

    suspend fun updatePet(pet: Pet): Pet

    suspend fun deletePet(petId: String): Boolean
}
