package uk.ac.wlv.petmate.data.repository.impl

import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.remote.PrescriptionRemoteDataSource
import uk.ac.wlv.petmate.data.model.Prescription
import uk.ac.wlv.petmate.data.repository.PrescriptionRepository

class PrescriptionRepositoryImpl @Inject constructor(
    private val dataSource: PrescriptionRemoteDataSource,
    private val userCache : UserCache
) : PrescriptionRepository {
    override val isLastPage: Boolean
        get() =dataSource.isLastPage
    private suspend fun bearerToken(): String {
        return "Bearer ${userCache.getToken() ?: ""}"
    }

    override suspend fun getMyPrescriptions(
                                             searchQuery: String? ,
                                             isRefresh  : Boolean ): List<Prescription> {
        return dataSource.getMyPrescriptions(token = bearerToken(), searchQuery = searchQuery, isRefresh = isRefresh)
    }

    override suspend fun getPrescription(id: Int): Prescription {
        return dataSource.getPrescription(bearerToken(), id)
    }

    override suspend fun getPrescriptionByAppointment(
        appointmentId: Int
    ): List<Prescription> {
        return dataSource.getPrescriptionByAppointment(bearerToken(), appointmentId)
    }
}