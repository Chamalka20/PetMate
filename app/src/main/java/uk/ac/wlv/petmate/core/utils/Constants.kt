package uk.ac.wlv.petmate.core.utils

object Constants {

    // ── Environment ───────────────────────────────────────
    const val IS_PRODUCTION = true

    // ── API URLs ──────────────────────────────────────────
    const val LOCAL_URL      = "http://10.0.2.2:7045/"
    const val PRODUCTION_URL = "https://petmateapi-production-90c8.up.railway.app/"

    // ── Active URL ────────────────────────────────────────
    val BASE_URL = if (IS_PRODUCTION) PRODUCTION_URL else LOCAL_URL


    val VET_SERVICES = listOf(
        "Checkup",
        "Vaccination",
        "Emergency",
        "Surgery",
        "Skin Care",
        "Allergy",
        "Grooming",
        "ICU",
        "Dental Care"

    )

    const val PAGE_SIZE = 10

    val APPOINTMENT_SLOTS = listOf(
        // ── Morning ───────────────────────────────────────────────────
        "08:00 AM",
        "08:30 AM",
        "09:00 AM",
        "09:30 AM",
        "10:00 AM",
        "10:30 AM",
        "11:00 AM",
        "11:30 AM",

        // ── Afternoon ─────────────────────────────────────────────────
        "12:00 PM",
        "12:30 PM",
        "01:00 PM",
        "01:30 PM",
        "02:00 PM",
        "02:30 PM",
        "03:00 PM",
        "03:30 PM",
        "04:00 PM",
        "04:30 PM",

        // ── Evening ───────────────────────────────────────────────────
        "05:00 PM",
        "05:30 PM",
        "06:00 PM",
        "06:30 PM",
        "07:00 PM"
    )
}