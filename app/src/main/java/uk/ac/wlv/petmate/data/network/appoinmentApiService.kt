package uk.ac.wlv.petmate.data.network

import retrofit2.Response
import retrofit2.http.*
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AvailableSlotsDto
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.CancelAppointmentRequest
import uk.ac.wlv.petmate.data.model.UpdatePaymentRequest

interface AppointmentApiService {

    // ── Book appointment ──────────────────────────────────────────────
    // POST /api/appointments
    @POST("api/appointments")
    suspend fun bookAppointment(
        @Header("Authorization") token: String,
        @Body request: BookAppointmentRequest
    ): Response<Appointment>

    // ── Get my appointments ───────────────────────────────────────────
    // GET /api/appointments/my
    @GET("api/appointments/my")
    suspend fun getMyAppointments(@Header("Authorization") token: String,): Response<List<Appointment>>

    // ── Get upcoming appointments ─────────────────────────────────────
    // GET /api/appointments/my/upcoming
    @GET("api/appointments/my/upcoming")
    suspend fun getUpcomingAppointments(@Header("Authorization") token: String,): Response<List<Appointment>>

    // ── Get appointment history ───────────────────────────────────────
    // GET /api/appointments/my/history
    @GET("api/appointments/my/history")
    suspend fun getAppointmentHistory(@Header("Authorization") token: String,): Response<List<Appointment>>

    // ── Get single appointment ────────────────────────────────────────
    // GET /api/appointments/{id}
    @GET("api/appointments/{id}")
    suspend fun getAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Appointment>

    // ── Cancel appointment ────────────────────────────────────────────
    // PATCH /api/appointments/{id}/cancel
    @PATCH("api/appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("id") id     : Int,
        @Body      request : CancelAppointmentRequest
    ): Response<String>

    // ── Confirm appointment ───────────────────────────────────────────
    // PATCH /api/appointments/{id}/confirm
    @PATCH("api/appointments/{id}/confirm")
    suspend fun confirmAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<String>

    // ── Complete appointment ──────────────────────────────────────────
    // PATCH /api/appointments/{id}/complete
    @PATCH("api/appointments/{id}/complete")
    suspend fun completeAppointment(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<String>

    // ── Update payment ────────────────────────────────────────────────
    // PATCH /api/appointments/{id}/payment
    @PATCH("api/appointments/{id}/payment")
    suspend fun updatePayment(
        @Header("Authorization") token: String,
        @Path("id") id     : Int,
        @Body       request: UpdatePaymentRequest
    ): Response<String>

    // ── Get available slots ───────────────────────────────────────────
    // GET /api/appointments/slots?vetId=1&date=2024-12-25
    @GET("api/appointments/slots")
    suspend fun getAvailableSlots(
        @Header("Authorization") token: String,
        @Query("vetId") vetId: Int,
        @Query("date")  date : String  // "2024-12-25"
    ): Response<AvailableSlotsDto>
}