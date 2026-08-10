package uk.ac.wlv.petmate.data.model

data class Prescription(
    val id             : Int,
    val appointmentId  : Int,
    val petName        : String,
    val vetName        : String,
    val vetImageUrl    : String?,
    val clinicAddress  : String?,
    val appointmentType: Int,
    val diagnosis      : String?,
    val notes          : String?,
    val pdfUrl         : String?,
    val issuedAt       : String,
    val medicines      : List<Medicine>
)

data class Medicine(
    val id          : Int,
    val name        : String,
    val dosage      : String,
    val frequency   : String,
    val durationDays: Int,
    val instructions: String?,
    val form        : String?
)