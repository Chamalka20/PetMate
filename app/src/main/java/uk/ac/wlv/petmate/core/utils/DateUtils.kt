package uk.ac.wlv.petmate.core.utils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toReadableDate(): String {
    return try {
        val datePart = this.substring(0, 10)

        val parsed = LocalDate.parse(
            datePart,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        )

        parsed.format(
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
        )
    } catch (e: Exception) {
        this
    }
}