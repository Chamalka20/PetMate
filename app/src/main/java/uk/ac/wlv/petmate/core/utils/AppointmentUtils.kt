package uk.ac.wlv.petmate.core.utils

import android.util.Log
import uk.ac.wlv.petmate.core.utils.Constants.APPOINTMENT_SLOTS
import uk.ac.wlv.petmate.data.model.AppointmentDay
import uk.ac.wlv.petmate.data.model.TimeSlot
import uk.ac.wlv.petmate.data.model.TimeSlotDto
import uk.ac.wlv.petmate.data.model.toTabLabel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun generateAppointmentDays(
    count          : Int           = 5,
    availableSlots : List<TimeSlotDto> = emptyList()
): List<AppointmentDay> {
    val today = LocalDate.now()
    val now   = LocalTime.now()
    return (0 until count).map { offset ->
        val date = today.plusDays(offset.toLong())
        val isToday = offset == 0

        val mappedSlots =availableSlots.map { slot ->

            val isPastSlot = if (isToday) {
                isSlotInPast(slot.slot, now)
            } else {
                false
            }
            TimeSlot(
                time = slot.slot,
                isBooked = !slot.isAvailable || isPastSlot
            )
        }
        AppointmentDay(
            label     = date.toTabLabel(),
            date      = date,
            timeSlots = mappedSlots
        )
    }
}

fun isSlotInPast(slot: String, now: LocalTime): Boolean {
    return try {
        val timeStr  = slot.split(" - ").first().trim()
        val slotTime = LocalTime.parse(
            timeStr,
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        )
        slotTime.isBefore(now)
    } catch (e: Exception) {
        Log.e("AppointmentUtils", "Failed to parse slot time: $slot")
        false
    }
}