package uk.ac.wlv.petmate.data.datasources.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import uk.ac.wlv.petmate.core.utils.Constants.PAGE_SIZE
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.SortOption
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.data.model.VetFilterState
import uk.ac.wlv.petmate.data.network.ApiClient
import kotlin.math.ceil

class VetRemoteDataSource @Inject constructor(

) {


    private var currentPage = 1
    var isLastPage = false
        private set
    private  val TAG = "VetRepo"




    suspend fun getVetsList(
        isRefresh   : Boolean        = false,
        filter      : VetFilterState = VetFilterState(),
        searchQuery : String         = ""
    ): List<Vet> {
        try {
        Log.d(TAG, "getVetsList called")
        Log.d(TAG, "isRefresh=$isRefresh | currentPage(before)=$currentPage | isLastPage=$isLastPage")

        if (isRefresh) {
            Log.d(TAG, "Refresh triggered -> resetting pagination")
            currentPage = 1
            isLastPage  = false
        }

        if (isLastPage) {
            Log.d(TAG, "Already at last page -> returning empty list")
            return emptyList()
        }

        val pageParam = currentPage
        val searchParam = searchQuery.ifBlank { null }
        val servicesParam = filter.selectedServices.ifEmpty { null }
        val minRatingParam = if (filter.minRating > 0f) filter.minRating else null
        val maxPriceParam = if (filter.maxPrice < 5000) filter.maxPrice else null
        val maxWaitingTimeParam = if (filter.maxWaitingTime < 60) filter.maxWaitingTime else null
        val sortByParam = if (filter.sortBy != SortOption.NONE) filter.sortBy.name else null

        Log.d(TAG, "API Params -> page=$pageParam, pageSize=$PAGE_SIZE")
        Log.d(TAG, "Filters -> search=$searchParam, services=$servicesParam, minRating=$minRatingParam, maxPrice=$maxPriceParam, maxWaitingTime=$maxWaitingTimeParam, sortBy=$sortByParam")

        val response = ApiClient.vetApi.getVets(
            page            = pageParam,
            pageSize        = PAGE_SIZE,
            searchQuery     = searchParam,
            services        = servicesParam,
            minRating       = minRatingParam,
            maxPrice        = maxPriceParam,
            maxWaitingTime  = maxWaitingTimeParam,
            sortBy          = sortByParam
        )

        Log.d(TAG, "API Response -> total=${response.total}, dataSize=${response.data.size}")

        val totalPages = ceil(response.total.toDouble() / PAGE_SIZE).toInt()
        Log.d(TAG, "Pagination -> totalPages=$totalPages, currentPage=$currentPage")

        isLastPage = currentPage >= totalPages
        Log.d(TAG, "isLastPage(after)=$isLastPage")

        if (!isLastPage) {
            currentPage++
            Log.d(TAG, "Next page incremented -> currentPage=$currentPage")
        } else {
            Log.d(TAG, "Reached last page")
        }

        return response.data

        } catch (e: Exception) {

            Log.e(
                "VetRepository",
                "getVets() failed: ${e.message}",
                e
            )

            throw e
        }
    }

    suspend fun getVet(vetId: Int): Vet {
        return ApiClient.vetApi.getVet(id =  vetId)
    }
}