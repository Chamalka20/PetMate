package uk.ac.wlv.petmate.data.datasources.remote

import android.util.Log
import jakarta.inject.Inject
import uk.ac.wlv.petmate.data.model.Prescription
import uk.ac.wlv.petmate.data.network.ApiClient
import uk.ac.wlv.petmate.data.network.PrescriptionApiService
import kotlin.math.ceil

class PrescriptionRemoteDataSource @Inject constructor(
) {
    private var currentPage = 1
    var isLastPage = false
        private set
    // ── Get my prescriptions ──────────────────────────────────────────
    suspend fun getMyPrescriptions(
        token      : String,
        searchQuery: String? = null,
        isRefresh  : Boolean = false
    ): List<Prescription> {
        if (isRefresh) {
            currentPage = 1
            isLastPage  = false
        }

        if (isLastPage) return emptyList()

        val response = ApiClient.prescriptionApi.getMyPrescriptions(
            token       = token,
            searchQuery = searchQuery,
            page        = currentPage,
            pageSize    = 10
        )

        if (response.isSuccessful) {
            val body       = response.body() ?: return emptyList()
            val totalPages = ceil(body.total.toDouble() / 10).toInt()
            isLastPage     = this.currentPage >= totalPages
            if (!isLastPage) currentPage++
            return body.data
        }

        throw Exception("Failed: ${response.errorBody()?.string()}")
    }

    // ── Get single prescription ───────────────────────────────────────
    suspend fun getPrescription(token: String, id: Int): Prescription {
        Log.d("Prescription", "► getPrescription id=$id")

        val response = ApiClient.prescriptionApi.getPrescription(token, id)

        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Empty response")
        }

        val errorBody = response.errorBody()?.string()
        Log.e("Prescription", "✗ Failed: $errorBody")
        throw Exception("Failed to get prescription: $errorBody")
    }

    // ── Get by appointment ────────────────────────────────────────────
    suspend fun getPrescriptionByAppointment(
        token        : String,
        appointmentId: Int
    ): List<Prescription> {
        Log.d("Prescription", "► getPrescriptionByAppointment id=$appointmentId")

        val response = ApiClient.prescriptionApi.getPrescriptionByAppointment(token, appointmentId)

        if (response.isSuccessful) {
            val body = response.body() ?: emptyList()
            Log.d("Prescription", "✓ Got ${body.size} prescriptions")
            return body
        }

        val errorBody = response.errorBody()?.string()
        Log.e("Prescription", "✗ Failed: $errorBody")
        throw Exception("Failed to get prescriptions: $errorBody")
    }
}