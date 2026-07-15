package uk.ac.wlv.petmate.data.model

enum class AppointmentStatus(val value: Int, val label: String) {
    PENDING              (0, "Pending"),
    COMPLETED            (2, "Completed"),
    CANCELLED_BY_USER    (3, "Cancelled"),
    CANCELLED_BY_VET     (4, "Cancelled by Vet"),
    CANCELLED_BY_ADMIN   (5, "Cancelled by Admin");

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: PENDING
    }
}

enum class CancelledBy(val value: String) {
    USER  ("user"),
    VET   ("vet"),
    ADMIN ("admin");

    companion object {
        fun fromValue(value: String) = entries.find { it.value == value } ?: USER
    }
}