package uk.ac.wlv.petmate.data.repository.impl

import uk.ac.wlv.petmate.data.datasources.remote.AppointmentRemoteDataSource
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AvailableSlotsDto
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.CancelAppointmentRequest
import uk.ac.wlv.petmate.data.model.UpdatePaymentRequest
import uk.ac.wlv.petmate.data.repository.AppointmentRepository

class AppointmentRepositoryImpl(
    private val dataSource: AppointmentRemoteDataSource
) : AppointmentRepository {

    // ── Book appointment ──────────────────────────────────────────────
    override suspend fun bookAppointment(
        request: BookAppointmentRequest
    ): Appointment {
        return dataSource.bookAppointment(request)
    }

    // ── Get my appointments ───────────────────────────────────────────
    override suspend fun getMyAppointments(): List<Appointment> {
        return dataSource.getMyAppointments()
    }

    // ── Get upcoming appointments ─────────────────────────────────────
    override suspend fun getUpcomingAppointments(): List<Appointment> {
        return dataSource.getUpcomingAppointments()
    }

    // ── Get appointment history ───────────────────────────────────────
    override suspend fun getAppointmentHistory(): List<Appointment> {
        return dataSource.getAppointmentHistory()
    }

    // ── Get single appointment ────────────────────────────────────────
    override suspend fun getAppointment(id: Int): Appointment {
        return dataSource.getAppointment(id)
    }

    // ── Cancel appointment ────────────────────────────────────────────
    override suspend fun cancelAppointment(
        id     : Int,
        request: CancelAppointmentRequest
    ): Boolean {
        return dataSource.cancelAppointment(id, request)
    }

    // ── Confirm appointment ───────────────────────────────────────────
    override suspend fun confirmAppointment(id: Int): Boolean {
        return dataSource.confirmAppointment(id)
    }

    // ── Complete appointment ──────────────────────────────────────────
    override suspend fun completeAppointment(id: Int): Boolean {
        return dataSource.completeAppointment(id)
    }

    // ── Update payment ────────────────────────────────────────────────
    override suspend fun updatePayment(
        id     : Int,
        request: UpdatePaymentRequest
    ): Boolean {
        return dataSource.updatePayment(id, request)
    }

    // ── Get available slots ───────────────────────────────────────────
    override suspend fun getAvailableSlots(
        vetId: Int,
        date : String
    ): AvailableSlotsDto {
        return dataSource.getAvailableSlots(vetId, date)
    }
}