package uk.ac.wlv.petmate.model

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)