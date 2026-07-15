package uk.ac.wlv.petmate.data.datasources.remote
import android.util.Log
import uk.ac.wlv.petmate.core.utils.Constants.PAGE_SIZE
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AppointmentActionResponse
import uk.ac.wlv.petmate.data.model.AvailableSlotsDto
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.CancelAppointmentRequest
import uk.ac.wlv.petmate.data.model.UpdatePaymentRequest
import uk.ac.wlv.petmate.data.network.ApiClient
import kotlin.math.ceil


class AppointmentRemoteDataSource(
    private val userCache: UserCache
) {
    private suspend fun bearerToken() =
        "Bearer ${userCache.getToken()}"

    private var historyCurrentPage = 1
    var historyIsLastPage          = false
        private set

    // ── Book appointment ──────────────────────────────────────────────
    suspend fun bookAppointment(
        request: BookAppointmentRequest
    ): Appointment {


        val response = ApiClient.appointmentApi.bookAppointment(request = request, token = bearerToken())


        if (response.isSuccessful) {
            val body = response.body() ?: throw Exception("Empty response from server")

            return body

        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Failed to book appointment: $errorBody")
        }
    }

    // ── Get my appointments ───────────────────────────────────────────
    suspend fun getMyAppointments(): List<Appointment> {
        val response =ApiClient.appointmentApi.getMyAppointments(token = bearerToken())
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception("Failed to get appointments: ${response.errorBody()?.string()}")
    }

    // ── Get upcoming appointments ─────────────────────────────────────
    suspend fun getUpcomingAppointments(): List<Appointment> {
        val response = ApiClient.appointmentApi.getUpcomingAppointments(token = bearerToken())
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception("Failed to get upcoming appointments: ${response.errorBody()?.string()}")
    }

    // ── Get appointment history ───────────────────────────────────────
    suspend fun getAppointmentHistory(  isRefresh: Boolean = false): List<Appointment> {
        if (isRefresh) {
            historyCurrentPage = 1
            historyIsLastPage  = false
        }

        if (historyIsLastPage) return emptyList()

        val response = ApiClient.appointmentApi.getAppointmentHistory(
            token    = bearerToken(),
            page     = historyCurrentPage,
            pageSize = PAGE_SIZE
        )

        val totalPages = ceil(response.total.toDouble() / PAGE_SIZE).toInt()
        historyIsLastPage = historyCurrentPage >= totalPages
        if (!historyIsLastPage) historyCurrentPage++
        return response.data

    }

    // ── Get single appointment ────────────────────────────────────────
    suspend fun getAppointment(id: Int): Appointment {
        val response = ApiClient.appointmentApi.getAppointment( bearerToken(),id)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Appointment not found")
        }
        throw Exception("Failed to get appointment: ${response.errorBody()?.string()}")
    }

    // ── Cancel appointment ────────────────────────────────────────────
    suspend fun cancelAppointment(
        id     : Int,
        request: CancelAppointmentRequest
    ): AppointmentActionResponse {
        val response = ApiClient.appointmentApi.cancelAppointment(token = bearerToken(),id, request)
        return response.body()?: throw Exception("Empty response")
    }

    // ── Confirm appointment ───────────────────────────────────────────
    suspend fun confirmAppointment(id: Int): Boolean {
        val response = ApiClient.appointmentApi.confirmAppointment(token = bearerToken(),id)
        return response.isSuccessful
    }

    // ── Complete appointment ──────────────────────────────────────────
    suspend fun completeAppointment(id: Int): Boolean {
        val response = ApiClient.appointmentApi.completeAppointment(token = bearerToken(),id)
        return response.isSuccessful
    }

    // ── Update payment ────────────────────────────────────────────────
    suspend fun updatePayment(
        id     : Int,
        request: UpdatePaymentRequest
    ): Boolean {
        val response = ApiClient.appointmentApi.updatePayment(token = bearerToken(),id, request)
        return response.isSuccessful
    }

    // ── Get available slots ───────────────────────────────────────────
    suspend fun getAvailableSlots(
        vetId: Int,
        date : String
    ): AvailableSlotsDto {
        val response = ApiClient.appointmentApi.getAvailableSlots(bearerToken(),vetId, date)
        if (response.isSuccessful) {
            return response.body()
                ?: throw Exception("Empty slots response")
        }
        throw Exception("Failed to get slots: ${response.errorBody()?.string()}")
    }
}