package uk.ac.wlv.petmate.data.repository

import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AppointmentActionResponse
import uk.ac.wlv.petmate.data.model.AvailableSlotsDto
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.CancelAppointmentRequest
import uk.ac.wlv.petmate.data.model.UpdatePaymentRequest

interface AppointmentRepository {
    val historyIsLastPage  : Boolean

    suspend fun bookAppointment(request: BookAppointmentRequest): Appointment

    suspend fun getMyAppointments(): List<Appointment>

    suspend fun getUpcomingAppointments(): List<Appointment>

    suspend fun getAppointmentHistory( isRefresh : Boolean  = false,vetName: String? = null,
                                       appointmentDate: String? = null): List<Appointment>

    suspend fun getAppointment(id: Int): Appointment

    suspend fun cancelAppointment(
        id     : Int,
        request: CancelAppointmentRequest
    ): AppointmentActionResponse

    suspend fun confirmAppointment(id: Int): Boolean

    suspend fun completeAppointment(id: Int): Boolean

    suspend fun updatePayment(
        id     : Int,
        request: UpdatePaymentRequest
    ): Boolean

    suspend fun getAvailableSlots(
        vetId: Int,
        date : String
    ): AvailableSlotsDto
}