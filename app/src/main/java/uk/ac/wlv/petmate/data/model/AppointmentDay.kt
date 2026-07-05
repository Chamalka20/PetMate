package uk.ac.wlv.petmate.data.model

import uk.ac.wlv.petmate.core.utils.Constants.APPOINTMENT_SLOTS
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TimeSlot(
    val time: String,
    val isBooked: Boolean = false
)

data class AppointmentDay(
    val label: String,
    val date: LocalDate,
    val timeSlots: List<TimeSlot>
)

// ── Date to Tab Label ─────────────────────────────────────────────────────────

fun LocalDate.toTabLabel(): String {
    val today    = LocalDate.now()
    val tomorrow = today.plusDays(1)

    return when (this) {
        today    -> "Today\n${this.format(DateTimeFormatter.ofPattern("d MMM"))}"
        tomorrow -> "Tomorrow\n${this.format(DateTimeFormatter.ofPattern("d MMM"))}"
        else     -> "${this.format(DateTimeFormatter.ofPattern("EEE"))}\n${
            this.format(DateTimeFormatter.ofPattern("d MMM"))
        }"
    }
}