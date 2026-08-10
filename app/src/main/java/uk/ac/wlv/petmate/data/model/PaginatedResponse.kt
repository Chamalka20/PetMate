package uk.ac.wlv.petmate.data.model

data class PaginatedResponse<T>(
    val total   : Int,
    val page    : Int,
    val pageSize: Int,
    val data    : List<T>
)