package uk.ac.wlv.petmate.data.repository

import uk.ac.wlv.petmate.data.model.Prescription

interface PrescriptionRepository {
    val isLastPage: Boolean
    suspend fun getMyPrescriptions(
        searchQuery: String? = null,
        isRefresh  : Boolean = false
    ): List<Prescription>
    suspend fun getPrescription(id: Int): Prescription
    suspend fun getPrescriptionByAppointment(appointmentId: Int): List<Prescription>
}