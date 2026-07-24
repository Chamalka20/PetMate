package uk.ac.wlv.petmate.data.repository.impl

import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.datasources.remote.VetRemoteDataSource
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.data.model.VetFilterState
import uk.ac.wlv.petmate.data.repository.VetRepository

class VetRepositoryImpl @Inject constructor(private val remoteDataSource: VetRemoteDataSource): VetRepository {
    override val isLastPage: Boolean
        get() = remoteDataSource.isLastPage
    override suspend fun getVetList(
        isRefresh   : Boolean,
        filter      : VetFilterState,
        searchQuery : String
    ): List<Vet> {
        return remoteDataSource.getVetsList(isRefresh, filter, searchQuery)
    }
    override suspend fun getVet(vetId: Int): Vet {
        return remoteDataSource.getVet( vetId)
    }

}