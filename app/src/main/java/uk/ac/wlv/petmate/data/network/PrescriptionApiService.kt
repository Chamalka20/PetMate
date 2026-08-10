package uk.ac.wlv.petmate.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.wlv.petmate.data.model.PaginatedResponse
import uk.ac.wlv.petmate.data.model.Prescription

interface PrescriptionApiService {

    // ── Get my prescriptions ──────────────────────────────────────────
    @GET("api/prescriptions/my")
    suspend fun getMyPrescriptions(
        @Header("Authorization") token      : String,
        @Query("searchQuery")    searchQuery: String? = null,
        @Query("page")           page       : Int     = 1,
        @Query("pageSize")       pageSize   : Int     = 10
    ):  Response<PaginatedResponse<Prescription>>

    // ── Get single prescription ───────────────────────────────────────
    @GET("api/prescriptions/{id}")
    suspend fun getPrescription(
        @Header("Authorization") token: String,
        @Path("id")              id   : Int
    ): Response<Prescription>

    // ── Get by appointment ────────────────────────────────────────────
    @GET("api/prescriptions/appointment/{appointmentId}")
    suspend fun getPrescriptionByAppointment(
        @Header("Authorization") token         : String,
        @Path("appointmentId")   appointmentId : Int
    ): Response<List<Prescription>>
}