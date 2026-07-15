package uk.ac.wlv.petmate.data.model

data class Appointment(
    val id               : Int,

    // ── Vet ──────────────────────────────────────────────────────────
    val vetId            : Int,
    val vetName          : String,
    val vetImageUrl      : String?,
    val clinicName       : String?,
    val clinicAddress    : String?,
    val clinicLatitude   : Double?,
    val clinicLongitude  : Double?,

    // ── Pet ───────────────────────────────────────────────────────────
    val petName          : String,
    val petType          : String?,
    val petBreed         : String?,

    // ── Appointment ───────────────────────────────────────────────────
    val appointmentDate  : String,
    val timeSlot         : String,
    val type             : Int,
    val status           : Int,
    val serviceType      : String?,
    val notes            : String?,

    // ── Home Visit ────────────────────────────────────────────────────
    val homeAddress      : String?,
    val homeLatitude     : Double?,
    val homeLongitude    : Double?,
    val homeAddressNotes : String?,

    // ── Payment ───────────────────────────────────────────────────────
    val consultationFee  : Double,
    val homeVisitFee     : Double?,
    val emergencyFee     : Double?,
    val totalFee         : Double,
    val paymentStatus    : Int,
    val paymentMethod    : String?,

    // ── Timestamps ────────────────────────────────────────────────────
    val createdAt           : String,
    val cancellationReason  : String?
)

// ── Request model ─────────────────────────────────────────────────────────────
data class BookAppointmentRequest(
    val vetId            : Int,
    val petId            : Int,
    val appointmentDate  : String,
    val timeSlot         : String,
    val type             : Int,
    val serviceType      : String?,
    val notes            : String?,
    val paymentMethod    : String?,

    // ── Home visit ────────────────────────────────────────────────────
    val homeAddress      : String?,
    val homeLatitude     : Double?,
    val homeLongitude    : Double?,
    val homeAddressNotes : String?
)

data class CancelAppointmentRequest(
    val reason: String,
    val cancelledBy: String
)

data class UpdatePaymentRequest(
    val paymentStatus: Int,
    val paymentMethod: String?
)

// ── Slots ─────────────────────────────────────────────────────────────────────
data class AvailableSlotsDto(
    val vetId           : Int,
    val vetName         : String,
    val date            : String,
    val vetWorksOnThisDay: Boolean,
    val slots           : List<TimeSlotDto>
)

data class TimeSlotDto(
    val slot       : String,
    val isAvailable: Boolean
)

data class AppointmentActionResponse(
    val success: Boolean,
    val message: String
)

data class AppointmentListResponse(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val data: List<Appointment>
)